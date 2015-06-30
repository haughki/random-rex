package org.haughki.randomrex;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class TestHttpServer extends Verticle {

    public void start() {
        System.out.println("Starting HTTP...");

        final JsonObject appConfig = container.config(); // loaded from -config on command line
        container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", appConfig.getObject("mongo-persistor"));

        final RouteMatcher matcher = new RouteMatcher();

        // the matcher for the complete list and the search
        matcher.get("/zips", new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest req) {
                System.out.println("request: " + req.absoluteURI());

                JsonObject json;
                MultiMap params = req.params();

                if (params.size() > 0 && params.contains("state") || params.contains("city")) {
                    // create the matcher configuration
                    JsonObject matcher = new JsonObject();
                    if (params.contains("state")) matcher.putString("state", params.get("state"));
                    if (params.contains("city")) matcher.putString("city", params.get("city"));

                    // create the message for the mongo-persistor verticle
                    json = new JsonObject().putString("collection", "zips")
                            .putString("action", "find")
                            .putObject("matcher", matcher);

                } else {
                    // create the query
                    json = new JsonObject().putString("collection", "zips")
                            .putString("action", "find")
                            .putObject("matcher", new JsonObject());
                }

                JsonObject data = new JsonObject();
                data.putArray("results", new JsonArray());
                // and call the event we want to use
                vertx.eventBus().send("mongodb-persistor", json, new ReplyHandler(req, data));
            }
        });

        matcher.post("/zips/:id", new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest req) {
                // process the body
                req.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer event) {
                        // normally we'd validate the input, for now just assume it is correct.
                        final String body = event.getString(0, event.length());

                        // create the query
                        JsonObject newObject = new JsonObject(body);
                        JsonObject matcher = new JsonObject().putString("_id", req.params().get("id"));
                        JsonObject json = new JsonObject().putString("collection", "zips")
                                .putString("action", "update")
                                .putObject("criteria", matcher)
                                .putBoolean("upsert", false)
                                .putBoolean("multi", false)
                                .putObject("objNew", newObject);

                        // and call the event we want to use
                        vertx.eventBus().send("mongodb-persistor", json, new Handler<Message<JsonObject>>() {
                            @Override
                            public void handle(Message<JsonObject> event) {
                                // we could handle the errors here, but for now assume everything went ok, and return the original and updated json
                                req.response().end(body);
                            }
                        });
                    }
                });
            }
        });

        vertx.createHttpServer().requestHandler(matcher)
                .listen(8989, "localhost", new AsyncResultHandler<HttpServer>() {
                    @Override
                    public void handle(AsyncResult<HttpServer> asyncResult) {
                        System.out.println("Listening? --> " + asyncResult.succeeded());
                    }
                });
    }

    private static class ReplyHandler implements Handler<Message<JsonObject>> {

        private final HttpServerRequest request;
        private JsonObject data;

        private ReplyHandler(final HttpServerRequest request, JsonObject data) {
            this.request = request;
            this.data = data;
        }

        @Override
        public void handle(Message<JsonObject> event) {
            // if the response contains more message, we need to get the rest
            if (event.body().getString("status").equals("more-exist")) {
                JsonArray results = event.body().getArray("results");

                for (Object el : results) {
                    data.getArray("results").add(el);
                }

                event.reply(new JsonObject(), new ReplyHandler(request, data));
            } else {

                JsonArray results = event.body().getArray("results");
                for (Object el : results) {
                    data.getArray("results").add(el);
                }

                request.response().putHeader("Content-Type", "application/json");
                request.response().end(data.encodePrettily());
            }
        }
    }
}