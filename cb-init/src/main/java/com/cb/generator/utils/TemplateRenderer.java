
package com.cb.generator.utils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ConcurrentReferenceHashMap;

public class TemplateRenderer {

	private static final Logger log = LoggerFactory.getLogger(TemplateRenderer.class);

	private boolean cache = true;

	private final Compiler mustache;
	private final ConcurrentMap<String, Template> templateCaches =
			new ConcurrentReferenceHashMap<>();

	public TemplateRenderer(Compiler mustache) {
		this.mustache = mustache;
	}

	public TemplateRenderer() {
		this(mustacheCompiler());
	}

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public String process(String name, Map<String, ?> model) {
		try {
			Template template = getTemplate(name);
			return template.execute(model);
		}
		catch (Exception e) {
			log.error("Cannot render: " + name, e);
			throw new IllegalStateException("Cannot render template", e);
		}
	}

	public Template getTemplate(String name) {
		if (cache) {
			return this.templateCaches.computeIfAbsent(name, this::loadTemplate);
		}
		return loadTemplate(name);
	}

	protected Template loadTemplate(String name) {
		try {
			Reader template;
			template = mustache.loader.getTemplate(name);
			return mustache.compile(template);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot load template " + name, e);
		}
	}

	private static Compiler mustacheCompiler() {
		return Mustache.compiler().withLoader(mustacheTemplateLoader());
	}

	private static TemplateLoader mustacheTemplateLoader() {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		String prefix = "classpath:/templates/";
		Charset charset = Charset.forName("UTF-8");
		return name -> new InputStreamReader(
				resourceLoader.getResource(prefix + name).getInputStream(), charset);
	}

}
