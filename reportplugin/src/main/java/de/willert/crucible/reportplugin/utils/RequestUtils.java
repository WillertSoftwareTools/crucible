/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * Created by czoeller on 12.12.16.
 */
public class RequestUtils {
    public URI getUri(final HttpServletRequest request)
    {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null)
        {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}
