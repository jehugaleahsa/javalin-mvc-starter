package com.truncon.web.controllers.api;

import com.truncon.javalin.mvc.api.ActionResult;
import com.truncon.javalin.mvc.api.ContentResult;
import com.truncon.javalin.mvc.api.Controller;
import com.truncon.javalin.mvc.api.HttpGet;

import javax.inject.Inject;

@Controller(prefix = "/api/home")
public final class HomeController {

    @Inject
    public HomeController() {
    }

    public static final String HOME_ROUTE = "";
    @HttpGet(route = HOME_ROUTE)
    public ActionResult getHome() {
        return new ContentResult("Hello, world!!!");
    }
}
