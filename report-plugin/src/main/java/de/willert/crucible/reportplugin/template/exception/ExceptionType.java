/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSING file in the root of this project.
 */

package de.willert.crucible.reportplugin.template.exception;

import java.util.regex.Pattern;

/**
 * Created by czoeller on 28.09.16.
 * Represents an error with information how to find, how to fix, severity and extra online help.
 */
public class ExceptionType {

    private Pattern regexToMatch;
    private String extraInfoURL;
    private String humanReadableHint;
    private SEVERITY severity;

    public enum SEVERITY {
        WARNING, ERROR
    }

    /**
     * @return Regex to find error.
     */
    public Pattern getRegexToMatch() {
        return regexToMatch;
    }

    /**
     * @return Online help url.
     */
    public String getExtraInfoURL() {
        return extraInfoURL;
    }

    /**
     * @return Human understandable error message.
     */
    public String getHumanReadableHint() {
        return humanReadableHint;
    }

    /**
     * @return The severity.
     */
    public SEVERITY getSeverity() {
        return severity;
    }

    public static final class ExceptionTypeBuilder {
        private Pattern regexToMatch;
        private String extraInfoURL;
        private String humanReadableHint;
        private SEVERITY severity;

        private ExceptionTypeBuilder() {
        }

        public static ExceptionTypeBuilder anExcpetionType() {
            return new ExceptionTypeBuilder();
        }

        public ExceptionTypeBuilder withRegexToMatch(String regexToMatch) {
            this.regexToMatch = Pattern.compile(regexToMatch);
            return this;
        }

        public ExceptionTypeBuilder withRegexToMatch(Pattern regexToMatch) {
            this.regexToMatch = regexToMatch;
            return this;
        }

        public ExceptionTypeBuilder withExtraInfoURL(String extraInfoURL) {
            this.extraInfoURL = extraInfoURL;
            return this;
        }

        public ExceptionTypeBuilder withHumanReadableHint(String humanReadableHint) {
            this.humanReadableHint = humanReadableHint;
            return this;
        }

        public ExceptionTypeBuilder withSeverity(SEVERITY severity) {
            this.severity = severity;
            return this;
        }

        public ExceptionTypeBuilder but() {
            return anExcpetionType().withRegexToMatch(regexToMatch)
                                    .withExtraInfoURL(extraInfoURL)
                                    .withHumanReadableHint(humanReadableHint)
                                    .withSeverity(severity);
        }

        public ExceptionType build() {
            ExceptionType excpetionType = new ExceptionType();
            excpetionType.regexToMatch = this.regexToMatch;
            excpetionType.humanReadableHint = this.humanReadableHint;
            excpetionType.extraInfoURL = this.extraInfoURL;
            excpetionType.severity = this.severity;
            return excpetionType;
        }
    }
}
