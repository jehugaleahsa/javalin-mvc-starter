package com.truncon.web;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.truncon.settings.DbSettings;
import com.truncon.web.settings.HoconSettings;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ITModule extends AbstractModule {
    @Override
    protected void configure() {
        super.configure();
        Path testConfigPath = Paths.get("config", "application-test.conf");
        bind(DbSettings.class)
            .toProvider(() -> HoconSettings.newInstance(testConfigPath))
            .in(Scopes.SINGLETON);
    }
}
