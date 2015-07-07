package org.haughki.randomrex.core.impl;

import com.google.inject.Inject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.haughki.randomrex.core.NonceAccess;

public class NonceAccessImpl implements NonceAccess {

    final MongoClient mongoClient;
    final static String NONCES = "nonces";
    final static String NONCE = "nonce";

    @Inject
    public NonceAccessImpl(MongoClient mongoClient) {

        this.mongoClient = mongoClient;
    }

    @Override
    public void addNonce(String nonce) {
        JsonObject nonceObj = new JsonObject("{ \"" + NONCE + "\": \"" + nonce + "\" }");
        mongoClient.insert(NONCES, nonceObj, res -> {
        }); // noop lambda - don't care about the response unless there's an error in which case it should throw
    }

    @Override
    public String getNonce(String nonce) {
        return null;
    }

    @Override
    public void deleteNonce(String nonce) {

    }
}
