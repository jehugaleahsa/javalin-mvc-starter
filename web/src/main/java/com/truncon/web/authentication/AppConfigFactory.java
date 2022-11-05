package com.truncon.web.authentication;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.http.client.indirect.FormClient;

import javax.inject.Inject;
import javax.inject.Provider;

public final class AppConfigFactory implements ConfigFactory {
    private final Provider<FormAuthenticator> formAuthenticatorProvider;

    @Inject
    public AppConfigFactory(Provider<FormAuthenticator> formAuthenticatorProvider) {
        this.formAuthenticatorProvider = formAuthenticatorProvider;
    }

    @Override
    public Config build(Object... objects) {
        // HTTP/Basic Authentication
        FormAuthenticator formAuthenticator = formAuthenticatorProvider.get();
        FormClient formClient = new FormClient("/login", formAuthenticator);
        formClient.setUsernameParameter("username");
        formClient.setPasswordParameter("password");

        Clients clients = new Clients("/login/callback", formClient);
        clients.setAjaxRequestResolver(new DefaultAjaxRequestResolver());

        return new Config(clients);
    }
}
