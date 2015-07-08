package org.haughki.randomrex.core.impl;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import org.haughki.randomrex.core.NonceManager;

public class SecurityUtilsImpl implements org.haughki.randomrex.core.SecurityUtils {

    private final NonceManager nonceManager;

    @Inject
    public SecurityUtilsImpl(NonceManager nonceManager) {
        this.nonceManager = nonceManager;
    }

    /**
     * Get a nonce for an OAuth request.
     *
     * @return the nonce string to use in an OAuth request
     */
    @Override
    public String getNonce() {
        return nonceManager.nextNonce();
    }

    @Override
    public void isNonceValid(String nonce, Handler<Boolean> handler) {
        handler.handle(false);
/*        nonceManager.isNonceValid(nonce, isValid -> {
            handler.handle(isValid);
        });*/
    }
}
