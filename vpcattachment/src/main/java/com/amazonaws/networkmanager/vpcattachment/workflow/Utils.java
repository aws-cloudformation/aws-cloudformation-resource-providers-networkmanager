package com.amazonaws.networkmanager.vpcattachment.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.networkmanager.model.ProposedSegmentChange;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.awssdk.services.networkmanager.model.VpcOptions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {
    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    public static List<Tag> cfnTagsToSdkTags(final List<com.amazonaws.networkmanager.vpcattachment.Tag> tags) {
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
    public static List<com.amazonaws.networkmanager.vpcattachment.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
                .map(e -> com.amazonaws.networkmanager.vpcattachment.Tag.builder()
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
    public static List<com.amazonaws.networkmanager.vpcattachment.Tag> mergeTags(
            List<com.amazonaws.networkmanager.vpcattachment.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new ArrayList<>();
        }
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> tags = new ArrayList<>();
        if(desiredResourceTags != null){
            for (Map.Entry<String, String> entry : desiredResourceTags.entrySet()) {
                com.amazonaws.networkmanager.vpcattachment.Tag tag = com.amazonaws.networkmanager.vpcattachment.Tag.builder()
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

    public static VpcOptions cfnOptionsToSdkOptions(final com.amazonaws.networkmanager.vpcattachment.VpcOptions options) {
        boolean ipv6Support = options == null? false: options.getIpv6Support();
        return VpcOptions.builder()
                .ipv6Support(ipv6Support)
                .build();
    }

    public static com.amazonaws.networkmanager.vpcattachment.VpcOptions sdkOptionsToCfnOptions(final VpcOptions options) {
        return com.amazonaws.networkmanager.vpcattachment.VpcOptions.builder()
                .ipv6Support(options.ipv6Support())
                .build();
    }

    public static com.amazonaws.networkmanager.vpcattachment.ProposedSegmentChange sdkSegmentChangeToCfnSegmentChange(
            ProposedSegmentChange proposedSegmentChange) {
        if (proposedSegmentChange == null) {
            return com.amazonaws.networkmanager.vpcattachment.ProposedSegmentChange.builder().build();
        }
        return com.amazonaws.networkmanager.vpcattachment.ProposedSegmentChange.builder()
                .attachmentPolicyRuleNumber(proposedSegmentChange.attachmentPolicyRuleNumber())
                .segmentName(proposedSegmentChange.segmentName())
                .tags(sdkTagsToCfnTags(proposedSegmentChange.tags()))
                .build();
    }

    public static List<Tag> tagsDifference(List<com.amazonaws.networkmanager.vpcattachment.Tag> tags1,
                                           List<com.amazonaws.networkmanager.vpcattachment.Tag> tags2) {
        final List<Tag> sdkTags1 = Utils.cfnTagsToSdkTags(tags1);
        final List<Tag> sdkTags2 = Utils.cfnTagsToSdkTags(tags2);
        return Sets.difference(Utils.taglistToSet(sdkTags1), Utils.taglistToSet(sdkTags2)).immutableCopy().asList();
    }

    public static List<String> subnetArnsDifference(List<String> subnetArns1, List<String> subnetArns2) {
        return Sets.difference(Utils.subnetArnsListToSet(subnetArns1), Utils.subnetArnsListToSet(subnetArns2)).immutableCopy().asList();
    }

    public static Set<Tag> taglistToSet(final List<Tag> tags) {
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static Set<String> subnetArnsListToSet(final List<String> subnetArns) {
        return CollectionUtils.isEmpty(subnetArns) ? new HashSet<>() : new HashSet<> (subnetArns);
    }

    //arn:<partition>:<vendor>:<region>:<namespace>:<relative-id>
    //arn:aws:ec2:us-west-1:564563714463:vpc/vpc-008c44ed82b80aed8
    public static String getVpcAttachmentArn(String resourceId, String resourceArn, String ownerAccountId) {
        System.out.println(resourceArn);
        String[] arnSplits = resourceArn.split(":");
        String awsPartition = arnSplits[1];
        return "arn:" + awsPartition + ":" + "networkmanager" + "::" + ownerAccountId + ":" + "attachment" + "/" + resourceId;
    }
}
