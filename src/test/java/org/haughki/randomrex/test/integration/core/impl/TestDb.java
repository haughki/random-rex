package org.haughki.randomrex.test.integration.core.impl;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.TestContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class TestDb {
    public final static String TEST_DB_NAME = "random-rex-test";

    public static void setUp(final MongoClient mongoClient, TestContext context) throws Exception {
        mongoClient.find("nonces", new JsonObject(), context.asyncAssertSuccess(res -> {
            context.assertEquals(0, res.size()); // make sure the db is clean
        }));


        URL setupDbUrl = TestDb.class.getClassLoader().getResource("mongodb/setupdb.js");

        final String command = "mongo localhost:27017/test " + setupDbUrl.getPath();

        System.out.println(executeCommand(command));
    }

    public static void tearDown(final MongoClient mongoClient, TestContext context) throws Exception {
        // drop the test database to clean up for next run.  runs regardless of success/failure.
        JsonObject command = new JsonObject().put("dropDatabase", 1);
        mongoClient.runCommand("dropDatabase", command, context.asyncAssertSuccess());
    }

    private static String executeCommand(final String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
