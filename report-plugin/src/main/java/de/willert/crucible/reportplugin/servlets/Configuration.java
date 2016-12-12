/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.servlets;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by czoeller on 09.12.16.
 */
public class Configuration {

    private static final Logger LOG = LoggerFactory.getLogger("atlassian.plugin");

    private final PluginSettingsFactory pluginSettingsFactory;

    private static final String SETTINGS_KEY = Configuration.class.getName() + "2";
    private Properties options;

    public Configuration(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.options = loadProperties();
    }

    private Properties loadProperties() {
        final Object o = pluginSettingsFactory.createGlobalSettings()
                                              .get(SETTINGS_KEY);
        if( null == o )
            return new Properties();
        return (Properties) o;
    }

    public Option get(Option option) {
        final Option tmpOption = Option.valueOf(option.name());
        tmpOption.setValue(options.getProperty(option.name()));
        return tmpOption;
    }

    public Option get(String option) {
        final Option tmpOption = Option.valueOf(option);
        return get(tmpOption);
    }

    public Option get(Object option) {
        return get((String) option);
    }

    public void put(Option option, String value) {
        options.put(option.name(), value);
        pluginSettingsFactory.createGlobalSettings().put(SETTINGS_KEY, options);
        options = loadProperties();
    }
    public String[] getKeys() {
        return Arrays.stream(Option.values()).map(Enum::name).toArray(String[]::new);
    }

}
