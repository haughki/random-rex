package org.haughki.randomrex;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.haughki.randomrex.core.NonceAccess;
import org.haughki.randomrex.core.NonceManager;
import org.haughki.randomrex.core.SecurityUtils;
import org.haughki.randomrex.core.impl.NonceAccessImpl;
import org.haughki.randomrex.core.impl.NonceManagerImpl;
import org.haughki.randomrex.core.impl.SecurityUtilsImpl;
import org.haughki.randomrex.impl.RequestHandlersImpl;

public class DependencyConfiguration extends AbstractModule {

    private final Vertx vertx;

    public DependencyConfiguration(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    protected void configure() {

        bind(RequestHandlers.class).to(RequestHandlersImpl.class);
        bind(SecurityUtils.class).to(SecurityUtilsImpl.class);
        bind(NonceAccess.class).to(NonceAccessImpl.class);
        bind(NonceManager.class).to(NonceManagerImpl.class);
    }

    /**
     * Provides a new MongoClient to any component that wants mongo access.
     */
    @Provides
    MongoClient createMongoClient() {
        // Create a mongo client using all defaults (connect to localhost and default port)
        // using the database name "demo".
        // TODO choose a real db name and configure mongo with a config file...
        return MongoClient.createShared(vertx, new JsonObject().put("db_name", "random-rex"));
    }
}

