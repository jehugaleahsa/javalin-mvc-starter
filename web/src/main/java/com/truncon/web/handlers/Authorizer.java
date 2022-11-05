package com.truncon.web.handlers;

import com.truncon.javalin.mvc.api.BeforeActionContext;
import com.truncon.javalin.mvc.api.BeforeActionHandler;
import com.truncon.javalin.mvc.api.HttpContext;
import io.javalin.http.Context;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.util.FindBest;
import org.pac4j.javalin.JavalinHttpActionAdapter;
import org.pac4j.javalin.JavalinWebContext;
import org.pac4j.jee.context.session.JEESessionStoreFactory;

import javax.inject.Inject;

public final class Authorizer implements BeforeActionHandler {
    private final Config config;

    @Inject
    public Authorizer(Config config) {
        this.config = config;
    }

    @Override
    public void executeBefore(BeforeActionContext beforeContext) {
        HttpContext httpContext = beforeContext.getHttpContext();
        HttpActionAdapter adapter = FindBest.httpActionAdapter(null, this.config, JavalinHttpActionAdapter.INSTANCE);
        SecurityLogic bestLogic = FindBest.securityLogic(null, this.config, DefaultSecurityLogic.INSTANCE);

        JavalinWebContext context = new JavalinWebContext((Context) httpContext.getHandle());
        Object result = bestLogic.perform(
            context,
            JEESessionStoreFactory.INSTANCE.newSessionStore(),
            this.config,
            (ctx, store, profiles, parameters) -> new Object(),
            adapter,
            "FormClient",
            null,
            null);
        beforeContext.setCancelled(result != null);
    }
}
