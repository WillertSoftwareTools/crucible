package de.willert.crucible.reportplugin.template.exception;

import java.util.regex.Pattern;

/**
 * Created by czoeller on 28.09.16.
 */
public class ExcpetionType {
    public Pattern getRegexToMatch() {
        return regexToMatch;
    }

    public String getExtraInfoURL() {
        return extraInfoURL;
    }

    public String getHumanReadableHint() {
        return humanReadableHint;
    }

    private Pattern regexToMatch;
    private String extraInfoURL;
    private String humanReadableHint;

    public static final class ExcpetionTypeBuilder {
        private Pattern regexToMatch;
        private String extraInfoURL;
        private String humanReadableHint;

        private ExcpetionTypeBuilder() {
        }

        public static ExcpetionTypeBuilder anExcpetionType() {
            return new ExcpetionTypeBuilder();
        }

        public ExcpetionTypeBuilder withRegexToMatch(String regexToMatch) {
            this.regexToMatch = Pattern.compile(regexToMatch);
            return this;
        }

        public ExcpetionTypeBuilder withRegexToMatch(Pattern regexToMatch) {
            this.regexToMatch = regexToMatch;
            return this;
        }

        public ExcpetionTypeBuilder withExtraInfoURL(String extraInfoURL) {
            this.extraInfoURL = extraInfoURL;
            return this;
        }

        public ExcpetionTypeBuilder withHumanReadableHint(String humanReadableHint) {
            this.humanReadableHint = humanReadableHint;
            return this;
        }

        public ExcpetionTypeBuilder but() {
            return anExcpetionType().withRegexToMatch(regexToMatch)
                                    .withExtraInfoURL(extraInfoURL)
                                    .withHumanReadableHint(humanReadableHint);
        }

        public ExcpetionType build() {
            ExcpetionType excpetionType = new ExcpetionType();
            excpetionType.regexToMatch = this.regexToMatch;
            excpetionType.humanReadableHint = this.humanReadableHint;
            excpetionType.extraInfoURL = this.extraInfoURL;
            return excpetionType;
        }
    }
}
