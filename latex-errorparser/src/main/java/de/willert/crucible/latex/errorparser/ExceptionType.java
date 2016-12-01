/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.latex.errorparser;

import java.util.regex.Pattern;

/**
 * Created by czoeller on 28.09.16.
 * Represents an error with information how to find, how to fix, severity and extra online help.
 */
class ExceptionType {

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
    Pattern getRegexToMatch() {
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
    String getHumanReadableHint() {
        return humanReadableHint;
    }

    /**
     * @return The severity.
     */
    SEVERITY getSeverity() {
        return severity;
    }

    public static final class ExceptionTypeBuilder {
        private Pattern regexToMatch;
        private String extraInfoURL;
        private String humanReadableHint;
        private SEVERITY severity;

        private ExceptionTypeBuilder() {
        }

        static ExceptionTypeBuilder anExcpetionType() {
            return new ExceptionTypeBuilder();
        }

        ExceptionTypeBuilder withRegexToMatch(String regexToMatch) {
            this.regexToMatch = Pattern.compile(regexToMatch);
            return this;
        }

        ExceptionTypeBuilder withRegexToMatch(Pattern regexToMatch) {
            this.regexToMatch = regexToMatch;
            return this;
        }

        ExceptionTypeBuilder withExtraInfoURL(String extraInfoURL) {
            this.extraInfoURL = extraInfoURL;
            return this;
        }

        ExceptionTypeBuilder withHumanReadableHint(String humanReadableHint) {
            this.humanReadableHint = humanReadableHint;
            return this;
        }

        ExceptionTypeBuilder withSeverity(SEVERITY severity) {
            this.severity = severity;
            return this;
        }

        public ExceptionTypeBuilder but() {
            return anExcpetionType().withRegexToMatch(regexToMatch)
                                    .withExtraInfoURL(extraInfoURL)
                                    .withHumanReadableHint(humanReadableHint)
                                    .withSeverity(severity);
        }

        ExceptionType build() {
            ExceptionType excpetionType = new ExceptionType();
            excpetionType.regexToMatch = this.regexToMatch;
            excpetionType.humanReadableHint = this.humanReadableHint;
            excpetionType.extraInfoURL = this.extraInfoURL;
            excpetionType.severity = this.severity;
            return excpetionType;
        }
    }
}
