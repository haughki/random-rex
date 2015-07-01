package org.haughki.randomrex.util;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.function.Consumer;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Runner {

    private static final String JAVA_MAIN_DIR = "src/main/java/";


    public static void runClusteredExample(Class clazz) {
        runJavaExample(JAVA_MAIN_DIR, clazz, true);
    }

    public static void runExample(Class clazz) {
        runJavaExample(JAVA_MAIN_DIR, clazz, false);
    }

    public static void runExample(Class clazz, DeploymentOptions options) {
        runJavaExample(JAVA_MAIN_DIR, clazz, options);
    }

    public static void runJavaExample(String prefix, Class clazz, boolean clustered) {
        runJavaExample(prefix, clazz, new VertxOptions().setClustered(clustered));
    }

    public static void runJavaExample(String prefix, Class clazz, VertxOptions options) {
        String exampleDir = prefix + clazz.getPackage().getName().replace(".", "/");
        runExample(exampleDir, clazz.getName(), options);
    }

    public static void runJavaExample(String prefix, Class clazz, DeploymentOptions deploymentOptions) {
        String exampleDir = prefix + clazz.getPackage().getName().replace(".", "/");
        runExample(exampleDir, clazz.getName(), new VertxOptions(), deploymentOptions);
    }

    public static void runExample(String exampleDir, String verticleID, VertxOptions options) {
        runExample(exampleDir, verticleID, options, null);
    }

    public static void runExample(String exampleDir, String verticleID, VertxOptions options, DeploymentOptions deploymentOptions) {
        System.out.println("exampleDir: " + exampleDir);
        System.setProperty("vertx.cwd", exampleDir);
        Consumer<Vertx> runner = vertx -> {
            try {
                if (deploymentOptions != null) {
                    vertx.deployVerticle(verticleID, deploymentOptions);
                } else {
                    vertx.deployVerticle(verticleID);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (options.isClustered()) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
            Vertx vertx = Vertx.vertx(options);
            runner.accept(vertx);
        }
    }
}
