package com.truncon.web;

import com.google.inject.Injector;

import javax.inject.Inject;
import javax.inject.Provider;

public final class RequestInjectorProvider implements Provider<GuiceInjector> {
    private final Injector appInjector;
    private final RequestModule module = new RequestModule();

    @Inject
    public RequestInjectorProvider(Injector appInjector) {
        this.appInjector = appInjector;
    }

    @Override
    public GuiceInjector get() {
        Injector requestInjector = appInjector.createChildInjector(module);
        return new GuiceInjector(requestInjector);
    }
}
