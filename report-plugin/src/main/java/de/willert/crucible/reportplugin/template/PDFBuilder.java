/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSING file in the root of this project.
 */

package de.willert.crucible.reportplugin.template;

import com.google.common.collect.Lists;
import de.nixosoft.jlr.JLRConverter;
import de.nixosoft.jlr.JLRGenerator;
import de.willert.crucible.latex.errorparser.ProblemMarker;
import de.willert.crucible.latex.errorparser.TexExceptionParser;
import de.willert.crucible.reportplugin.template.exception.TexParserException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ParseErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by czoeller on 31.08.16.
 */
public class PDFBuilder {
    private static final Log log = LogFactory.getLog(PDFBuilder.class);

    private TemplateEnvironment templateEnvironment;
    private File templateFile;
    private File transformFile;

    public PDFBuilder(TemplateEnvironment templateEnvironment) {
        this.templateEnvironment = templateEnvironment;
        templateFile = templateEnvironment.getTemplateFile()
                                          .orElseThrow(() -> new IllegalStateException("Unable to get Template file"));
        transformFile = templateEnvironment.getTransformFile()
                                           .orElseThrow(() -> new IllegalStateException("Unable to get Transformer file"));
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public File getTransformFile() {
        return transformFile;
    }

    public Optional<File> buildPDF(ReviewTemplate reviewTemplate) {
        try {
            transformToTex(reviewTemplate, templateFile, transformFile);
            return createPDFFile(transformFile, templateEnvironment.getDestinationFile(), transformFile.getParentFile());
        } catch (IOException | TexParserException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<File> buildPDF(File transformFile) throws IOException, TexParserException {
        return createPDFFile(transformFile, templateEnvironment.getDestinationFile(), transformFile.getParentFile());
    }

    public Optional<File> transformToTex(final ReviewTemplate reviewTemplate, final File templateFile, final File transformFile) throws IOException {
        return Optional.ofNullable(parseTexFile(transformFile, templateFile, transformFile.getParentFile(), reviewTemplate));
    }

    /**
     * Creates a transformed .tex file from apache velocity .tex template with injected template data.
     *
     * @param outputTexFile  .tex File that is being created to store the transformed tex.
     * @param templateFile   .tex File that is used as input for the transformation.
     * @param imageDir       Directory, where the images for the template is stored.
     * @param reviewTemplate {@link ReviewTemplate} that is used for transformation.
     * @throws IOException Is thrown by the JLRConverter while converting.
     */
    private File parseTexFile(File outputTexFile, File templateFile, File imageDir, ReviewTemplate reviewTemplate) throws IOException, ParseErrorException {
        JLRConverter converter = new JLRConverter(templateFile.getParentFile());

        converter.replace("reviewTemplate", reviewTemplate);
        converter.replace("imgDir", toLatexStylePath(imageDir.getAbsolutePath()));

        converter.parse(templateFile, outputTexFile);
        return outputTexFile;
    }

    /**
     * Is needed to make a Path matching the Latex syntax for paths
     *
     * @param path Path, that is converted to latex style path
     * @return path that can be used in a latex document without getting errors
     */
    private String toLatexStylePath(String path) {
        return FilenameUtils.separatorsToUnix(path);
    }

    /**
     * This Method is used to parse the specific .tex file, created by the converter into a PDF File. Therefore the The JLRGenerator implicitly uses
     * pdflatex.
     *
     * @param inputTexFile  .tex File that is used as input for pdf.
     * @param outputPDFFile specifies where the pdfFile should be saved
     * @param rootDir       is needed for the JLRGenerator
     * @return File         generated PDFFile
     * @throws IOException        Is thrown by the JLRGenerator if any error occurs while generating the PDF File
     * @throws TexParserException Is thrown when the .tex file has wrong latex syntax and cannot be interpreted by pdflatex
     */
    private Optional<File> createPDFFile(File inputTexFile, File outputPDFFile, File rootDir) throws IOException, TexParserException {

        fixCommonLatexPeculiarities(inputTexFile);

        JLRGenerator generator = new JLRGenerator();
        if (generator.generate(inputTexFile, inputTexFile.getParentFile(), rootDir)) {
            File tempGeneratedPDF = findPDFFile(inputTexFile);
            FileUtils.copyFile(tempGeneratedPDF, outputPDFFile);
            //JLROpener.open(outputPDFFile);
            return Optional.of(outputPDFFile);
        } else {
            throw new TexParserException(extractErrorMessage(inputTexFile, outputPDFFile, generator));
        }
    }

    private void fixCommonLatexPeculiarities(File inputTexFile) throws IOException {
            List<String> lines = FileUtils.readLines(inputTexFile, "UTF-8");
             List<String> replaced = Lists.newArrayList();
            for (String line : lines) {
                line = line.replaceAll("((?<!\\\\)(_+))", "\\\\textunderscore ");
                replaced.add(line);
            }
            Files.write(inputTexFile.toPath(), replaced);
    }

    /**
     * Extract error message from environment.
     *
     * @param inputTexFile  .tex File that is used as input for pdf
     * @param outputPDFFile specifies where the pdfFile should be saved
     * @param generator     JLR converter used plot
     * @return error message
     */
    private String extractErrorMessage(File inputTexFile, File outputPDFFile, JLRGenerator generator) throws IOException {
        String latexError = extractLatexError(inputTexFile);
        String logFileContents = FileUtils.readFileToString(findLogFile(inputTexFile), "UTF-8");
        return String.format("PDF conversion from Tex-File %s to %s failed! Latex Error:%s\nError Message:\n%s", inputTexFile, outputPDFFile, latexError, generator
                .getErrorMessage());
    }

    private String extractLatexError(File sibling) throws IOException {
        try {
        final List<String> lines = Files.readAllLines(findLogFile(sibling).toPath(), Charset.defaultCharset());
            final List<ProblemMarker> errors = new TexExceptionParser().parseErrors(findLogFile(sibling), lines);
            if( !errors.isEmpty() )
                return StringUtils.join( errors.toArray() );
            return lines.stream()
                    .filter(s -> s.contains("! LaTeX Error:"))
                    .findFirst()
                    .orElse("No meaningful error message could be parsed from logfile.");

        } catch (IOException ioe) {
            log.error("Error reading latex log file: ", ioe);
            return "Error reading latex log file: " + ioe.getMessage();
        }
    }

    /**
     * Find generated pdf file.
     *
     * @param sibling a file located next to the logfile
     * @return the pdf file
     */
    private File findPDFFile(File sibling) {
        return FileUtils.listFiles(sibling.getParentFile(), new String[]{"pdf"}, false)
                        .stream()
                        .filter(file -> FilenameUtils.removeExtension(file.getName()).equals(FilenameUtils.removeExtension(sibling.getName())))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("A generated PDF could not be found to moving it to it's destination."));
    }

    /**
     * Find logfile.
     *
     * @param sibling a file located next to the logfile
     * @return the logfile
     */
    private File findLogFile(File sibling) {
        return FileUtils.listFiles(sibling.getParentFile(), new String[]{"log"}, false)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Unable to find Logfile!"));
    }

    public Collection<File> getAvailableTemplates() {
        return templateEnvironment.getAvailableTemplates();
    }
}
