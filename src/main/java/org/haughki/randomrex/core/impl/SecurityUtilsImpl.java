package org.haughki.randomrex.core.impl;

public class SecurityUtilsImpl implements org.haughki.randomrex.core.SecurityUtils {
    /**
     * Get a nonce for an OAuth request.  OAuth defines the nonce as "a random
     * string, uniquely generated for each request. The nonce allows the Service
     * Provider to verify that a request has never been made before and helps
     * prevent replay attacks when requests are made over a non-secure channel
     * (such as HTTP)."
     *
     * @return the nonce string to use in an OAuth request
     */
    public String getNonce() {
        return Long.toString(System.nanoTime());
    }
}
