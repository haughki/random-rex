package org.haughki.randomrex.impl;

import com.google.inject.Inject;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.client.utils.URIBuilder;
import org.haughki.randomrex.RequestHandlers;
import org.haughki.randomrex.core.NonceManager;

import java.net.URISyntaxException;

public class RequestHandlersImpl implements RequestHandlers {

    private final static String CLIENT_ID = "b5e9e5e62c814f8fabdfe65fd3b4005e";
    private final static String CLIENT_SECRET = "85fc615c16624b39bc9219d6f4b973a7";
    private final static String REDIRECT_URI = "http://localhost:8888/callback";
    private final static String STATE_KEY = "spotify_auth_state";

    private final NonceManager nonceManager;

    @Inject
    public RequestHandlersImpl(NonceManager nonceManager) {
        this.nonceManager = nonceManager;
    }

    @Override
    public void handleLogin(RoutingContext context) {

        // TODO: this whole "nonce state" thing seems fishy.  Why set a cookie
        // AND send it on the query string? Also seems like we'd need to at least remember
        // (persist) sent nonce's and verify against incoming reqs.  Really should
        // correlate with incoming next domain req (map key to nonce queue?)
        final String NONCE = nonceManager.nextNonce();
        context.addCookie(Cookie.cookie(STATE_KEY, NONCE));

        // your application requests authorization
        //var scope = 'user-read-private user-read-email';  // may need one or more of these
        final String SCOPE = "user-library-read";
        //context.response().setStatusCode(302);  // Found.  Might work better...?
        context.response().setStatusCode(303);
        context.response().setStatusMessage("See Other");

        URIBuilder spotifyAuthUri = null;
        try {
            spotifyAuthUri = new URIBuilder("https://accounts.spotify.com/authorize");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            sendError(500, context.response());
        }
        if (spotifyAuthUri != null) {
            spotifyAuthUri.addParameter("response_type", "code");
            spotifyAuthUri.addParameter("client_id", CLIENT_ID);
            spotifyAuthUri.addParameter("scope", SCOPE);
            spotifyAuthUri.addParameter("redirect_uri", REDIRECT_URI);
            spotifyAuthUri.addParameter("state", NONCE);

            context.response().putHeader("Location", spotifyAuthUri.toString());

            context.response().end();
        }
    }

    @Override
    public void handleCallback(RoutingContext routingContext) {

    }

    @Override
    public void handleRefreshToken(RoutingContext routingContext) {

    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
