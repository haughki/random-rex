package org.haughki.randomrex.core.impl;

public class SecurityUtilsImpl implements org.haughki.randomrex.core.SecurityUtils {
    /**
     * Get a nonce for an OAuth request.
     *
     * @return the nonce string to use in an OAuth request
     */
    @Override
    public String getNonce() {
        return Nonce.nextNonce().toString();
    }

    @Override
    public boolean isNonceValid(String nonce) {
        return NonceValidator.isNonceValid(nonce);
    }
}
