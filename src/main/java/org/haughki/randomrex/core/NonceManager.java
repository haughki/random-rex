package org.haughki.randomrex.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface NonceManager {
    String nextNonce();

    void isNonceValid(final String nonce, final Handler<AsyncResult<Boolean>> handler);
}
