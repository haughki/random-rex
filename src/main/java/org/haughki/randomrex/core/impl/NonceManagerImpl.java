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
    public String nextNonce() {
        String nonce = Nonce.nextNonce().toString();
        nonceAccess.addNonce(nonce);
        return nonce;
    }

    @Override
    public void isNonceValid(final String nonce, Handler<AsyncResult<Boolean>> handler) {
        nonceAccess.getNonce(nonce, res -> {
            final String foundNonce = res.result();
            handler.handle(Future.succeededFuture(foundNonce != null && foundNonce != ""));
        });
    }

    // TODO implement -- should periodically wake up and remove all
    // expired nonces
    private void deleteExpiredNonces() {
    }
}
