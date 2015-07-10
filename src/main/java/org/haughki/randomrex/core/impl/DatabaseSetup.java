package org.haughki.randomrex.core.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class DatabaseSetup {

    public static void runDatabaseSetup(final Vertx vertx,
                                        final String mongoUrl,
                                        final String dbName,
                                        final Handler<AsyncResult<Object>> handler) {
        final String setupDbScriptPath = "mongodb/setupdb.js";

        URL setupDbUrl = DatabaseSetup.class.getClassLoader().getResource(setupDbScriptPath);
        if (setupDbUrl == null) {
            handler.handle(Future.failedFuture(new IllegalStateException(setupDbScriptPath + " was not found during DB setup.")));
        } else {
            final String command = "mongo " + mongoUrl + "/" + dbName + " " + setupDbUrl.getPath();
            vertx.executeBlocking(future -> {
                try {
                    System.out.println("Executing shell command: " + command);
                    System.out.println("Command output: " + executeSystemCommand(command));
                } catch (IOException | InterruptedException e) {
                    future.fail(e);
                }
                future.complete();
            }, res -> handler.handle(res));
        }
    }

    /***
     * CONTAINS BLOCKING CODE!!
     ***/
    private static String executeSystemCommand(final String command) throws IOException, InterruptedException {
        StringBuffer output = new StringBuffer();
        Process p;
        p = Runtime.getRuntime().exec(command);
        p.waitFor();  // !! BLOCKING  !!
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        }

        return output.toString();
    }
}
