package com.truncon.web.authentication;

import org.pac4j.core.config.Config;

import javax.inject.Inject;
import javax.inject.Provider;

public final class AppConfigProvider implements Provider<Config> {
    private final AppConfigFactory factory;

    @Inject
    public AppConfigProvider(AppConfigFactory factory) {
        this.factory = factory;
    }

    @Override
    public Config get() {
        return factory.build();
    }
}
