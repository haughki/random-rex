package org.haughki.randomrex;

import com.google.inject.AbstractModule;
import org.haughki.randomrex.impl.RequestHandlersImpl;

public class DependencyConfiguration extends AbstractModule {

    @Override
    protected void configure() {
        bind(RequestHandlers.class).to(RequestHandlersImpl.class);
    }

}

