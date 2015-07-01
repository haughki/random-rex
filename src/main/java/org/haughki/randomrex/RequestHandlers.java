package org.haughki.randomrex;

import io.vertx.ext.web.RoutingContext;


public interface RequestHandlers {
    void handleLogin(RoutingContext routingContext);

    void handleCallback(RoutingContext routingContext);

    void handleRefreshToken(RoutingContext routingContext);
}
