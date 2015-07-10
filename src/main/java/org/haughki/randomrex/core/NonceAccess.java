package org.haughki.randomrex.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface NonceAccess {
    void addNonce(final String nonce, final Handler<AsyncResult<String>> handler);

    void findNonce(final String nonce, final Handler<AsyncResult<String>> handler);

    void deleteNonce(final String nonce, final Handler<AsyncResult<Void>> handler);

}
