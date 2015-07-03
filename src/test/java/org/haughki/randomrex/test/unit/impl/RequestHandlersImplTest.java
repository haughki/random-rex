package org.haughki.randomrex.test.unit.impl;

import com.google.inject.Guice;
import com.google.inject.Inject;
import org.haughki.randomrex.DependencyConfiguration;
import org.haughki.randomrex.RequestHandlers;
import org.junit.Before;
import org.junit.Test;

public class RequestHandlersImplTest {

    @Inject
    private RequestHandlers handlers;

    @Before
    public void setUp() throws Exception {
        Guice.createInjector(new DependencyConfiguration()).injectMembers(this);
    }

    @Test
    public void testHandleLogin() throws Exception {
        //RoutingContext context = new MockRoutingContext();
        //handlers.handleLogin(context);

    }
}