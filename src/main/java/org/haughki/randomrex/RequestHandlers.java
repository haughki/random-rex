package org.haughki.randomrex;

import io.vertx.ext.web.RoutingContext;


public interface RequestHandlers {
    void handleLogin(final RoutingContext routingContext);

    void handleCallback(final RoutingContext routingContext);

    void handleRefreshToken(final RoutingContext routingContext);
}
