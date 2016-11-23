package de.willert.crucible.reportplugin.servlets;

/**
 * Created by czoeller on 16.11.16.
 */

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.willert.crucible.reportplugin.template.FillTemplateHelper;
import de.willert.crucible.reportplugin.template.PDFBuilder;
import de.willert.crucible.reportplugin.template.ReviewTemplate;
import de.willert.crucible.reportplugin.template.exception.TexParserException;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ReportServlet extends HttpServlet {

    private final UserManager userManager;
    private final TemplateRenderer templateRenderer;
    private final FillTemplateHelper fillTemplateHelper;
    private final PDFBuilder pdfBuilder;
    private final LoginUriProvider loginUriProvider;
    private final PageBuilderService pageBuilderService;

    private List<String> messages;

    private List<String> lines;
    private STEPS STEP;
    private String permaId;
    private List<String> unreplacedVariables;

    public ReportServlet(LoginUriProvider loginUriProvider, PageBuilderService pageBuilderService, UserManager userManager, TemplateRenderer templateRenderer, FillTemplateHelper fillTemplateHelper, PDFBuilder pdfBuilder) {
        this.loginUriProvider = loginUriProvider;
        this.pageBuilderService = pageBuilderService;
        this.userManager = userManager;
        this.templateRenderer = templateRenderer;
        this.fillTemplateHelper = fillTemplateHelper;
        this.pdfBuilder = pdfBuilder;
        this.lines = new ArrayList<>();
        this.unreplacedVariables = new ArrayList<>();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        if (req.getParameterMap().containsKey("lines")) {
            String lines = req.getParameter("lines");
            File overwrittenTransform = pdfBuilder.getTransformFile();
            FileUtils.writeStringToFile(overwrittenTransform, lines, "UTF-8");

            Optional<File> buildPDF = Optional.empty();
            try {
                buildPDF = pdfBuilder.buildPDF(overwrittenTransform);
            } catch (Exception | TexParserException e) {
                throw new IllegalStateException("Error while generating pdf.: " + e.getMessage() + " \n\n" + Arrays.toString(e
                        .getStackTrace()) + "\n\n" + FileUtils.readFileToString( new File(overwrittenTransform.getParentFile().getAbsoluteFile(), overwrittenTransform.getName().replaceAll("\\..+", ".log")) ));
            }

            if (buildPDF.isPresent()) {
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "inline; Filename=\"" + buildPDF.get().getAbsolutePath() + "\";");
                resp.setContentLength((int) buildPDF.get().length());
                OutputStream output = resp.getOutputStream();
                output.write(FileUtils.readFileToByteArray(buildPDF.get()));
                output.close();
            } else {
                throw new IllegalStateException("Error while generating pdf.");
            }
        } else {
            throw new IllegalStateException("No lines passed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameterMap().containsKey("step")) {
            String stepParam = req.getParameter("step");
            STEP = STEPS.valueOf(stepParam);
        } else {
            STEP = STEPS.ONE;
        }

        UserProfile user = userManager.getRemoteUser(req);
        if (null == user) {
            redirectToLogin(req, resp);
            return;
        }
        if (!userManager.isUserInGroup(user.getUserKey(), "crucible-users")) {
            resp.sendError(403, "You must be a registered user to generate reports.");
            return;
        }

        resp.setContentType("text/html");
        pageBuilderService.assembler().resources().requireWebResource("de.willert.crucible.reportplugin:hallo");
        switch (STEP) {
            case TWO:
                step2(req, resp);
                break;
            case ONE:
            default:
                step1(req, resp);
                break;
        }
    }

    private void step2(HttpServletRequest req, HttpServletResponse response) throws IOException {
        if( !req.getParameterMap().containsKey("lines")) {

            final ReviewTemplate reviewTemplate = fillTemplateHelper.fillTemplate(permaId);
            File transformFile = pdfBuilder.transformToTex(reviewTemplate, pdfBuilder.getTemplateFile(), pdfBuilder.getTransformFile())
                                     .orElseThrow(IllegalArgumentException::new);
            lines = FileUtils.readLines(transformFile, "UTF-8");
            checkSanity(reviewTemplate);
        } else {
            lines = Lists.newArrayList(req.getParameter("lines").split("\\n"));
        }


        for( String unreplacedVariable : unreplacedVariables ) {
            if( req.getParameterMap().containsKey(unreplacedVariable) ) {
                lines.replaceAll(s -> s.replaceAll("\\"+unreplacedVariable+"", req.getParameter(unreplacedVariable)));
            }
        }
        unreplacedVariables = findUnreplacedVariables(lines);
        templateRenderer.render("view/generatepdf-preview.vm", ImmutableMap.<String, Object>builder()
                                                                                                     .put("permaId", permaId)
                                                                                                     .put("lines", lines)
                                                                                                     .put("step", STEP)
                                                                                                     .put("messages", messages)
                                                                                                     .put("unreplacedVariables", unreplacedVariables)
                                                                                                     .build(), response.getWriter());
    }

    private void step1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        permaId = request.getParameter("permaId");
        if (null == permaId) {
            response.sendError(400, "No review for reportplugin generation passed. Please just use the UI to get here.");
            return;
        }
        templateRenderer.render("view/generatepdf.vm", ImmutableMap.<String, Object>builder().put("permaId", permaId)
                                                                                             .put("step", STEP)
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

    private String getAPPPath(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        String servletPath = request.getServletPath();
        return requestURL.substring(0, requestURL.indexOf(servletPath));
    }

    private enum STEPS {ONE, TWO}

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }
    private URI getUri(HttpServletRequest request)
    {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null)
        {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

    public List<String> findUnreplacedVariables(final List<String> lines) throws IOException {
        List<String> unreplaced = Lists.newArrayList();

        final Predicate<String> stringPredicate = Pattern.compile("\\$.+").asPredicate();

        final List<String> collect = lines.stream()
                                          .filter(stringPredicate)
                                          .collect(Collectors.toList());
        unreplaced = collect;
        return unreplaced;
    }

}
