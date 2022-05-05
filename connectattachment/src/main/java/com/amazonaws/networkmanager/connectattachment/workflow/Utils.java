package com.amazonaws.networkmanager.connectattachment.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.networkmanager.model.ConnectAttachmentOptions;
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
    public static List<Tag> cfnTagsToSdkTags(final List<com.amazonaws.networkmanager.connectattachment.Tag> tags) {
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
    public static List<com.amazonaws.networkmanager.connectattachment.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
                .map(e -> com.amazonaws.networkmanager.connectattachment.Tag.builder()
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
    public static List<com.amazonaws.networkmanager.connectattachment.Tag> mergeTags(
            List<com.amazonaws.networkmanager.connectattachment.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new ArrayList<>();
        }
        final List<com.amazonaws.networkmanager.connectattachment.Tag> tags = new ArrayList<>();
        if(desiredResourceTags != null){
            for (Map.Entry<String, String> entry : desiredResourceTags.entrySet()) {
                com.amazonaws.networkmanager.connectattachment.Tag tag = com.amazonaws.networkmanager.connectattachment.Tag.builder()
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

    public static com.amazonaws.networkmanager.connectattachment.ProposedSegmentChange sdkSegmentChangeToCfnSegmentChange(
            ProposedSegmentChange proposedSegmentChange) {
        if (proposedSegmentChange == null) {
            return com.amazonaws.networkmanager.connectattachment.ProposedSegmentChange.builder().build();
        }
        return com.amazonaws.networkmanager.connectattachment.ProposedSegmentChange.builder()
                .attachmentPolicyRuleNumber(proposedSegmentChange.attachmentPolicyRuleNumber())
                .segmentName(proposedSegmentChange.segmentName())
                .tags(sdkTagsToCfnTags(proposedSegmentChange.tags()))
                .build();
    }

    public static List<Tag> tagsDifference(List<com.amazonaws.networkmanager.connectattachment.Tag> tags1,
                                           List<com.amazonaws.networkmanager.connectattachment.Tag> tags2) {
        final List<Tag> sdkTags1 = Utils.cfnTagsToSdkTags(tags1);
        final List<Tag> sdkTags2 = Utils.cfnTagsToSdkTags(tags2);
        return Sets.difference(Utils.listToSet(sdkTags1), Utils.listToSet(sdkTags2)).immutableCopy().asList();
    }

    public static com.amazonaws.networkmanager.connectattachment.ConnectAttachmentOptions sdkOptionsToCfnOptions(ConnectAttachmentOptions options) {
        return com.amazonaws.networkmanager.connectattachment.ConnectAttachmentOptions.builder()
                .protocol(options.protocolAsString())
                .build();
    }

    public static ConnectAttachmentOptions cfnOptionsToSdkOptions(com.amazonaws.networkmanager.connectattachment.ConnectAttachmentOptions options) {
        return ConnectAttachmentOptions.builder()
                .protocol(options.getProtocol())
                .build();
    }

    public static Set<Tag> listToSet(final List<Tag> tags) {
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static String getConnectAttachmentArn(String resourceId, String resourceArn, String ownerAccountId) {
        String[] arnSplits = resourceArn.split(":");
        String awsPartition = arnSplits[1];
        return "arn:" + awsPartition + ":" + "networkmanager" + "::" + ownerAccountId + ":" + "attachment" + "/" + resourceId;
    }
}
