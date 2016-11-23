package de.willert.crucible.reportplugin.template;

import com.atlassian.crucible.spi.PermId;
import com.atlassian.crucible.spi.data.*;
import com.atlassian.crucible.spi.services.ReviewService;
import org.apache.commons.collections.ListUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.crucible.spi.data.FisheyeReviewItemData.FileType.Directory;
import static java.util.stream.Collectors.toList;

/**
 * Provisions @{@link ReviewTemplate} with data.
 * Created by czoeller on 19.08.16.
 */
public class FillTemplateHelper {

    private final ReviewService reviewService;
    private final ReviewTemplate reviewTemplate;

    public FillTemplateHelper(ReviewService reviewService, ReviewTemplate reviewTemplate) {
        this.reviewService = reviewService;
        this.reviewTemplate = reviewTemplate;
    }

    /**
     * Provisions template
     * @param rId The review id
     * @return template
     */
    public ReviewTemplate fillTemplate(String rId) {
        final PermId<ReviewData> permId = new PermId(rId);
        //final ReviewData reviewData = reviewService.getReview(permId);
        final DetailedReviewData reviewDetails = reviewService.getReviewDetails(permId);
        final List<FisheyeReviewItemData> reviewItems = reviewService.getReviewDetails(permId).getReviewItems().reviewItem;


        reviewTemplate.setTitle( reviewDetails.getName() );
        reviewTemplate.setAuthor( reviewDetails.getAuthor() );
        reviewTemplate.setReviewers( reviewService.getAllReviewers(permId) );
        reviewTemplate.setStartDate( reviewDetails.getCreateDate() );
        reviewTemplate.setReviewItems( reviewItems
                                                .stream()
                                                .filter(reviewItem -> !reviewItem.getFileType().equals(Directory))
                                                .collect(toList()));
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

        List<CommentData> allComments = ListUtils.union( reviewTemplate.getGeneralComments(), reviewTemplate.getVersionedComments() );
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