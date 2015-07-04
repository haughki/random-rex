package org.haughki.randomrex.core.impl;


import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds a data struct of sent nonces
 * When a nonce-laden request comes in, checks for the nonce.
 * Any added nonce has a timeout, after which it must be removed.
 */
public class NonceValidator {

    // TODO !! MEMORY LEAK !!  SHOULD BE DB BACKED, NON IN MEM!!
    // concurrent set. should be thread-safe.
    // interesting http://minborgsjavapot.blogspot.com/2014/12/java-8-implementing-concurrenthashset.html
    static Set<String> nonces = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    public static void addNonce(String nonce) {
        nonces.add(nonce);
    }

    public static boolean isNonceValid(String nonce) {
        boolean valid = nonces.contains(nonce);
        nonces.remove(nonce);
        return valid;
    }
}
