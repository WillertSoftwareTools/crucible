/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSING file in the root of this project.
 */

package de.willert.crucible.reportplugin.template;

import com.atlassian.crucible.spi.PermId;
import com.atlassian.crucible.spi.data.*;
import com.atlassian.crucible.spi.services.ReviewService;
import org.apache.commons.collections4.ListUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.atlassian.crucible.spi.data.FisheyeReviewItemData.FileType.Directory;
import static java.util.stream.Collectors.toList;

/**
 * Provisions @{@link ReviewTemplate} with data.
 * Created by czoeller on 19.08.16.
 */
public class FillTemplateHelper {

    private final ReviewService reviewService;
    private final ReviewTemplate reviewTemplate;
    private final WikiLatexRenderer wikiLatexRenderer;

    public FillTemplateHelper(ReviewService reviewService, ReviewTemplate reviewTemplate, WikiLatexRenderer wikiLatexRenderer) {
        this.reviewService = reviewService;
        this.reviewTemplate = reviewTemplate;
        this.wikiLatexRenderer = wikiLatexRenderer;
    }

    /**
     * Provisions template.
     * @param rId The review id
     * @return template
     */
    public ReviewTemplate fillTemplate(String rId) {
        final PermId<ReviewData> permId = new PermId<>(rId);
        final DetailedReviewData reviewDetails = reviewService.getReviewDetails(permId);
        final List<FisheyeReviewItemData> reviewItems = reviewService.getReviewDetails(permId).getReviewItems().reviewItem;

        reviewTemplate.setTitle( reviewDetails.getName() );
        reviewTemplate.setAuthor( reviewDetails.getAuthor() );
        reviewTemplate.setReviewers(reviewService.getAllReviewers(permId));
        reviewTemplate.setReviewersEnumerated(reviewService.getAllReviewers(permId)
                                                           .stream()
                                                           .map(ReviewerData::getDisplayName)
                                                           .map(Object::toString)
                                                           .collect(Collectors.joining(", ")));
        String description = reviewDetails.getDescription();
        String descriptionLatex = wikiLatexRenderer.markupToLatex( description );
        reviewTemplate.setDescription(descriptionLatex);

        reviewTemplate.setStartDate( reviewDetails.getCreateDate() );
        reviewTemplate.setReviewItems( reviewItems
                                                .stream()
                                                .filter(reviewItem -> !reviewItem.getFileType().equals(Directory))
                                                .collect(toList()));

        reviewDetails.getGeneralComments().comments.forEach(comment -> comment.setMessage(wikiLatexRenderer.markupToLatex(comment.getMessage())));
        reviewDetails.getVersionedComments().comments.forEach(comment -> comment.setMessage(wikiLatexRenderer.markupToLatex(comment.getMessage())));
        reviewTemplate.setGeneralComments( reviewDetails.getGeneralComments().comments );
        reviewTemplate.setVersionedComments(reviewDetails.getVersionedComments().comments );
        reviewTemplate.setPermaId(reviewDetails.getPermaId());
        reviewTemplate.setVersion(reviewDetails.getVersionedComments()
                                               .comments
                                               .stream()
                                               .map(CommentData::getCreateDate)
                                               .max(Date::compareTo)
                                               .orElseGet(reviewDetails::getCreateDate));
        reviewTemplate.setLatestRevision(reviewDetails.getReviewItems()
                                                      .reviewItem
                                                      .stream()
                                                      .map(FisheyeReviewItemData::getToRevision)
                                                      .map(Long::valueOf)
                                                      .max(Long::compareTo)
                                                      .orElseThrow(IllegalStateException::new));

        List<CommentData> allComments = ListUtils.union(reviewTemplate.getGeneralComments(), reviewTemplate.getVersionedComments() );
        Map<String, CommentResolutionData> commentResolutionData = new HashMap<>();
        for( CommentData v : allComments ) {
            PermId<CommentData> permaId = new PermId<>(v.getPermaIdAsString());
            final CommentResolutionData commentResolution = reviewService.getCommentResolution(permaId);
            if( null != commentResolution ) {
                commentResolutionData.put(v.getPermaIdAsString(), commentResolution);
            }
        }
        reviewTemplate.setResolutions(commentResolutionData);

        return reviewTemplate;
    }

}
