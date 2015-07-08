package org.haughki.randomrex.core.impl;

import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.haughki.randomrex.core.NonceAccess;

public class NonceAccessImpl implements NonceAccess {

    private final MongoClient mongoClient;
    public final static String NONCES = "nonces";
    public final static String NONCE = "nonce";
    public final static String CREATED = "created";
    public final static String EXPIRES = "expires";

    @Inject
    public NonceAccessImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void addNonce(final String nonce) {
        final long currSecs = System.currentTimeMillis() / 1000;
        JsonObject nonceObj = new JsonObject()
                .put(NONCE, nonce)
                .put(CREATED, currSecs)
                .put(EXPIRES, currSecs + 300);
        mongoClient.insert(NONCES, nonceObj, res -> {
        }); // noop lambda - don't care about the response unless there's an error in which case it should throw
    }

    @Override
    public void getNonce(final String nonce, final Handler<AsyncResult<String>> handler) {
        long currSecs = System.currentTimeMillis() / 1000;

        JsonObject query = new JsonObject()
                .put(NONCE, nonce)
                .put(EXPIRES, new JsonObject()
                        .put("$gte", currSecs));

        mongoClient.find(NONCES, query, res -> {
            if (res.succeeded()) {
                if (!res.result().isEmpty()) {
                    handler.handle(Future.succeededFuture(res.result().get(0).getString(NONCE)));
                } else
                    handler.handle(Future.succeededFuture(""));
            } else {
                res.cause().printStackTrace();  // TODO !! real exception handling !!
            }
        });
    }

    @Override
    public void deleteNonce(final String nonce) {

    }
}
