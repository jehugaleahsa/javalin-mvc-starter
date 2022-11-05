package com.truncon.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.truncon.javalin.mvc.ControllerRegistry;
import com.truncon.javalin.mvc.JavalinControllerRegistry;
import com.truncon.javalin.mvc.api.Injector;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;
import io.javalin.json.JsonMapper;
import io.javalin.openapi.OpenApiInfo;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

public final class JavalinFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavalinFactory.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new ParameterNamesModule())
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    private final Provider<Injector> injectorProvider;

    @Inject
    public JavalinFactory(Provider<Injector> injectorProvider) {
        this.injectorProvider = injectorProvider;
    }

    public Javalin create() {
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            JsonMapper jsonMapper = new JavalinJackson(OBJECT_MAPPER);
            config.jsonMapper(jsonMapper);
            config.plugins.register(new OpenApiPlugin(getOpenApiOptions()));
            config.plugins.register(new SwaggerPlugin(new SwaggerConfiguration()));
            config.staticFiles.add("./public", Location.EXTERNAL);
            config.spaRoot.addFile("/", "./public/index.html", Location.EXTERNAL);
        });

        ControllerRegistry registry = new JavalinControllerRegistry(injectorProvider::get);
        registry.register(app);

        app.exception(Exception.class, (e, ctx) -> {
            LOGGER.error("Encountered an unhandled exception for HTTP.", e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        });
        app.wsException(
            Exception.class,
            (e, ctx) -> LOGGER.error("Encountered an unhandled exception for WebSockets.", e));

        return app;
    }

    private static OpenApiConfiguration getOpenApiOptions() {
        OpenApiConfiguration configuration = new OpenApiConfiguration();
        OpenApiInfo info = configuration.getInfo();
        info.setTitle("Web API Documentation");
        info.setVersion("1.0");
        return configuration;
    }
}
