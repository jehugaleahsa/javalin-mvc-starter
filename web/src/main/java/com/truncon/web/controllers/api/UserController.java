package com.truncon.web.controllers.api;

import com.truncon.javalin.mvc.api.ActionResult;
import com.truncon.javalin.mvc.api.Before;
import com.truncon.javalin.mvc.api.ContentResult;
import com.truncon.javalin.mvc.api.Controller;
import com.truncon.javalin.mvc.api.HttpGet;
import com.truncon.web.handlers.Authorizer;

@Controller(prefix = "/api/users")
public final class UserController {
    public static final String GET_CURRENT_USER_ROUTE = "";
    @HttpGet(route = GET_CURRENT_USER_ROUTE)
    @Before(handler = Authorizer.class)
    public ActionResult getCurrentUser() {
        return new ContentResult("Hello, authenticated user!");
    }
}
