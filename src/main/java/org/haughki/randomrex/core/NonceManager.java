package org.haughki.randomrex.core;

public interface NonceManager {
    String nextNonce();

    boolean isNonceValid(String nonce);
}
