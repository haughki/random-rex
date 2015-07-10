package org.haughki.randomrex.test.integration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.haughki.randomrex.DependencyConfiguration;
import org.haughki.randomrex.ServerStart;
import org.haughki.randomrex.core.NonceAccess;
import org.haughki.randomrex.core.impl.DatabaseSetup;
import org.haughki.randomrex.core.impl.Nonce;
import org.haughki.randomrex.core.impl.NonceAccessImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class DbSmokeTest {

    private final Vertx vertx = Vertx.vertx();

    @Inject
    private NonceAccess nonceAccess;
    @Inject
    private MongoClient mongoClient;

    @Before
    public void setUp(TestContext context) throws Exception {
        Guice.createInjector(new DependencyConfiguration(vertx, ServerStart.DB_NAME)).injectMembers(this);
        DatabaseSetup.runDatabaseSetup(vertx, ServerStart.MONGO_URL, ServerStart.DB_NAME, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        vertx.close();
    }

    @Test
    public void testAddDuplicateNonceFailsACTUAL_DB(TestContext context) throws Exception {
        // Identical to previous test except that it tests against the ACTUAL db, to test correct config.
        // Note that this is really testing correct setup of the unique index on the nonce field
        // in the ACTUAL db, not the test db

        final String nonce = Nonce.nextNonce().toString();
        final JsonObject nonceObj = new JsonObject().put(NonceAccessImpl.NONCE_KEY, nonce);
        final JsonObject sameNonceObj = new JsonObject().put(NonceAccessImpl.NONCE_KEY, nonce);
        mongoClient.insert(NonceAccessImpl.NONCES_COLLECTION, nonceObj, context.asyncAssertSuccess(res -> {
            context.assertTrue(res.length() > 0);
            mongoClient.insert(NonceAccessImpl.NONCES_COLLECTION, sameNonceObj, context.asyncAssertFailure(throwable -> {
                // cleanup the previously created dummy test nonce
                JsonObject deleteQuery = new JsonObject().put(NonceAccessImpl.NONCE_KEY, nonce);
                mongoClient.remove(NonceAccessImpl.NONCES_COLLECTION, deleteQuery, context.asyncAssertSuccess(deleteResult -> {
                    context.assertNull(deleteResult);
                }));

                context.assertEquals(com.mongodb.MongoWriteException.class, throwable.getClass());
                context.assertNotEquals(-1, throwable.getMessage().indexOf("E11000 duplicate key error index"));
            }));
        }));
    }
}
