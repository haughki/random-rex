package org.haughki.randomrex;

import com.google.inject.AbstractModule;
import org.haughki.randomrex.core.SecurityUtils;
import org.haughki.randomrex.core.impl.SecurityUtilsImpl;
import org.haughki.randomrex.impl.RequestHandlersImpl;

public class DependencyConfiguration extends AbstractModule {

    @Override
    protected void configure() {
        bind(SecurityUtils.class).to(SecurityUtilsImpl.class);
        bind(RequestHandlers.class).to(RequestHandlersImpl.class);
    }
}

