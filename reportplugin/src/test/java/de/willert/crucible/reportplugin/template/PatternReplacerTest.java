/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.template;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

/**
 * Created by czoeller on 15.12.16.
 */
public class PatternReplacerTest {

    private PatternReplacer patternReplacer;

    @Before
    public void setUp() {
        this.patternReplacer = new PatternReplacer();
    }

    @Test
    public void testNoReplacement() {
        String in = "Hello";
        List<String> replaced = PatternReplacer.replace(newArrayList(in), "","" );
        assertThat(replaced, hasItem("Hello"));
    }

    @Test
    public void testFindUnreplaced() {
        final List<String> unreplacedLines = PatternReplacer.findUnreplacedVariables(newArrayList("$unknownVariable"));
        assertThat(unreplacedLines, hasItems("$unknownVariable"));
    }

    @Test
    public void testFindUnreplacedMultiple() {
        String line1 = "$unknownVariable\n";
        String line2 = "Hello $name";

        final List<String> unreplacedLines = PatternReplacer.findUnreplacedVariables(newArrayList(line1, line2));
        assertThat(unreplacedLines, hasItems("$unknownVariable", "$name"));
    }

    @Test
    public void testSingleLineReplacementUnknown() {
        String in = "$unknownVariable";
        String expected = "replaced";
        final List<String> replaced = PatternReplacer.replace(newArrayList(in), "$unknownVariable", expected);
        assertThat(replaced,  hasItem(expected));
    }

    @Test
    public void testSingleLineReplacement() {
        String in = "Hello $unknownVariable";
        String replacement = "replacement";
        final List<String> replaced = PatternReplacer.replace(newArrayList(in), "$unknownVariable", replacement);
        assertThat(replaced, hasItem("Hello replacement"));
    }

    @Test
    public void testSingleLineWrappedReplacement() {
        String in = "\\item{$unknownVariable}";
        String replacement = "replacement";
        String expectedOut = "\\item{replacement}";
        final List<String> replaced = PatternReplacer.replace(newArrayList(in), "$unknownVariable", replacement);
        assertThat(replaced, hasItem(expectedOut));
    }

    @Test
    public void testJavaClassRegression() {
        String in = "\\\\subsection{DevTools\\slash EclipseWorkspace\\slash project\\slash build\\slash classes\\slash project\\slash CommandExecJFrame$4.class}";
        String expectedOut = in;
        final List<String> replaced = PatternReplacer.replace(newArrayList(in), "$irrelevant", "irrelevant");
        assertThat(replaced, hasItem(expectedOut));
    }

}