package org.haughki.randomrex.core.impl;

import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.haughki.randomrex.core.NonceAccess;

/**
 *
 */
public class NonceAccessImpl implements NonceAccess {

    private final MongoClient mongoClient;
    public final static String NONCES_COLLECTION = "nonces";
    public final static String NONCE_KEY = "nonce";
    public final static String CREATED_KEY = "created";
    public final static String EXPIRES_KEY = "expires";

    @Inject
    public NonceAccessImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    /**
     * Adds a nonce to the db, including created and expires times.
     * <p>
     * TODO implement real date support, and esp. support for mongo's TTL index
     * The Vert.x MongoClient doesn't seem to have straightforward support for DateTime objects yet.
     * The JsonObject throws when you try to add a Date.  The following has been merged as a fix:
     * https://github.com/vert-x3/vertx-mongo-client/commit/df3364f5171ae5f9769db3399cb89cda16be7e0d
     * But, it's not exactly obvious to me if this would support the TTL index, and
     * I just don't want to deal with figuring this out now.
     * For now, uses secs since epoch.
     * <p>
     * see also http://diabolicallab.com/store-dates-with-vertx-mongo-persistor/
     * http://docs.mongodb.org/manual/tutorial/expire-data/
     *
     * @param nonce a nonce string from Nonce.nextNonce()
     */
    @Override
    public void addNonce(final String nonce) {
        final long currSecs = System.currentTimeMillis() / 1000;
        JsonObject nonceObj = new JsonObject()
                .put(NONCE_KEY, nonce)
                .put(CREATED_KEY, currSecs)
                .put(EXPIRES_KEY, currSecs + 300);
        mongoClient.insert(NONCES_COLLECTION, nonceObj, res -> {
        }); // noop lambda - don't care about the response unless there's an error in which case it should throw
    }

    @Override
    public void getNonce(final String nonce, final Handler<AsyncResult<String>> handler) {
        long currSecs = System.currentTimeMillis() / 1000;

        JsonObject query = new JsonObject()
                .put(NONCE_KEY, nonce)
                .put(EXPIRES_KEY, new JsonObject()
                        .put("$gte", currSecs));

        mongoClient.find(NONCES_COLLECTION, query, res -> {
            if (res.succeeded()) {
                if (!res.result().isEmpty()) {
                    handler.handle(Future.succeededFuture(res.result().get(0).getString(NONCE_KEY)));
                } else
                    handler.handle(Future.succeededFuture(""));
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    // TODO implement
    @Override
    public void deleteNonce(final String nonce) {

    }
}
