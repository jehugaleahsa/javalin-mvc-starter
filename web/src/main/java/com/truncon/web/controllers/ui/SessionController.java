package com.truncon.web.controllers.ui;

import com.truncon.auditing.AuditContext;
import com.truncon.javalin.mvc.api.ActionResult;
import com.truncon.javalin.mvc.api.Controller;
import com.truncon.javalin.mvc.api.HttpContext;
import com.truncon.javalin.mvc.api.HttpGet;
import com.truncon.javalin.mvc.api.HttpPost;
import com.truncon.javalin.mvc.api.StreamResult;
import io.javalin.http.Context;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.util.FindBest;
import org.pac4j.javalin.JavalinHttpActionAdapter;
import org.pac4j.javalin.JavalinWebContext;
import org.pac4j.jee.context.session.JEESessionStore;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public final class SessionController {
    private final Config config;
    private final AuditContext auditContext;

    @Inject
    public SessionController(Config config, AuditContext auditContext) {
        this.config = config;
        this.auditContext = auditContext;
    }

    public static final String GET_LOGIN_ROUTE = "login";
    @HttpGet(route = GET_LOGIN_ROUTE)
    public ActionResult getLoginPage() throws IOException {
        InputStream html = Files.newInputStream(Paths.get("./public/login.html"));
        return new StreamResult(html, "text/html");
    }

    public static final String POST_LOGIN_CALLBACK_ROUTE = "login/callback";
    @HttpPost(route = POST_LOGIN_CALLBACK_ROUTE)
    public void postLoginCallback(HttpContext context) {
        SessionStore sessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        HttpActionAdapter adapter = FindBest.httpActionAdapter(null, config, JavalinHttpActionAdapter.INSTANCE);
        CallbackLogic callbackLogic = FindBest.callbackLogic(null, config, DefaultCallbackLogic.INSTANCE);

        JavalinWebContext webContext = new JavalinWebContext((Context) context.getHandle());
        callbackLogic.perform(webContext, sessionStore, config, adapter, "/", true, "FormClient");
    }

    public static final String POST_LOGOUT_USER_ROUTE = "logout";
    @HttpPost(route = POST_LOGOUT_USER_ROUTE)
    public void postLogoutUser(HttpContext context) {
        SessionStore sessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        HttpActionAdapter adapter = FindBest.httpActionAdapter(null, config, JavalinHttpActionAdapter.INSTANCE);
        LogoutLogic callbackLogic = FindBest.logoutLogic(null, config, DefaultLogoutLogic.INSTANCE);

        JavalinWebContext webContext = new JavalinWebContext((Context) context.getHandle());
        callbackLogic.perform(webContext, sessionStore, config, adapter, "/", null, true, true, false);
        auditContext.setUser(null);
    }
}
