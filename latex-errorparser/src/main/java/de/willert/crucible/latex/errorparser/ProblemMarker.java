/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.latex.errorparser;

import static de.willert.crucible.latex.errorparser.ExceptionType.SEVERITY;

/**
 * Created by czoeller on 01.12.16.
 */
public class ProblemMarker {
    private String humanReadableHint;
    private Integer lineNumber;
    private String extraInfoURL;
    private SEVERITY severity;

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getExtraInfoURL() {
        return extraInfoURL;
    }

    public String getHumanReadableHint() {
        return humanReadableHint;
    }

    public SEVERITY getSeverity() {
        return severity;
    }

    public static final class ProblemMarkerBuilder {
        private Integer lineNumber;
        private String extraInfoURL;
        private String humanReadableHint;
        private SEVERITY severity;

        private ProblemMarkerBuilder() {
        }

        public static ProblemMarkerBuilder aProblemMarker() {
            return new ProblemMarkerBuilder();
        }

        public ProblemMarkerBuilder withLineNumber(Integer lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public ProblemMarkerBuilder withExtraInfoURL(String extraInfoURL) {
            this.extraInfoURL = extraInfoURL;
            return this;
        }

        public ProblemMarkerBuilder withHumanReadableHint(String humanReadableHint) {
            this.humanReadableHint = humanReadableHint;
            return this;
        }

        public ProblemMarkerBuilder withSeverity(SEVERITY severity) {
            this.severity = severity;
            return this;
        }

        public ProblemMarkerBuilder but() {
            return aProblemMarker().withLineNumber(lineNumber)
                                   .withExtraInfoURL(extraInfoURL)
                                   .withHumanReadableHint(humanReadableHint)
                                   .withSeverity(severity);
        }

        public ProblemMarker build() {
            ProblemMarker problemMarker = new ProblemMarker();
            problemMarker.lineNumber = this.lineNumber;
            problemMarker.extraInfoURL = this.extraInfoURL;
            problemMarker.humanReadableHint = this.humanReadableHint;
            problemMarker.severity = this.severity;
            return problemMarker;
        }
    }

    @Override
    public String toString() {
        return "ProblemMarker{" + "humanReadableHint='" + humanReadableHint + '\'' + ", lineNumber=" + lineNumber + ", extraInfoURL='" + extraInfoURL + '\'' + ", severity=" + severity + '}';
    }
}
