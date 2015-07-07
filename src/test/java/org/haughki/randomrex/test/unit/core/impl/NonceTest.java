package org.haughki.randomrex.test.unit.core.impl;

import org.haughki.randomrex.core.impl.Nonce;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NonceTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test1000NonceGetsNotEqual_NonNull_HaveAlphaNum() throws Exception {
        // get nonces as fast as you can
        final List<String> nonces = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            nonces.add(Nonce.nextNonce().toString());
        }

        for (int i = 0; i < nonces.size(); i++) {
            Assert.assertNotNull(nonces.get(i));
            Assert.assertTrue("Nonce should have at least one letter.", Pattern.compile("\\p{Alpha}").matcher(nonces.get(i)).find());
            Assert.assertTrue("Nonce should have at least one number.", Pattern.compile("\\p{Digit}").matcher(nonces.get(i)).find());
            Assert.assertEquals("Nonce should be 32 chars long.", 32, nonces.get(i).length());
            for (int j = 0; j < nonces.size(); j++) {
                if (i == j) {
                    Assert.assertEquals("Same nonce should be equal.", nonces.get(i), nonces.get(j));
                } else {
                    Assert.assertNotEquals("Different nonce should not be equal.", nonces.get(i), nonces.get(j));
                }
            }
        }
    }
}