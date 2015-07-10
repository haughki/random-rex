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
import org.haughki.randomrex.core.NonceManager;
import org.haughki.randomrex.core.impl.Nonce;
import org.haughki.randomrex.core.impl.NonceAccessImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class NonceManagerImplTest {
    private final Vertx vertx = Vertx.vertx();

    @Inject
    private NonceManager nonceManager;
    @Inject
    private MongoClient mongoClient;

    @Before
    public void setUp(TestContext context) throws Exception {
        Guice.createInjector(new DependencyConfiguration(vertx, TestDb.TEST_DB_NAME)).injectMembers(this);
        TestDb.setUp(vertx, mongoClient, context, context.asyncAssertSuccess());
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
    public void testIsNonceValid(TestContext context) throws Exception {
        final Async async = context.async();
        nonceManager.nextNonce(foundNonce -> {
            context.assertTrue(foundNonce.succeeded());
            nonceManager.isNonceValid(foundNonce.result(), isValidResult -> {
                context.assertTrue(isValidResult.result());
                async.complete();
            });
        });
    }

    @Test
    public void testExpiredNonceIsInvalid(TestContext context) throws Exception {
        final String nonce = Nonce.nextNonce().toString();
        final long currSecs = System.currentTimeMillis() / 1000;
        JsonObject nonceObj = new JsonObject()
                .put(NonceAccessImpl.NONCE_KEY, nonce)
                .put(NonceAccessImpl.CREATED_KEY, currSecs)
                .put(NonceAccessImpl.EXPIRES_KEY, currSecs - 1);  // add expired nonce

        final Async async = context.async();
        mongoClient.insert(NonceAccessImpl.NONCES_COLLECTION, nonceObj, res -> {
            nonceManager.isNonceValid(nonce, isValidResult -> {
                context.assertFalse(isValidResult.result());
                async.complete();
            });
        });
    }
}