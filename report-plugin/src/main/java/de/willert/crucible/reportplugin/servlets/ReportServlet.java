/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSING file in the root of this project.
 */

package de.willert.crucible.reportplugin.servlets;

/**
 * Created by czoeller on 16.11.16.
 */

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.willert.crucible.reportplugin.template.FillTemplateHelper;
import de.willert.crucible.reportplugin.template.PDFBuilder;
import de.willert.crucible.reportplugin.template.ReviewTemplate;
import de.willert.crucible.reportplugin.template.exception.TexParserException;
import de.willert.crucible.reportplugin.utils.AuthUtils;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;


public class ReportServlet extends HttpServlet {

    private final TemplateRenderer templateRenderer;
    private final FillTemplateHelper fillTemplateHelper;
    private final PDFBuilder pdfBuilder;
    private final PageBuilderService pageBuilderService;
    private final AuthUtils authUtils;

    private List<String> messages;
    private List<String> lines;
    private STEPS nextStep;
    private String permaId;
    private List<String> unreplacedVariables;

    public ReportServlet(AuthUtils authUtils, PageBuilderService pageBuilderService, TemplateRenderer templateRenderer, FillTemplateHelper fillTemplateHelper, PDFBuilder pdfBuilder) {
        this.pageBuilderService = pageBuilderService;
        this.authUtils = authUtils;
        this.templateRenderer = templateRenderer;
        this.fillTemplateHelper = fillTemplateHelper;
        this.pdfBuilder = pdfBuilder;
        this.lines = new ArrayList<>();
        this.unreplacedVariables = new ArrayList<>();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepareResponse(resp);
        step1(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepareResponse(resp);
        authUtils.checkAuthentication(req, resp);
        nextStep = STEPS.enumConstantForPath( req.getServletPath() );
        switch (nextStep) {
            case TWO:
                step2(req, resp);
                break;
            case THREE:
                step3(req, resp);
                break;
            case ONE:
            default:
                step1(req, resp);
                break;
        }
    }

    private void prepareResponse(HttpServletResponse resp) {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        pageBuilderService.assembler().resources().requireWebResource("de.willert.crucible.reportplugin:report-generate");
    }

    private void step3(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<File> buildPDF;

        updateLines(req);

        try {
            buildPDF = pdfBuilder.buildPDF(pdfBuilder.getTransformFile());
        } catch (TexParserException e) {
            throw new IllegalStateException(
                    "Error while generating pdf.: " + e.getMessage() + " \n\n" + Arrays.toString(e.getStackTrace()));
        }

        if (buildPDF.isPresent()) {
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "inline; Filename=\"" + buildPDF.get()
                                                                                  .getAbsolutePath() + "\";");
            resp.setContentLength((int) buildPDF.get()
                                                .length());
            OutputStream output = resp.getOutputStream();
            output.write(FileUtils.readFileToByteArray(buildPDF.get()));
            output.close();
        } else {
            throw new IllegalStateException("Error while generating pdf.");
        }
    }

    private void step2(HttpServletRequest req, HttpServletResponse response) throws IOException {


        if( req.getParameterMap().containsKey("select-template")) {
            String templateFileHash = req.getParameter("select-template");
            final Optional<File> templateFile = pdfBuilder.getAvailableTemplates()
                                                   .stream()
                                                   .filter(file -> 0 == Integer.compare(file.hashCode(),
                                                                                        Integer.parseInt(
                                                                                                templateFileHash)))
                                                   .findFirst();
            if( templateFile.isPresent() ) {
                pdfBuilder.setTemplateFile(templateFile.get());
            }
        }

        updateLines(req);

        unreplacedVariables = findUnreplacedVariables(lines);
        for (Iterator<String> iterator = unreplacedVariables.iterator(); iterator.hasNext();) {
            String unreplacedVariable = iterator.next();
            if( req.getParameterMap().containsKey(unreplacedVariable) ) {
                lines.replaceAll(s -> s.replaceAll("\\"+unreplacedVariable+"", req.getParameter(unreplacedVariable)));
                iterator.remove();
            }
        }
        Files.write( pdfBuilder.getTransformFile().toPath(), lines);
        lines = FileUtils.readLines(pdfBuilder.getTransformFile(), "UTF-8");
        templateRenderer.render("view/generatepdf-preview.vm", ImmutableMap.<String, Object>builder()
                                                                                                     .put("permaId", permaId)
                                                                                                     .put("lines", lines)
                                                                                                     .put("messages", messages)
                                                                                                     .put("unreplacedVariables", unreplacedVariables)
                                                                                                     .build(), response.getWriter());
    }

    private void updateLines(HttpServletRequest req) throws IOException {
        if( !req.getParameterMap().containsKey("lines")) {

            final ReviewTemplate reviewTemplate = fillTemplateHelper.fillTemplate(permaId);
            File transformFile = pdfBuilder.transformToTex(reviewTemplate, pdfBuilder.getTemplateFile(), pdfBuilder.getTransformFile())
                                           .orElseThrow(IllegalArgumentException::new);
            lines = FileUtils.readLines(transformFile, "UTF-8");
            checkSanity(reviewTemplate);
        } else {
            lines = Lists.newArrayList(req.getParameter("lines").split("\\n"));
            Files.write( pdfBuilder.getTransformFile().toPath(), lines);
        }
    }

    private void step1(HttpServletRequest req, HttpServletResponse response) throws IOException {
        permaId = req.getParameter("permaId");
        if (null == permaId) {
            throw new IllegalStateException("No review for reportplugin generation passed. Please just use the UI to get here.");
        }
        templateRenderer.render("view/generatepdf.vm", ImmutableMap.<String, Object>builder().put("permaId", permaId)
                                                                                             .put("availableTemplates", pdfBuilder.getAvailableTemplates())
                                                                                             .build(), response.getWriter());
    }

    private void checkSanity(ReviewTemplate reviewTemplate) {
        messages = Lists.newArrayList();
        if (0 > reviewTemplate.getVersionedComments().size()) {
            messages.add("Review does not contain any comments on files!");
        }

        if (3 > reviewTemplate.getReviewers().size()) {
            messages.add(String.format("Only %d reviewers attended this review! Is this review of good quality?", reviewTemplate
                    .getReviewers()
                    .size()));
        }
    }

    public List<String> findUnreplacedVariables(final List<String> lines) throws IOException {
        List<String> unreplaced = new Vector<>();

        final Predicate<String> stringPredicate = Pattern.compile("\\$\\w+\\b(?!\\.class)").asPredicate();

        lines.stream().filter(stringPredicate)
                      .map(String::trim)
                      .forEach(unreplaced::add);

        return unreplaced;
    }

}
