package org.haughki.randomrex.test.integration;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.haughki.randomrex.ServerStart;
import org.haughki.randomrex.util.IdAndWorkingDir;
import org.haughki.randomrex.util.Runner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/*
 * Example of an asynchronous unit test written in JUnit style using vertx-unit
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@RunWith(VertxUnitRunner.class)
public class BasicIntegrationTest {

    private static Vertx vertx;

    @BeforeClass
    public static void oneTimeSetUp(TestContext context) {
        final IdAndWorkingDir builder = new IdAndWorkingDir(ServerStart.class);
        vertx = Vertx.vertx();
        Runner runner = new Runner(builder, context.asyncAssertSuccess(), vertx);
        runner.run();
    }


    @AfterClass
    public static void oneTimeTearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testGetIndexBasicHappyPath(TestContext context) {
        // Send a request and get a response
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        client.getNow(ServerStart.PORT, "localhost", "/", resp -> {
            context.assertEquals(200, resp.statusCode());
            resp.bodyHandler(body -> {
                String htmlString = body.toString("UTF-8");
                context.assertNotEquals(-1, htmlString.indexOf("<title>Example of the Authorization Code flow"));
                client.close();
                async.complete();
            });
        });
    }

    @Test
    public void testLoginBasicHappyPath(TestContext context) {
        // Send a request and get a response
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        client.getNow(ServerStart.PORT, "localhost", "/login", resp -> {
            context.assertEquals(303, resp.statusCode());
            context.assertEquals("See Other", resp.statusMessage());

            List<NameValuePair> queryString = null;
            try {
                queryString = URLEncodedUtils.parse(new URI(resp.getHeader("Location")), "UTF-8");
            } catch (URISyntaxException e) {
                context.fail(e);
            }

            context.assertNotNull(queryString);
            context.assertEquals(5, queryString.size());

            client.close();
            async.complete();
        });
    }

    @Test
    public void testCallbackBasicHappyPath(TestContext context) {
        // Send a request and get a response
/*        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        client.getNow(ServerStart.PORT, "localhost", "/callback", resp -> {
            context.assertEquals(303, resp.statusCode());
            context.assertEquals("See Other", resp.statusMessage());

            List<NameValuePair> queryString = null;
            try {
                queryString = URLEncodedUtils.parse(new URI(resp.getHeader("Location")), "UTF-8");
            } catch (URISyntaxException e) {
                context.fail(e);
            }

            context.assertNotNull(queryString);
            context.assertEquals(5, queryString.size());

            client.close();
            async.complete();
        });*/
    }
}