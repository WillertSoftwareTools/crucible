/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.template;


import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by czoeller on 15.12.16.
 */
public class PatternReplacer {

    public static List<String> findUnreplacedVariables(List<String> lines) {

        List<String> unreplaced = new Vector<>();

        final Pattern pattern = Pattern.compile("\\$\\w+\\b(?!\\.class)");

        for (String line : lines) {
            final Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                unreplaced.add( matcher.group() );
            }
        }
        return unreplaced;

    }

    public static List<String> replace(List<String> lines, String unreplacedVariable, String replacement) {
        if(unreplacedVariable.isEmpty()) {
            return lines;
        }
        lines.replaceAll(s -> s.replaceAll("\\"+unreplacedVariable+"", replacement));
        return lines;
    }
}
