package com.truncon.web;

import com.truncon.javalin.mvc.api.Injector;

import javax.inject.Inject;

public final class GuiceInjector implements Injector {
    private final com.google.inject.Injector injector;

    @Inject
    public GuiceInjector(com.google.inject.Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T> T getInstance(Class<T> clz) {
        return injector.getInstance(clz);
    }

    @Override
    public Object getHandle() {
        return injector;
    }
};
