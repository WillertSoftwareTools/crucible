/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.utils;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by czoeller on 09.12.16.
 */
public class AuthUtils {

    private final LoginUriProvider loginUriProvider;
    private final UserManager userManager;
    private final RequestUtils requestUtils;

    public AuthUtils(LoginUriProvider loginUriProvider, UserManager userManager, RequestUtils requestUtils) {
        this.loginUriProvider = loginUriProvider;
        this.userManager = userManager;
        this.requestUtils = requestUtils;
    }

    public void checkAuthentication(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        UserProfile user = userManager.getRemoteUser(req);
        if (null == user) {
            redirectToLogin(req, resp);
            return;
        }
        if (!userManager.isUserInGroup(user.getUserKey(), "crucible-users")) {
            resp.sendError(403, "You must be a registered user to generate reports.");
            return;
        }
    }

    public void redirectToLogin(final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        response.sendRedirect(loginUriProvider.getLoginUri(requestUtils.getUri(request)).toASCIIString());
    }

}
