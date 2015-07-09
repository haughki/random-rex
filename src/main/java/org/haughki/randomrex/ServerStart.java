/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package org.haughki.randomrex;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.haughki.randomrex.util.IdAndWorkingDir;
import org.haughki.randomrex.util.Runner;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="parker.hawkeye@gmail.com">Hawkeye Parker</a>. Origin from
 *         sample code by <a href="http://tfox.org">Tim Fox</a>.
 */
public class ServerStart extends AbstractVerticle {

    public static final int PORT = 8888;
    private static final String HOST = "localhost";

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Runner runner = new Runner(new IdAndWorkingDir(ServerStart.class));
        runner.run();
    }

    private Map<String, JsonObject> products = new HashMap<>();

    @Inject
    private RequestHandlers handlers;

    @Override
    public void start() {
        System.out.println("Starting server...");
        System.out.println("user.dir:" + System.getProperty("user.dir"));

        URL one = this.getClass().getClassLoader().getResource("mongodb/");
        URL two = this.getClass().getClassLoader().getResource("mongodb/setupdb.js");
        Guice.createInjector(new DependencyConfiguration(vertx, "random-rex")).injectMembers(this);

        Router router = Router.router(vertx);

        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.get("/login").handler(handlers::handleLogin);
        router.put("/callback").handler(handlers::handleCallback);
        router.get("/refreshToken").handler(handlers::handleRefreshToken);

        router.route().handler(StaticHandler.create());  // defaults to webroot

        vertx.createHttpServer().requestHandler(router::accept)
                .listen(PORT, "localhost", res -> {
                    if (res.succeeded()) {
                        System.out.println("Server started at " + HOST + ":" + PORT);
                    } else {
                        System.out.println("ERROR: Server failed to start!");
                    }
                });
    }
}
