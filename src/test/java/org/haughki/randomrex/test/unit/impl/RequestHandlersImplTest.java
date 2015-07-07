package org.haughki.randomrex.test.unit.impl;

import com.google.inject.Inject;
import org.haughki.randomrex.RequestHandlers;
import org.junit.Before;
import org.junit.Test;


// TODO implement or not?
public class RequestHandlersImplTest {

    @Inject
    private RequestHandlers handlers;

    @Before
    public void setUp() throws Exception {
        //Guice.createInjector(new DependencyConfiguration()).injectMembers(this);
    }

    @Test
    public void testHandleLogin() throws Exception {
 /*       RoutingContext context = mock(RoutingContext.class);
        when(context.addCookie())


        handlers.handleLogin(context);*/
    }

    @Test
    public void test() throws Exception {
        //mock creation
/*        List mockedList = mock(List.class);

        //using mock object
        mockedList.add("one");
        mockedList.clear();

        //verification
        verify(mockedList).add("one");
        verify(mockedList).clear();*/
    }


}