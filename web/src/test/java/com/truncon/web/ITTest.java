package com.truncon.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.junit.jupiter.api.BeforeEach;

public abstract class ITTest {
    private static final Injector appInjector = Guice.createInjector(
        Modules.override(new AppModule()).with(new ITModule())
    );
    private Injector eachInjector;

    protected ITTest() {
    }

    protected <T> T getInstance(Class<T> clz) {
        return eachInjector.getInstance(clz);
    }

    @BeforeEach
    protected void before() {
        this.eachInjector = appInjector.createChildInjector(new RequestModule());
    }
}
