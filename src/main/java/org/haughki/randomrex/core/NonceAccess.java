package org.haughki.randomrex.core;

import org.haughki.randomrex.core.impl.Nonce;

public interface NonceAccess {
    void addNonce(String nonce);

    String getNonce(String nonce);

    void deleteNonce(String nonce);

}
