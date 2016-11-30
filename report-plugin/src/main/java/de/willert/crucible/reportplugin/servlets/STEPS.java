package de.willert.crucible.reportplugin.servlets;

import java.util.stream.Stream;

/**
 * Created by czoeller on 30.11.16.
 */
public enum STEPS {
    ONE("/plugins/servlet/report-servlet"),
    TWO("/plugins/servlet/report-servlet-preview"),
    THREE("/plugins/servlet/report-servlet-generate");

    private String path;

    STEPS(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static STEPS literalFor(String servletPath) {
        return Stream.of(STEPS.values())
                     .filter(steps -> steps.getPath().equals(servletPath))
                     .findFirst()
                     .orElse(STEPS.ONE);
    }
}
