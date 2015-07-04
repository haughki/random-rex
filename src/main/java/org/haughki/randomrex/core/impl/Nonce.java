/*Copyright (c) 2009-2014 David Grant
Copyright (c) 2010 ThruPoint, Ltd

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.*/

package org.haughki.randomrex.core.impl;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * Copied almost verbatim from
 * https://github.com/jscep/jscep/blob/89457ae7c7caab76c9a11ea0218be90a9ddfa5b8/src/main/java/org/jscep/transaction/Nonce.java
 * Didn't want the entire lib just for this.
 * <p>
 * OAuth defines the nonce as "a random string, uniquely generated for each request.
 * The nonce allows the Service Provider to verify that a request has never been made
 * before and helps prevent replay attacks when requests are made over a non-secure channel
 * (such as HTTP)."
 * <p>
 * This class represents the <code>senderNonce</code> and
 * <code>recipientNonce</code> types.
 */
public final class Nonce {
    private static final int NONCE_LENGTH = 16;
    private static final Random RND = new SecureRandom();
    private final byte[] nonce;

    /**
     * Creates a new <tt>Nonce</tt> with the given byte array.
     *
     * @param nonce the byte array.
     */
    public Nonce(final byte[] nonce) {
        this.nonce = ArrayUtils.clone(nonce);
    }

    /**
     * Returns the <ttNonce</tt> byte array.
     *
     * @return the byte array.
     */
    public byte[] getBytes() {
        return ArrayUtils.clone(nonce);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new String(Hex.encodeHex(nonce));
    }

    /**
     * Generates a new random <tt>Nonce</tt>.
     * <p>
     * This method uses a static {@link SecureRandom} instance as the source of
     * randomness, and can therefore make no guarantee of true uniqueness.
     *
     * @return the generated <tt>Nonce</tt>.
     */
    public static Nonce nextNonce() {
        byte[] bytes = new byte[NONCE_LENGTH];
        RND.nextBytes(bytes);

        return new Nonce(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Nonce nonce1 = (Nonce) o;

        return Arrays.equals(nonce, nonce1.nonce);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(nonce);
    }
}