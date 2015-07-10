package org.haughki.randomrex.test.integration.core.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.TestContext;
import org.haughki.randomrex.ServerStart;
import org.haughki.randomrex.core.impl.DatabaseSetup;

public class TestDb {
    public final static String TEST_DB_NAME = "random-rex-test";

    public static void setUp(final Vertx vertx,
                             final MongoClient mongoClient,
                             final TestContext context,
                             final Handler<AsyncResult<Object>> handler) throws Exception {
        // look for any value in the nonces collection

        mongoClient.find("nonces", new JsonObject(), res -> {
            context.assertEquals(0, res.result().size());

                /*context.asyncAssertSuccess(res -> {
            context.assertEquals(0, res.size());*/


            DatabaseSetup.runDatabaseSetup(vertx, ServerStart.MONGO_URL, TEST_DB_NAME, handler);

        });
    }

    public static void tearDown(final MongoClient mongoClient, Handler<AsyncResult<JsonObject>> handler) throws Exception {
        // drop the test database to clean up for next run.  runs regardless of success/failure.
        JsonObject command = new JsonObject().put("dropDatabase", 1);
        mongoClient.runCommand("dropDatabase", command, res -> handler.handle(res));
    }
}
