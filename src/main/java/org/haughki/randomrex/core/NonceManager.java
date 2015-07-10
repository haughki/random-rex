package org.haughki.randomrex.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface NonceManager {
    void nextNonce(final Handler<AsyncResult<String>> handler);

    void isNonceValid(final String nonce, final Handler<AsyncResult<Boolean>> handler);
}
