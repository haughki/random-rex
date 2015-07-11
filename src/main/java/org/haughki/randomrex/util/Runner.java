package org.haughki.randomrex.util;

import io.vertx.core.*;

import java.util.function.Consumer;

/*
 * A helper to deploy verticles.
 *
 * @author <a href="mailto:parker.hawkeye@gmail.com">Hawkeye Parker</a>
 */
public class Runner {

    private final Vertx vertx;
    private Handler<AsyncResult<String>> completionHandler;
    private final VertxOptions vertxOptions;
    private final DeploymentOptions deploymentOptions;
    private final String verticleId;

    public Runner(final IdAndWorkingDir idAndWorkingDir) {
        this(idAndWorkingDir, null);
    }

    public Runner(final IdAndWorkingDir idAndWorkingDir, final Handler<AsyncResult<String>> completionHandler) {
        this(idAndWorkingDir, completionHandler, null);
    }

    /*
     * If you pass in a vertx instance, you probably want to create the IdAndWorkingDir instance
     * _before_ you create your vertx instance, since the builder sets the vertx.cwd which vertx uses during startup.
     */
    public Runner(final IdAndWorkingDir idAndWorkingDir, final Handler<AsyncResult<String>> completionHandler, final Vertx vertx) {
        if (idAndWorkingDir == null)
            throw new IllegalArgumentException("idAndDirBuilder cannot be null.");

        this.verticleId = idAndWorkingDir.getVerticleId();
        if (this.verticleId == null)
            throw new IllegalArgumentException("verticleId cannot be null.");

        this.completionHandler = completionHandler;

        if (this.completionHandler == null) {
            this.completionHandler = res -> {  // default handler
                if (res.succeeded()) {
                    System.out.println("Deployment successful for: " + this.verticleId);
                } else {
                    System.out.println("ERROR: Deployment failed for: " + this.verticleId);
                }
            };
        }

        // paranoia
        //noinspection ConstantConditions
        if (this.completionHandler == null)
            throw new IllegalArgumentException("completionHandler should not be null.");

        // Can't pass in a vertx and expect to set options on it in here -- previously set options might be overwritten
        this.vertx = vertx;
        if (this.vertx == null)
            this.vertxOptions = new VertxOptions().setClustered(false);
        else
            this.vertxOptions = null;

        // paranoia
        //noinspection ConstantConditions
        if (this.vertx != null && this.vertxOptions != null)
            throw new IllegalArgumentException("In this class, you can't set vertx options on an existing vertx instance.");


        this.deploymentOptions = null;  // just remembering this for later
    }

    public void run() {
        final Consumer<Vertx> runner = aVertx -> {
            if (deploymentOptions != null) {
                aVertx.deployVerticle(this.verticleId, deploymentOptions, completionHandler);
            } else {
                aVertx.deployVerticle(this.verticleId, completionHandler);
            }
        };

        if (this.vertx == null) {
            // This is a bit of a mess.  I pass in a vertx instance at the ctor of this class, so I can't really
            // go back and ~convert it~ into a clustered vertx, or even set options on it (might overwrite something).
            if (vertxOptions != null && vertxOptions.isClustered()) {
                Vertx.clusteredVertx(vertxOptions, res -> {
                    if (res.succeeded()) {
                        Vertx newVertx = res.result();  // must use a new vertx instance here, not the member instance
                        runner.accept(newVertx);
                    } else {
                        throw new RuntimeException("Failed to deploy clustered Vertx.", res.cause());
                    }
                });
            } else {
                Vertx newVertx = Vertx.vertx(vertxOptions);
                runner.accept(newVertx);
            }
        } else {
            runner.accept(this.vertx); // see ctor validation re: this and vertxOptions
        }
    }

}
