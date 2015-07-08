package org.haughki.randomrex.test.integration.core.impl;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
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
        TestDb.setUp(mongoClient, context);
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        TestDb.tearDown(mongoClient, context);
    }

    @Test
    public void testGetNonce(TestContext context) throws Exception {
        final String nonce = Nonce.nextNonce().toString();
        nonceAccess.addNonce(nonce);
        nonceAccess.getNonce(nonce, foundNonce -> {
            context.assertEquals(nonce, foundNonce);
        });
    }

    @Test
    public void testDontGetExpiredNonce(TestContext context) throws Exception {
        final String nonce = Nonce.nextNonce().toString();
        final long currSecs = System.currentTimeMillis() / 1000;
        JsonObject nonceObj = new JsonObject()
                .put(NonceAccessImpl.NONCE, nonce)
                .put(NonceAccessImpl.CREATED, currSecs)
                .put(NonceAccessImpl.EXPIRES, currSecs - 1);  // add expired nonce
        mongoClient.insert(NonceAccessImpl.NONCES, nonceObj, res -> {
        });

        nonceAccess.getNonce(nonce, context.asyncAssertSuccess(res -> {
            context.assertEquals("", res);
        }));
    }

    @Test
    public void testDeleteNonce(TestContext context) throws Exception {

    }
}