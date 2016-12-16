/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.servlets;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.ImmutableMap;
import de.willert.crucible.reportplugin.config.Configuration;
import de.willert.crucible.reportplugin.utils.AuthUtils;
import de.willert.crucible.reportplugin.utils.RequestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;

public class ConfigurationServlet extends HttpServlet
{
    private final RequestUtils requestUtils;
    private final Configuration configuration;
    private final TemplateRenderer templateRenderer;
    private final PageBuilderService pageBuilderService;
    private final AuthUtils authUtils;

    public ConfigurationServlet(AuthUtils authUtils, RequestUtils requestUtils, Configuration configuration, TemplateRenderer templateRenderer, PageBuilderService pageBuilderService)
    {
        this.authUtils = authUtils;
        this.requestUtils = requestUtils;
        this.configuration = configuration;
        this.templateRenderer = templateRenderer;
        this.pageBuilderService = pageBuilderService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("text/html");
        authUtils.checkAuthentication(req, resp);

        final ImmutableMap.Builder<String, Object> ctx = ImmutableMap.<String, Object>builder().put("configuration",
                                                                                                    configuration)
                                                                                               .put("keys",
                                                                                                    configuration.getKeys());
        if(req.getParameterMap().containsKey("status")) {
            ctx.put("status", req.getParameter("status"));
        }
        templateRenderer.render("view/config.vm",
                                ctx.build(),
                                resp.getWriter());
        resp.getWriter().close();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        authUtils.checkAuthentication(req, resp);
        req.getParameterMap().keySet().stream().filter(s -> ((String) s).contains("config-")).forEach(o -> {
            String strippedOptionName = ((String) o).replace("config-", "");
            configuration.put( configuration.get(strippedOptionName ), req.getParameter((String) o));
        });

        final String redirect = UriBuilder.fromUri(requestUtils.getUri(req))
                                          .replaceQueryParam("status", "success")
                                          .build()
                                          .toASCIIString();
        resp.sendRedirect( redirect );
        resp.getWriter().close();
    }

}

