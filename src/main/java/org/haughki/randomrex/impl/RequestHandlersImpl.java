package org.haughki.randomrex.impl;

import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.haughki.randomrex.RequestHandlers;
import org.haughki.randomrex.ServerStart;
import org.haughki.randomrex.core.NonceManager;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RequestHandlersImpl implements RequestHandlers {

    private final static String CLIENT_ID = "b5e9e5e62c814f8fabdfe65fd3b4005e";
    private final static String CLIENT_SECRET = "85fc615c16624b39bc9219d6f4b973a7";
    private final static String REDIRECT_URI = "http://localhost:" + ServerStart.PORT + "/callback";
    private final static String COOKIE_STATE_KEY = "spotify_auth_state";

    private final NonceManager nonceManager;
    private final Vertx vertx;

    @Inject
    public RequestHandlersImpl(Vertx vertx, NonceManager nonceManager) {
        this.vertx = vertx;
        this.nonceManager = nonceManager;
    }

    @Override
    public void handleLogin(final RoutingContext context) {


        nonceManager.nextNonce(newNonce -> {
            if (!newNonce.succeeded()) {
                // TODO !! log error !!
                sendError(500, context.response());
                return;
            }

            // set the nonce in a cookie AND in a query param sent to spotify.  The cookie will get
            // set on the client but the query param will be reflected from spotify.  When we
            // receive the callback, we compare the cookie and query param nonces to make sure they match.
            final String nonce = newNonce.result();
            context.addCookie(Cookie.cookie(COOKIE_STATE_KEY, nonce));

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
                spotifyAuthUri.addParameter("state", nonce);

                context.response().putHeader("Location", spotifyAuthUri.toString());

                context.response().end();
            }
        });
    }

    @Override
    public void handleCallback(final RoutingContext routingContext) {
        final HttpServerRequest request = routingContext.request();
        final HttpServerResponse response = routingContext.response();

        final String authCode = request.getParam("code");
        if (isEmptySendBadRequest(authCode, response)) return;

        final Cookie nonceCookie = routingContext.getCookie(COOKIE_STATE_KEY);
        if (nonceCookie == null) {
            sendError(400, response);
            return;
        }

        final String nonceFromCookie = nonceCookie.getValue();
        if (isEmptySendBadRequest(nonceFromCookie, response)) return;

        final String nonce = request.getParam("state");
        if (nonceIsIncorrect(nonce, nonceFromCookie, response)) return;

        nonceManager.isNonceValid(nonce, nonceIsValid -> {
            if (!nonceIsValid.succeeded() || !nonceIsValid.result()) {
                // TODO !! log error !! -- for all the error cases below too
                sendError(400, response);
                return;
            }
            routingContext.removeCookie(COOKIE_STATE_KEY);

            HttpClient client = vertx.createHttpClient(new HttpClientOptions().setSsl(true));
            HttpClientRequest tokenRequest = client.post(443, "accounts.spotify.com", "/api/token", tokenResponse -> {
                System.out.println("Received token response with status code " + tokenResponse.statusCode());
                tokenResponse.bodyHandler(bodyBuffer -> System.out.println(bodyBuffer.toString("UTF-8")));
                response.end();
            });


            String authHeader = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
            tokenRequest.putHeader("Authorization", "Basic " + authHeader);
            tokenRequest.putHeader("Accept", "application/json");
            tokenRequest.putHeader("Connection", "keep-alive");
            tokenRequest.putHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

            List<NameValuePair> formParams = new ArrayList<>();
            formParams.add(new BasicNameValuePair("code", authCode));
            formParams.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
            formParams.add(new BasicNameValuePair("grant_type", "authorization_code"));

            final String bodyParams = URLEncodedUtils.format(formParams, "UTF-8");
            tokenRequest.putHeader("Content-Length", Integer.toString(bodyParams.length()));
            tokenRequest.write(bodyParams);

            tokenRequest.end();
        });
    }

    private boolean isEmptySendBadRequest(final String required, final HttpServerResponse response) {
        if (StringUtils.isBlank(required)) {
            sendError(400, response);
            return true;
        }
        return false;
    }

    private boolean nonceIsIncorrect(final String nonce, final String nonceFromCookie, final HttpServerResponse response) {
        if (isEmptySendBadRequest(nonce, response)) {
            return true;
        } else if (!nonce.equals(nonceFromCookie)) {  // one nonce came from spotify (via the redirect to /callback)
            sendError(400, response);                 // the other comes from a cookie from the client.  they should
            return true;                              // be the same!
        }
        return false;
    }


    @Override
    public void handleRefreshToken(final RoutingContext routingContext) {

    }

    private void sendError(final int statusCode, final HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
