package org.haughki.randomrex.util;

public class IdAndWorkingDir {

    private static final String JAVA_MAIN_DIR = "src/main/java/";
    private final String verticleId;

    private final String vertxWorkingDir;

    public IdAndWorkingDir(final Class clazz) {
        this.verticleId = clazz.getName();
        this.vertxWorkingDir = buildVertxWorkingDir(JAVA_MAIN_DIR, clazz);
        System.out.println("vertxWorkingDir: " + this.vertxWorkingDir);
        System.setProperty("vertx.cwd", vertxWorkingDir);
    }

    public String getVerticleId() {
        return verticleId;
    }

    private String buildVertxWorkingDir(String prefix, Class clazz) {
        return prefix + clazz.getPackage().getName().replace(".", "/");
    }
}
