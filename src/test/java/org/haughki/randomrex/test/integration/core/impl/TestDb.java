package org.haughki.randomrex.test.integration.core.impl;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.TestContext;

public class TestDb {
    public final static String TEST_DB_NAME = "random-rex-test";

    public static void setUp(final MongoClient mongoClient, TestContext context) throws Exception {
        mongoClient.find("nonces", new JsonObject(), context.asyncAssertSuccess(res -> {
            context.assertEquals(0, res.size()); // make sure the db is clean
        }));
    }

    public static void tearDown(final MongoClient mongoClient, TestContext context) throws Exception {
        // drop the test database to clean up for next run.  runs regardless of success/failure.
        JsonObject command = new JsonObject().put("dropDatabase", 1);
        mongoClient.runCommand("dropDatabase", command, res -> {
        });
    }
}
