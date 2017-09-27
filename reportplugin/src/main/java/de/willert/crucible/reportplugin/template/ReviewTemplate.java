/*
 * Willert Software Tools GmbH
 * Copyright (C) 2016 Willert Software Tools GmbH
 * This file is covered by the LICENSING file in the root of this project.
 */

package de.willert.crucible.reportplugin.template;

import com.atlassian.crucible.spi.PermId;
import com.atlassian.crucible.spi.data.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fields must be initialized.
 * Created by czoeller on 31.08.16.
 */
public class ReviewTemplate {

    private final WikiLatexRenderer wikiLatexRenderer;
    private String title;
    private UserData author;
    private Set<ReviewerData> reviewers;
    private String reviewersEnumerated;
    private Date startDate;
    private List<FisheyeReviewItemData> reviewItems;
    private List<CommentDataImpl> generalComments;
    private List<CommentDataImpl> versionedComments;
    private PermId<ReviewData> permaId;
    private Date version;
    private Long latestRevision;
    private Map<String, CommentResolutionData> resolutions;
    private String description;

    public ReviewTemplate(WikiLatexRenderer wikiLatexRenderer) {
        this.wikiLatexRenderer = wikiLatexRenderer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(UserData author) {
        this.author = author;
    }

    public UserData getAuthor() {
        return author;
    }

    public void setReviewers(Set<ReviewerData> reviewers) {
        this.reviewers = reviewers;
    }

    public Set<ReviewerData> getReviewers() {
        return reviewers;
    }

    public void setReviewersEnumerated(String reviewersEnumerated) {
        this.reviewersEnumerated = reviewersEnumerated;
    }

    public String getReviewersEnumerated() {
        return reviewersEnumerated;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public List<FisheyeReviewItemData> getReviewItems() {
        return reviewItems;
    }

    public void setReviewItems(List<FisheyeReviewItemData> reviewItems) {
        this.reviewItems = reviewItems;
    }

    public void setGeneralComments(List<CommentDataImpl> generalComments) {
        this.generalComments = generalComments;
    }

    public List<CommentDataImpl> getGeneralComments() {
        return generalComments;
    }

    public void setVersionedComments(List<CommentDataImpl> versionedComments) {
        this.versionedComments = versionedComments;
    }

    public List<CommentDataImpl> getVersionedComments() {
        return versionedComments;
    }

    public void setPermaId(PermId<ReviewData> permaId) {
        this.permaId = permaId;
    }

    public PermId<ReviewData> getPermaId() {
        return permaId;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public Date getVersion() {
        return version;
    }

    public void setLatestRevision(Long latestRevision) {
        this.latestRevision = latestRevision;
    }

    public Long getLatestRevision() {
        return latestRevision;
    }

    public void setResolutions(Map<String, CommentResolutionData> resolutions) {
        this.resolutions = resolutions;
    }

    public Map<String, CommentResolutionData> getResolutions() {
        return resolutions;
    }

    public String getGeneralCommentsRendered() {
        return renderComments(getGeneralComments());
    }

    public String getVersionedCommentsRendered(String rId) {
        final List<VersionedCommentData> versionedCommentData = getVersionedComments().stream().map(commentData -> (VersionedCommentData) commentData).collect(Collectors.toList());
        final List<CommentDataImpl> collect = versionedCommentData.stream()
                                                                  .filter(c -> c.getReviewItemId().getId()
                                                                                .equals(rId))
                                                                  .collect(Collectors.toList());
        return renderComments(collect);

    }

    private String renderComments(List<? extends CommentData> comments) {
        if (comments.isEmpty()) {
            return "";
        }

        StringBuilder output = new StringBuilder();

        output.append("\\begin{itemize}\n");
        comments.forEach( c -> {
            output.append(printComment(c));
            c.getReplies().forEach(r -> output.append(buildReply(r, "\t")));
        });
        output.append("\\end{itemize}\n");

        return output.toString();
    }

    private String buildReply(CommentData reply, String indent) {
        String output = "";
        output += indent + "\\begin{itemize}\n";
        output += indent + printComment(reply);
        for (CommentData r : reply.getReplies()) {
            output += indent + buildReply(r, indent + "\t");
        }
        output += indent + "\\end{itemize}\n";
        return output;
    }

    private String printComment(CommentData comment) {
        String defect = comment.isDefectRaised() ? "(Defect) " : "";
        String resolved = comment.isDefectApproved() ? "(Resolved) " : "";
        String resolution = getResolution(comment);
        return "\t\\item{ " + resolution + "" + defect + "" + resolved + "[" + new SimpleDateFormat().format(comment.getCreateDate()) + "] " + comment.getUser()
                                                                                                                                    .getDisplayName() + ": " + wikiLatexRenderer.markupToLatex(comment
                .getMessage()) + " }\n";
    }

    private String getResolution(CommentData comment) {
        String resolution = "";
        final CommentResolutionData commentResolutionData = getResolutions().get(comment.getPermaIdAsString());
        if( null != commentResolutionData ) {
            resolution = commentResolutionData.getStatus().name();
        }
        return resolution;
    }
}
