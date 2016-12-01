/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.template.exception;

import de.willert.crucible.latex.errorparser.ProblemMarker;
import de.willert.crucible.latex.errorparser.TexExceptionParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by czoeller on 01.12.16.
 */
public class TexExceptionParserTest {

    private TexExceptionParser parser;
    private File unusedTexFile;

    @Before
    public void setUp() throws Exception  {
        parser = new TexExceptionParser();
        final TemporaryFolder testFolder = new TemporaryFolder();
        testFolder.create();
        unusedTexFile = testFolder.newFile("unusedTexFile.tex");
    }

    @Test
    public void textMisplacedAlignmentTab() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                              " Misplaced alignment tab character &.\n" + "\n" + "\n" + "l.5 The company is named `Michael & Sons'\n" + "I can't figure out why you would want to use a tab mark\n" + "here. If you just want an ampersand, the remedy is\n" + "simple: Just type `I\\&' now. But if some right brace\n" + "up above has ended a previous alignment prematurely,\n" + "you're probably due for more error messages, and you\n" + "might try typing `S' now just to see what is salvageable.\n");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have placed an alignment tab character") );
    }

    @Test
    public void textExtraAlignmentTab() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       " Extra alignment tab has been changed to \\cr.\n" + "\n" + "\n" + "<recently read> \\endtemplate ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have written too many alignment tabs in a table") );
    }

    @Test
    public void testForgottenDollarSign() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "Display math should end with $$.\n" + "<to be read again> \n" + "                    \n" + "l.1617 $$ E=mc^2 $\n" + "                  \n" + "The `$' that I just saw supposedly matches a previous `$$'.\n" + "So I shall assume that you typed `$$' both times.");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have forgotten a $ sign at the end of 'display math' mode.") );
    }

    @Test
    public void testMissingDollarSign() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "Missing $ inserted.\n" + "<inserted text> \n" + "                $\n" + "l.1618 \n" + "       \n" + "I've inserted a begin-math/end-math symbol since I think\n" + "you left one out. Proceed, with fingers crossed.");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("Check that your $'s match around math expressions. If they do, then you've probably used a symbol in normal text") );
    }

    @Test
    public void testReferencingMissingLabel() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Warning: Reference `intorduction' on page 31 undefined on input line 1619.");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have referenced something which has not yet been labelled. If you have labelled it already") );
    }

    @Test
    public void testWrongCitation() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "Package natbib Warning: Citation `Qinstein' on page 32 undefined on input line 1635.");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have cited something which is not included in your bibliography.") );
    }

    @Test
    public void testMultipleDefinedLabels() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Warning: Label `algebra' multiply defined.");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have used the same label more than once.") );
    }

    @Test
    public void testFloatSpecifierH() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "`h' float specifier changed to `ht'. ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("The float specifier 'h' is too strict of a demand for LaTeX to place your") );
    }

    @Test
    public void testNoPosOptionalFloatPositioner() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "No positions in optional float specifier");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have forgotten to include a float specifier, which tells LaTeX where") );
    }

    @Test
    public void testUndefinedControlSequence() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "./b.tex:10: Undefined control sequence.\n" + "<recently read> \\Zlpha ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("The compiler is having trouble understanding a command you have used. Check that the c") );
    }

    @Test
    public void testFileNotFound() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Warning: File `image.PNG' not found on input line 12.");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("The compiler cannot find the file you want to include. Make sure that you hav") );
    }

    @Test
    public void unknownGraphicsExtension() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "./b.tex:12: LaTeX Error: Unknown graphics extension: .blalb.");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("The compiler does not recognise the file type of one of your images. Make sure you are") );
    }

    @Test
    public void unknownFloatOptionH() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Error: Unknown float option `H'.\n" + "\n");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("The compiler isn't recognizing the float option 'H'") );
    }

    @Test
    public void unknownFloatOptionQ() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Error: Unknown float option `q'. ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have used a float specifier which the compiler does not understand.") );
    }

    @Test
    public void testFontCommandinMath() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Error: \\mathrm allowed only in math mode");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have used a font command which is only available in math mode") );
    }

    public void testBeginWithoutEnd() throws Exception {
        //TODO
    }

    public void testOpenWithoutCloseBracket() throws Exception {
        //TODO
    }

    @Test
    public void testOnlyInPreamble() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Error: Can be used only in preamble.");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have used a command in the main body of your document which should be used in the prea") );
    }

    @Test
    public void testMissingRightInserted() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "Missing \\right. inserted. ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have started an expression with a \\left command, but have not included a corresp") );
    }

    @Test
    public void testDoubleSuperscript() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "Double superscript. ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have written a double superscript incorrectly as") );
    }

    @Test
    public void testDoubleSuberscript() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "Double subscript. ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have written a double subscript incorrectly as") );
    }

    @Test
    public void testNoAuthor() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "No \\author given");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have used the \\maketitle command, but have not specified any") );
    }

    @Test
    public void testUnknownEnvironment() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Error: Environment equation* undefined. ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have created an environment") );
    }

    @Test
    public void testNoItems() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "LaTeX Error: Something's wrong--perhaps a missing \\item");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("There are no entries found in a list you have created. Ma") );
    }

    @Test
    public void testMisplacedNoAlign() throws Exception {
        final List<ProblemMarker> errors = parser.parseErrors(unusedTexFile,
                                                       "Misplaced \\noalign. ");
        assertTrue(0 < errors.size() );
        assertTrue(errors.get(0).getHumanReadableHint().contains("You have used a \\hline command in the wrong place,") );
    }

    public void testEndExpectedButNotFound() throws Exception {
        //TODO
    }

    public void testNoMatchngEndFound() throws Exception {
        //TODO
    }

    public void testEndFoundWithoutBegin() throws Exception {
        //TODO
    }



}