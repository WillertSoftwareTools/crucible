/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSING file in the root of this project.
 */

package de.willert.crucible.reportplugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Created by czoeller on 17.11.16.
 */
public class PluginConfig extends GuiceServletContextListener {
    private static Injector injector;

    @Override
    protected Injector getInjector() {
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("test")).to("hello");
            }
        });
        return injector;
    }
}
