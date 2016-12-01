/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.latex.errorparser;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by czoeller on 01.09.16.
 * Constructs meaningful error messages.
 */
public class TexExceptionParser {
    private static final int MAX_LINE_LENGTH = 79;
    private List<ProblemMarker> problems = Lists.newArrayList();
    private List<ExceptionType> exceptionTypes;

    public TexExceptionParser() {
        exceptionTypes = Lists.newArrayList(
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Misplaced alignment tab character \\&")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Misplaced_alignment_tab_character_%26")
                                                  .withHumanReadableHint("You have placed an alignment tab character '&' in the wrong place. If you want to align something, you must write it inside an align environment such as \\begin{align} \u2026 \\end{align}, \\begin{tabular} \u2026 \\end{tabular}, etc. If you want to write an ampersand '&' in text, you must write \\& instead.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Extra alignment tab has been changed to \\\\cr")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Extra_alignment_tab_has_been_changed_to_%5Ccr")
                                                  .withHumanReadableHint("You have written too many alignment tabs in a table, causing one of them to be turned into a line break. Make sure you have specified the correct number of columns in your <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Tables\">table</a>.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Display math should end with \\$\\$")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Display_math_should_end_with_$$")
                                                  .withHumanReadableHint("You have forgotten a $ sign at the end of 'display math' mode. When writing in display math mode, you must always math write inside $$ \u2026 $$. Check that the number of $s match around each math expression.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Missing [{$] inserted.")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Missing_$_inserted")
                                                  .withHumanReadableHint("Check that your $'s match around math expressions. If they do, then you've probably used a symbol in normal text that needs to be in math mode. Symbols such as subscripts ( _ ), integrals ( \\int ), Greek letters ( \\alpha, \\beta, \\delta ), and modifiers (\\vec{x}, \\tilde{x} ) must be written in math mode. See the full list <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Errors/Missing_$_inserted \">here</a>.If you intended to use mathematics mode, then use $ \u2026 $ for 'inline math mode', $$ \u2026 $$ for 'display math mode' or alternatively \\begin{math} \u2026 \\end{math}.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("(undefined )?[rR]eference(s)?.+(undefined)?")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/There_were_undefined_references")
                                                  .withHumanReadableHint("You have referenced something which has not yet been labelled. If you have labelled it already, make sure that what is written inside \\ref{...} is the same as what is written inside \\label{...}.")
                                                  .withSeverity(ExceptionType.SEVERITY.WARNING)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Citation .+ on page .+ undefined on input line .+")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Citation_XXX_on_page_XXX_undefined_on_input_line_XXX")
                                                  .withHumanReadableHint("You have cited something which is not included in your bibliography. Make sure that the citation (\\cite{...}) has a corresponding key in your bibliography, and that both are spelled the same way.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("(Label .+)? multiply[ -]defined( labels)?")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/There_were_multiply-defined_labels")
                                                  .withHumanReadableHint("You have used the same label more than once. Check that each \\label{...} labels only one item.")
                                                  .withSeverity(ExceptionType.SEVERITY.WARNING)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("`!?h' float specifier changed to `!?ht'")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/%60!h%27_float_specifier_changed_to_%60!ht%27")
                                                  .withHumanReadableHint("The float specifier 'h' is too strict of a demand for LaTeX to place your float in a nice way here. Try relaxing it by using 'ht', or even 'htbp' if necessary. If you want to try keep the float here anyway, check out the <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Positioning_of_Figures\">float package</a>.")
                                                  .withSeverity(ExceptionType.SEVERITY.WARNING)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("No positions in optional float specifier")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/No_positions_in_optional_float_specifier")
                                                  .withHumanReadableHint("You have forgotten to include a float specifier, which tells LaTeX where to position your figure. To fix this, either insert a float specifier inside the square brackets (e.g. \begin{figure}[h]), or remove the square brackets (e.g. \begin{figure}). Find out more about float specifiers <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Positioning_of_Figures\">here</a>.")
                                                  .withSeverity(ExceptionType.SEVERITY.WARNING)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Undefined control sequence")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Undefined_control_sequence")
                                                  .withHumanReadableHint("The compiler is having trouble understanding a command you have used. Check that the command is spelled correctly. If the command is part of a package, make sure you have included the package in your preamble using \\usepackage{...}.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("File .+ not found")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/File_XXX_not_found_on_input_line_XXX")
                                                  .withHumanReadableHint("The compiler cannot find the file you want to include. Make sure that you have <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Including_images_in_ShareLaTeX\">uploaded the file</a> and <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Errors/File_XXX_not_found_on_input_line_XXX.\">specified the file location correctly</a>.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("LaTeX Error: Unknown graphics extension: \\..+")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/LaTeX_Error:_Unknown_graphics_extension:_.XXX")
                                                  .withHumanReadableHint("The compiler does not recognise the file type of one of your images. Make sure you are using a <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Errors/LaTeX_Error:_Unknown_graphics_extension:_.gif.\">supported image format</a> for your choice of compiler, and check that there are no periods (.) in the name of your image.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("LaTeX Error: Unknown float option `H'")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/LaTeX_Error:_Unknown_float_option_%60H%27")
                                                  .withHumanReadableHint("The compiler isn't recognizing the float option 'H'. Include \\usepackage{float} in your preamble to fix this.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("LaTeX Error: Unknown float option `q'")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/LaTeX_Error:_Unknown_float_option_%60q%27")
                                                  .withHumanReadableHint("You have used a float specifier which the compiler does not understand. You can learn more about the different float options available for placing figures <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Positioning_of_Figures\">here</a>")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("LaTeX Error: \\\\math.+ allowed only in math mode")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/LaTeX_Error:_%5Cmathrm_allowed_only_in_math_mode")
                                                  .withHumanReadableHint("You have used a font command which is only available in math mode. To use this command, you must be in maths mode (E.g. $ \u2026 $ or \\begin{math} \u2026 \\end{math}). If you want to use it outside of math mode, use the text version instead: \\textrm, \\textit, etc.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Error: `([^']{2,})' expected, found `([^']{2,})'.*")
                                                  .withHumanReadableHint("You have used \\\\begin{...} without a corresponding \\\\end{...}.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Error: `([^a-zA-Z0-9])' expected, found `([^a-zA-Z0-9])'.*")
                                                  .withHumanReadableHint("You have used an open bracket without a corresponding close bracket.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("LaTeX Error: Can be used only in preamble")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/LaTeX_Error:_Can_be_used_only_in_preamble")
                                                  .withHumanReadableHint("You have used a command in the main body of your document which should be used in the preamble. Make sure that \\documentclass[\u2026]{\u2026} and all \\usepackage{\u2026} commands are written before \\begin{document}.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Missing \\\\right\\.|. inserted.")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Missing_%5Cright_inserted")
                                                  .withHumanReadableHint("You have started an expression with a \\left command, but have not included a corresponding \\right command. Make sure that your \\left and \\right commands balance everywhere, or else try using \\Biggl and \\Biggr commands instead as shown <a target=\"_blank\" href=\"https://www.sharelatex.com/learn/Errors/Missing_%5Cright_inserted\">here</a>.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Double superscript")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Double_superscript")
                                                  .withHumanReadableHint("You have written a double superscript incorrectly as a^b^c, or else you have written a prime with a superscript. Remember to include { and } when using multiple superscripts. Try a^{b^c} instead.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Double subscript")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Double_subscript")
                                                  .withHumanReadableHint("You have written a double subscript incorrectly as a_b_c. Remember to include { and } when using multiple subscripts. Try a_{b_c} instead.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("No \\\\author given")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/No_%5Cauthor_given")
                                                  .withHumanReadableHint("You have used the \\maketitle command, but have not specified any \\author. To fix this, include an author in your preamble using the \\author{\u2026} command.")
                                                  .withSeverity(ExceptionType.SEVERITY.WARNING)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("LaTeX Error: Environment .+ undefined")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors%2FLaTeX%20Error%3A%20Environment%20XXX%20undefined")
                                                  .withHumanReadableHint("You have created an environment (using \\begin{\u2026} and \\end{\u2026} commands) which is not recognized. Make sure you have included the required package for that environment in your preamble, and that the environment is spelled correctly.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("LaTeX Error: Something's wrong--perhaps a missing \\\\item")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/LaTeX_Error:_Something%27s_wrong--perhaps_a_missing_%5Citem")
                                                  .withHumanReadableHint("There are no entries found in a list you have created. Make sure you label list entries using the \\item command, and that you have not used a list inside a table.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Misplaced \\\\noalign")
                                                  .withExtraInfoURL("https://www.sharelatex.com/learn/Errors/Misplaced_%5Cnoalign")
                                                  .withHumanReadableHint("You have used a \\hline command in the wrong place, probably outside a table. If the \\hline command is written inside a table, try including \\\\ before it.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Error: `\\end\\{([^\\}]+)\\}' expected but found `\\end\\{([^\\}]+)\\}'.*")
                                                  .withHumanReadableHint("You have used \\begin{} without a corresponding \\end{}.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Warning: No matching \\\\end found for `\\begin\\{([^\\}]+)\\}'.*")
                                                  .withHumanReadableHint("You have used \\begin{} without a corresponding \\end{}.")
                                                  .withSeverity(ExceptionType.SEVERITY.WARNING)
                                                  .build(),
                ExceptionType.ExceptionTypeBuilder.anExcpetionType()
                                                  .withRegexToMatch("Error: Found `\\end\\{([^\\}]+)\\}' without corresponding \\begin.*")
                                                  .withHumanReadableHint("You have used \\begin{} without a corresponding \\end{}.")
                                                  .withSeverity(ExceptionType.SEVERITY.ERROR)
                                                  .build()
        );
    }

    /**
     * Parse the output of the LaTeX program.
     *
     * @param resource the input tex file that was processed
     * @param output   the output of the external program
     * @return a list of error messages
     */
    public List<ProblemMarker> parseErrors(File resource, List<String> output) {
        return parseErrors(resource, output.toString() );
    }

    /**
     * Parse the output of the LaTeX program.
     *
     * @param resource the input tex file that was processed
     * @param output   the output of the external program
     * @return true, if error messages were found in the output, false otherwise
     */
    public List<ProblemMarker> parseErrors(File resource, String output) {

        StringTokenizer st = new StringTokenizer(output, "\r\n");
        int lineNr = 0;
        String line;
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            //Add more lines if line length is a multiple of 79 and
            //it does not end with ...
            while (!line.endsWith("...") && st.hasMoreTokens() && line.length() % MAX_LINE_LENGTH == 0) {
                line = line + st.nextToken();
                lineNr++;
            }
            line = line.replaceAll(" {2,}", " ").trim();
            String finalLine = line;
            int finalLineNr = lineNr;
            this.exceptionTypes.stream()
                               .filter(e -> e.getRegexToMatch().matcher(finalLine).find())
                               .filter(e -> !e.getHumanReadableHint().isEmpty())
                               .forEach(e -> addProblemMarker(e, finalLineNr, resource));
        }
        return problems;
    }

    /**
     * Constructs an error message to help solve the error.
     * @param e found ExceptionType
     * @param linenr The linenumber in the tex file.
     * @param resource The tex file
     */
    private void addProblemMarker(ExceptionType e, int linenr, File resource) {
        final ProblemMarker problemMarker = ProblemMarker.ProblemMarkerBuilder.aProblemMarker()
                                                                      .withHumanReadableHint(e.getHumanReadableHint())
                                                                      .withLineNumber(linenr)
                                                                      .withSeverity(e.getSeverity())
                                                                      .withExtraInfoURL(e.getExtraInfoURL())
                                                                      .build();
        problems.add( problemMarker );
    }
}
