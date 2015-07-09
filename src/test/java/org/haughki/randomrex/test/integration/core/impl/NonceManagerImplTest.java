package org.haughki.randomrex.test.integration.core.impl;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
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
    //private final static String TEST_DB = "random-rex-test";

    @Inject
    private NonceManager nonceManager;
    @Inject
    private MongoClient mongoClient;

    @Before
    public void setUp(TestContext context) throws Exception {
        Guice.createInjector(new DependencyConfiguration(vertx, "random-rex-test")).injectMembers(this);

        mongoClient.find("nonces", new JsonObject(), context.asyncAssertSuccess(res -> {
            context.assertEquals(0, res.size()); // make sure the db is clean
        }));
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        // drop the test database to clean up for next run.  runs regardless of success/failure.
        JsonObject command = new JsonObject().put("dropDatabase", 1);
        mongoClient.runCommand("dropDatabase", command, res -> {
        });
    }

    @Test
    public void testIsNonceValid(TestContext context) throws Exception {
        final String nonce = nonceManager.nextNonce();
        nonceManager.isNonceValid(nonce, context.asyncAssertSuccess(isValid -> {
            context.assertTrue(isValid);
        }));
    }

    @Test
    public void testExpiredNonceIsInvalid(TestContext context) throws Exception {
        final String nonce = Nonce.nextNonce().toString();
        final long currSecs = System.currentTimeMillis() / 1000;
        JsonObject nonceObj = new JsonObject()
                .put(NonceAccessImpl.NONCE_KEY, nonce)
                .put(NonceAccessImpl.CREATED_KEY, currSecs)
                .put(NonceAccessImpl.EXPIRES_KEY, currSecs - 1);  // add expired nonce
        mongoClient.insert(NonceAccessImpl.NONCES_COLLECTION, nonceObj, res -> {
        });

        nonceManager.isNonceValid(nonce, context.asyncAssertSuccess(isValid -> {
            context.assertFalse(isValid);
        }));
    }
}