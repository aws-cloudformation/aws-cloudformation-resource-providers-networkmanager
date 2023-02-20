package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.networkmanager.model.ProposedSegmentChange;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.cloudformation.proxy.delay.Constant;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    public static Set<Tag> cfnTagsToSdkTags(final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags) {
        if (tags == null) {
            return new HashSet<>();
        }
        final Set<Tag> networkManagerTags =
                tags.stream()
                        .map(e -> Tag.builder()
                                .key(e.getKey())
                                .value(e.getValue())
                                .build())
                        .collect(Collectors.toSet());
        return networkManagerTags;
    }

    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is NetworkManager SDK Tag object and Tag2 is cloudformation Tag object
     */
    public static Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> sdkTagsToCfnTags(final Set<Tag> tags) {
        if (tags == null) {
            return new HashSet<>();
        }
        return tags.stream()
                .map(e -> com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag.builder()
                        .key(e.key())
                        .value(e.value())
                        .build()).collect(Collectors.toSet());
    }

    public static Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> jsonTagsToCfnTags(Map<String, String> jsonTags) {
        Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags = new HashSet<>();
        if(jsonTags != null){
            for (Map.Entry<String, String> entry : jsonTags.entrySet()) {
                com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag tag =
                        com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag
                                .builder().key(entry.getKey())
                                .value(entry.getValue())
                                .build();
                tags.add(tag);
            }
        }
        return tags;
    }

    public static Map<String, String> cfnTagsToJsonTags(Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> cfnTags) {
        List<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tagList = new ArrayList<>(cfnTags);
        Map<String, String> jsonTags = new HashMap<>();
        for (com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag tag: tagList) {
            jsonTags.put(tag.getKey(), tag.getValue());
        }
        return jsonTags;
    }

    /**
     * Merge Tags from cloudformation stack
     * @param modelTags
     * @param desiredResourceTags
     * @return mergedTags
     */
    public static Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> mergeTags(
            Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new HashSet<>();
        }
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags = jsonTagsToCfnTags(desiredResourceTags);

        if(tags.isEmpty()) {
            return modelTags;
        } else if(modelTags == null || modelTags.isEmpty()) {
            return tags;
        } else {
            return Stream.concat(modelTags.stream(), tags.stream())
                    .collect(Collectors.toSet());
        }
    }

    public static com.amazonaws.networkmanager.transitgatewayroutetableattachment.ProposedSegmentChange sdkSegmentChangeToCfnSegmentChange(
            ProposedSegmentChange proposedSegmentChange) {
        if (proposedSegmentChange == null) {
            return com.amazonaws.networkmanager.transitgatewayroutetableattachment.ProposedSegmentChange.builder().build();
        }
        return com.amazonaws.networkmanager.transitgatewayroutetableattachment.ProposedSegmentChange.builder()
                .attachmentPolicyRuleNumber(proposedSegmentChange.attachmentPolicyRuleNumber())
                .segmentName(proposedSegmentChange.segmentName())
                .tags((sdkTagsToCfnTags(new HashSet<>(proposedSegmentChange.tags()))))
                .build();
    }

    public static Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tagsDifference(
            Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags1,
            Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags2) {
        return Sets.difference(tags1, tags2).immutableCopy();
    }

    public static Set<Tag> taglistToSet(final List<Tag> tags) {
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static String getTgwRtbAttachmentArn(String resourceId, String resourceArn, String ownerAccountId) {
        String[] arnSplits = resourceArn.split(":");
        String awsPartition = arnSplits[1];
        return "arn:" + awsPartition + ":" + "networkmanager" + "::" + ownerAccountId + ":" + "attachment" + "/" + resourceId;
    }

    // Remove this method once add corenetwork arn
    public static String getCoreNetworkArn(String coreNetworkId, String resourceArn, String ownerAccountId) {
        String[] arnSplits = resourceArn.split(":");
        String awsPartition = arnSplits[1];
        return "arn:" + awsPartition + ":" + "networkmanager" + "::" + ownerAccountId + ":" + "core-network" + "/" + coreNetworkId;
    }

    // TransitGatewayRouteTableAttachment Create/Delete can take up to hours due to some intermittent issues
    public static Constant getBackOffStrategy() {
        return Constant.of()
                .timeout(Duration.ofMinutes(90))
                .delay(Duration.ofSeconds(20))
                .build();
    }
}
