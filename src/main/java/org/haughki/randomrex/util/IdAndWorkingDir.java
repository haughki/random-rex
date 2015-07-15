package org.haughki.randomrex.util;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class IdAndWorkingDir {

    private static final String JAVA_MAIN_DIR = "src/main/java/";
    private final String verticleId;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public IdAndWorkingDir(final Class clazz) {
        this.verticleId = clazz.getName();
        final String vertxWorkingDir = buildVertxWorkingDir(JAVA_MAIN_DIR, clazz);
        logger.info("vertxWorkingDir: " + vertxWorkingDir);
        System.setProperty("vertx.cwd", vertxWorkingDir);
    }

    public String getVerticleId() {
        return verticleId;
    }

    private String buildVertxWorkingDir(final String prefix, final Class clazz) {
        return prefix + clazz.getPackage().getName().replace(".", "/");
    }
}
