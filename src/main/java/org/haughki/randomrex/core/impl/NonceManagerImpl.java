package org.haughki.randomrex.core.impl;

import com.google.inject.Inject;
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
    public boolean isNonceValid(String nonce) {
        String foundNonce = nonceAccess.getNonce(nonce);
        return foundNonce != null && foundNonce != "";
    }

    // TODO implement -- should periodically wake up and remove all
    // expired nonces
    private void deleteExpiredNonces() {
    }
}
