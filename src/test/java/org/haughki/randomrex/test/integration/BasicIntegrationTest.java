package org.haughki.randomrex.test.integration;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.haughki.randomrex.HttpEntryPoint;
import org.haughki.randomrex.util.IdAndWorkingDir;
import org.haughki.randomrex.util.Runner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * Example of an asynchronous unit test written in JUnit style using vertx-unit
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@RunWith(VertxUnitRunner.class)
public class BasicIntegrationTest {

    Vertx vertx;
    HttpServer server;

    @Before
    public void before(TestContext context) {

        final IdAndWorkingDir builder = new IdAndWorkingDir(HttpEntryPoint.class);
        vertx = Vertx.vertx();
        Runner runner = new Runner(builder, context.asyncAssertSuccess(), vertx);
        runner.run();
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test1(TestContext context) {
        // Send a request and get a response
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        client.getNow(HttpEntryPoint.PORT, "localhost", "/", resp -> {
            resp.bodyHandler(body -> {
                String htmlString = body.toString("UTF-8");
                context.assertNotEquals(-1, htmlString.indexOf("<title>Example of the Authorization Code flow"));
                client.close();
                async.complete();
            });
        });
    }
}