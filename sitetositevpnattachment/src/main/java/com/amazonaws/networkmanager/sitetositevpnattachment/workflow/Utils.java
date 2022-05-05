package com.amazonaws.networkmanager.sitetositevpnattachment.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.networkmanager.model.ProposedSegmentChange;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {
    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    public static List<Tag> cfnTagsToSdkTags(final List<com.amazonaws.networkmanager.sitetositevpnattachment.Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        final List<Tag> networkManagerTags =
                tags.stream()
                        .map(e -> Tag.builder()
                                .key(e.getKey())
                                .value(e.getValue())
                                .build())
                        .collect(Collectors.toList());
        return networkManagerTags;
    }

    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is NetworkManager SDK Tag object and Tag2 is cloudformation Tag object
     */
    public static List<com.amazonaws.networkmanager.sitetositevpnattachment.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
                .map(e -> com.amazonaws.networkmanager.sitetositevpnattachment.Tag.builder()
                        .key(e.key())
                        .value(e.value())
                        .build()).collect(Collectors.toList());
    }

    /**
     * Merge Tags from cloudformation stack
     * @param modelTags
     * @param desiredResourceTags
     * @return mergedTags
     */
    public static List<com.amazonaws.networkmanager.sitetositevpnattachment.Tag> mergeTags(
            List<com.amazonaws.networkmanager.sitetositevpnattachment.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new ArrayList<>();
        }
        final List<com.amazonaws.networkmanager.sitetositevpnattachment.Tag> tags = new ArrayList<>();
        if(desiredResourceTags != null){
            for (Map.Entry<String, String> entry : desiredResourceTags.entrySet()) {
                com.amazonaws.networkmanager.sitetositevpnattachment.Tag tag = com.amazonaws.networkmanager.sitetositevpnattachment.Tag.builder()
                        .key(entry.getKey())
                        .value(entry.getValue())
                        .build();
                tags.add(tag);
            }
        }
        if(tags.isEmpty()) {
            return modelTags;
        } else if(modelTags == null || modelTags.isEmpty()) {
            return tags;
        } else {
            return Stream.concat(modelTags.stream(), tags.stream())
                    .collect(Collectors.toList());
        }
    }

    public static com.amazonaws.networkmanager.sitetositevpnattachment.ProposedSegmentChange sdkSegmentChangeToCfnSegmentChange(
            ProposedSegmentChange proposedSegmentChange) {
        if (proposedSegmentChange == null) {
            return com.amazonaws.networkmanager.sitetositevpnattachment.ProposedSegmentChange.builder().build();
        }
        return com.amazonaws.networkmanager.sitetositevpnattachment.ProposedSegmentChange.builder()
                .attachmentPolicyRuleNumber(proposedSegmentChange.attachmentPolicyRuleNumber())
                .segmentName(proposedSegmentChange.segmentName())
                .tags(sdkTagsToCfnTags(proposedSegmentChange.tags()))
                .build();
    }

    public static List<Tag> tagsDifference(List<com.amazonaws.networkmanager.sitetositevpnattachment.Tag> tags1,
                                           List<com.amazonaws.networkmanager.sitetositevpnattachment.Tag> tags2) {
        final List<Tag> sdkTags1 = Utils.cfnTagsToSdkTags(tags1);
        final List<Tag> sdkTags2 = Utils.cfnTagsToSdkTags(tags2);
        return Sets.difference(Utils.taglistToSet(sdkTags1), Utils.taglistToSet(sdkTags2)).immutableCopy().asList();
    }

    public static Set<Tag> taglistToSet(final List<Tag> tags) {
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static String getVpnAttachmentArn(String resourceId, String resourceArn, String ownerAccountId) {
        String[] arnSplits = resourceArn.split(":");
        String awsPartition = arnSplits[1];
        return "arn:" + awsPartition + ":" + "networkmanager" + "::" + ownerAccountId + ":" + "attachment" + "/" + resourceId;
    }
}
