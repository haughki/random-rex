package org.haughki.randomrex.test.integration.core.impl;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.haughki.randomrex.DependencyConfiguration;
import org.haughki.randomrex.core.NonceAccess;
import org.haughki.randomrex.core.impl.Nonce;
import org.haughki.randomrex.core.impl.NonceAccessImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class NonceAccessImplTest {

    private final Vertx vertx = Vertx.vertx();

    @Inject
    private NonceAccess nonceAccess;
    @Inject
    private MongoClient mongoClient;

    @Before
    public void setUp(TestContext context) throws Exception {
        Guice.createInjector(new DependencyConfiguration(vertx, TestDb.TEST_DB_NAME)).injectMembers(this);
        TestDb.setUp(vertx, mongoClient, context, context.asyncAssertSuccess());
/*
        Async async = context.async();
        async.complete();
*/
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        TestDb.tearDown(mongoClient, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                vertx.close();
            }
        });
    }

    @Test
    public void testGetNonce(TestContext context) throws Exception {
        final String nonce = Nonce.nextNonce().toString();
        Async async = context.async();
        nonceAccess.addNonce(nonce, addResult -> {
            nonceAccess.findNonce(nonce, foundNonce -> {
                context.assertEquals(nonce, foundNonce.result());
                async.complete();
            });
        });
    }

    @Test
    public void testDontGetExpiredNonce(TestContext context) throws Exception {
        final String nonce = Nonce.nextNonce().toString();
        final long currSecs = System.currentTimeMillis() / 1000;
        JsonObject nonceObj = new JsonObject()
                .put(NonceAccessImpl.NONCE_KEY, nonce)
                .put(NonceAccessImpl.CREATED_KEY, currSecs)
                .put(NonceAccessImpl.EXPIRES_KEY, currSecs - 1);  // add expired nonce
        Async async = context.async();
        mongoClient.insert(NonceAccessImpl.NONCES_COLLECTION, nonceObj, res -> {
            nonceAccess.findNonce(nonce, foundNonce -> {
                context.assertEquals("", foundNonce.result());
                async.complete();
            });
        });
    }

    @Test
    public void testDeleteNonce(TestContext context) throws Exception {
        final String nonce = Nonce.nextNonce().toString();
        Async async = context.async();
        nonceAccess.addNonce(nonce, addResult -> {
            context.assertTrue(addResult.succeeded());
            nonceAccess.deleteNonce(nonce, deleteResult -> {
                context.assertTrue(deleteResult.succeeded());
                nonceAccess.findNonce(nonce, foundNonce -> {
                    context.assertEquals("", foundNonce.result());
                    async.complete();
                });
            });
        });
    }

    @Test
    public void testAddDuplicateNonceFails(TestContext context) throws Exception {
        // Note that this is really testing correct setup of the unique index on the nonce field
        // in the db
        final String nonce = Nonce.nextNonce().toString();
        final JsonObject nonceObj = new JsonObject().put(NonceAccessImpl.NONCE_KEY, nonce);
        final JsonObject sameNonceObj = new JsonObject().put(NonceAccessImpl.NONCE_KEY, nonce);
        Async async = context.async();
        mongoClient.insert(NonceAccessImpl.NONCES_COLLECTION, nonceObj, res -> {
                    context.assertTrue(res.result().length() > 0);
                    mongoClient.insert(NonceAccessImpl.NONCES_COLLECTION, sameNonceObj, context.asyncAssertFailure(throwable -> {
                        context.assertEquals(com.mongodb.MongoWriteException.class, throwable.getClass());
                        context.assertNotEquals(-1, throwable.getMessage().indexOf("E11000 duplicate key error index"));
                        async.complete();
                    }));
                }
        );
    }
}