package org.haughki.randomrex.test.unit.core.impl;

import org.haughki.randomrex.core.SecurityUtils;
import org.haughki.randomrex.core.impl.SecurityUtilsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecurityUtilsImplTest {

    private SecurityUtils securityUtils;

    @Before
    public void setUp() throws Exception {
        securityUtils = new SecurityUtilsImpl();
    }

    @Test
    public void testNonceNotNull() throws Exception {
        Assert.assertNotNull(securityUtils.getNonce());
    }

    @Test
    public void testTwoGetsNotEqual() throws Exception {
        Assert.assertNotEquals("Two calls to getNonce() produce same value.",
                securityUtils.getNonce(), securityUtils.getNonce());
    }

    @Test
    public void testFourGetsNotEqual() throws Exception {
        // get nonces as fast as you can
        List<String> nonces = new ArrayList<>(Arrays.asList(
                securityUtils.getNonce(),
                securityUtils.getNonce(),
                securityUtils.getNonce(),
                securityUtils.getNonce()
        ));

        for (int i = 0; i < nonces.size(); i++) {
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