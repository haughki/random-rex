package org.haughki.randomrex;

import org.vertx.java.platform.impl.cli.Starter;

public class Main {
    public static void main(String[] args) {
        Starter.main(new String[]{"run",
                "./src/main/java/org/haughki/randomrex/TestHttpServer.java",
                "-conf",
                "src/main/resources/config.json"});
    }
}
