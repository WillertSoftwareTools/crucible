/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.extendedapi;

import com.atlassian.crucible.spi.data.CommentResolutionStatus;
import com.atlassian.crucible.spi.data.UserData;
import com.sun.istack.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by czoeller on 25.10.16.
 */
@XmlRootElement
public class CommentResolutionData {
    public Date created;
    public CommentResolutionStatus status;
    public UserData user;

    private CommentResolutionData(){}

    public CommentResolutionData(@NotNull com.atlassian.crucible.spi.data.CommentResolutionData resolutionData) {
        this.created = resolutionData.getCreated();
        this.status = resolutionData.getStatus();
        this.user = resolutionData.getUser();
    }
}
