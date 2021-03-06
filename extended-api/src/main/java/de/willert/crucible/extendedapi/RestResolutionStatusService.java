/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.extendedapi;

import com.atlassian.crucible.spi.PermId;
import com.atlassian.crucible.spi.data.CommentData;
import com.atlassian.crucible.spi.services.NotFoundException;
import com.atlassian.crucible.spi.services.NotPermittedException;
import com.atlassian.crucible.spi.services.ReviewService;
import org.apache.commons.lang.math.NumberUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/comments")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class RestResolutionStatusService {

    public static final String ERROR_ID_NOT_FOUND = "No comment with this id found.";
    public static final String ERROR_ID_INVALID = "Id passed is not valid";
    public static final String ERROR_NOT_PERMITTED = "Not permitted";

    private final ReviewService reviewService;

    public RestResolutionStatusService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Gets comment resolution data.
     * <p>
     * comments/{cId}/resolutionStatus
     * <p>
     * {@link com.atlassian.crucible.spi.data.CommentResolutionData}
     * {@link com.atlassian.crucible.spi.data.CommentResolutionStatus}
     *
     * @param cId The comment Id
     * @return Comment Resolution
     */
    @GET
    @Path("{cId}/resolutionStatus")
    public Response resolutionStatus(@PathParam("cId") String cId) {
        final PermId<CommentData> commentPermId;

        if (cId.isEmpty() || !NumberUtils.isNumber(cId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ERROR_ID_INVALID).build();
        }

        commentPermId = new PermId<>(cId);

        try {
            final com.atlassian.crucible.spi.data.CommentResolutionData commentResolution = reviewService.getCommentResolution(commentPermId);
            final CommentResolutionData commentResolutionData = new CommentResolutionData(commentResolution);
            return Response.ok().entity(commentResolutionData).build();
        } catch (NotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND).entity(ERROR_ID_NOT_FOUND).build();
        } catch (NotPermittedException npe) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ERROR_NOT_PERMITTED).build();
        } catch (IllegalStateException ise) {
            return Response.serverError().entity(ise.getMessage()).build();
        }
    }
}
