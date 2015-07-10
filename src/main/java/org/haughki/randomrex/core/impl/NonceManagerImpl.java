package org.haughki.randomrex.core.impl;

import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.haughki.randomrex.core.NonceAccess;
import org.haughki.randomrex.core.NonceManager;

public class NonceManagerImpl implements NonceManager {

    private final NonceAccess nonceAccess;

    @Inject
    public NonceManagerImpl(NonceAccess nonceAccess) {
        this.nonceAccess = nonceAccess;
    }

    @Override
    public void nextNonce(final Handler<AsyncResult<String>> handler) {
        final String nonce = Nonce.nextNonce().toString();
        nonceAccess.addNonce(nonce, res -> {
            if (res.succeeded()) {
                handler.handle(Future.succeededFuture(nonce));
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    @Override
    public void isNonceValid(final String nonce, Handler<AsyncResult<Boolean>> handler) {
        nonceAccess.findNonce(nonce, res -> {
            if (res.succeeded()) {
                final String foundNonce = res.result();
                handler.handle(Future.succeededFuture(foundNonce != null && foundNonce != ""));
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }


    // TODO implement -- should periodically wake up and remove all
    // expired nonces -- better:  use Mongo's TTL index.  See NonceAccessImpl.addNonce()
    private void deleteExpiredNonces() {
    }
}
