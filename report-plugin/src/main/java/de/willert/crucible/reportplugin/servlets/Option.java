/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.servlets;

/**
 * Created by czoeller on 09.12.16.
 */
public enum Option {

    HEADER_LEFT("", "Header left", "Example\\\\www.example.com", "Left header value in template on each page.");

    private String displayname;
    private String value;
    private final String defaultValue;
    private final String description;

    Option(String value, String displayname, String defaultValue, String description) {
        this.value = value;
        this.displayname = displayname;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayname() {
        return displayname;
    }
}
