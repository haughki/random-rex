package org.haughki.randomrex.impl;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.haughki.randomrex.RequestHandlers;

public class RequestHandlersImpl implements RequestHandlers {

    private final static String CLIENT_ID = 'b5e9e5e62c814f8fabdfe65fd3b4005e';
    private final static String CLIENT_SECRET = '85fc615c16624b39bc9219d6f4b973a7';
    private final static String REDIRECT_URI = 'http://localhost:8888/callback';
    private final static String STATE_KEY = "spotify_auth_state";

    @Override
    public void handleLogin(RoutingContext context) {

        //app.get('/login', function (req, res) {

        final String nonce = getNonce();
        context.addCookie(Cookie.cookie(STATE_KEY, nonce));

        //res.cookie(stateKey, state);

        // your application requests authorization
        //var scope = 'user-read-private user-read-email';
        final String scope = "user-library-read";
        //context.response().
        res.redirect("https://accounts.spotify.com/authorize?" +
                querystring.stringify({
                        response_type:"code",
                client_id:client_id,
                scope:scope,
                redirect_uri:redirect_uri,
                state:state
        }));
    }

    );

    //routingContext.response().putHeader("content-type", "text/html").end("Hello World!");
}

    @Override
    public void handleCallback(RoutingContext routingContext) {

    }

    @Override
    public void handleRefreshToken(RoutingContext routingContext) {

    }

    /**
     * Get a nonce for an OAuth request.  OAuth defines the nonce as "a random
     * string, uniquely generated for each request. The nonce allows the Service
     * Provider to verify that a request has never been made before and helps
     * prevent replay attacks when requests are made over a non-secure channel
     * (such as HTTP)."
     *
     * @return the nonce string to use in an OAuth request
     */
    private static String getNonce() {
        return Long.toString(System.nanoTime());
    }
}
