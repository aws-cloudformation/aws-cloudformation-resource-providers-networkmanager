package com.amazonaws.networkmanager.transitgatewaypeering.workflow;

import com.google.common.collect.Sets;
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
    public static Set<Tag> cfnTagsToSdkTags(final Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> tags) {
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
    public static Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> sdkTagsToCfnTags(final Set<Tag> tags) {
        if (tags == null) {
            return new HashSet<>();
        }
        return tags.stream()
                .map(e -> com.amazonaws.networkmanager.transitgatewaypeering.Tag.builder()
                        .key(e.key())
                        .value(e.value())
                        .build()).collect(Collectors.toSet());
    }

    public static Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> jsonTagsToCfnTags(Map<String, String> jsonTags) {
        Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> tags = new HashSet<>();
        if(jsonTags != null){
            for (Map.Entry<String, String> entry : jsonTags.entrySet()) {
                com.amazonaws.networkmanager.transitgatewaypeering.Tag tag =
                        com.amazonaws.networkmanager.transitgatewaypeering.Tag
                                .builder().key(entry.getKey())
                                .value(entry.getValue())
                                .build();
                tags.add(tag);
            }
        }
        return tags;
    }

    public static Map<String, String> cfnTagsToJsonTags(Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> cfnTags) {
        List<com.amazonaws.networkmanager.transitgatewaypeering.Tag> tagList = new ArrayList<>(cfnTags);
        Map<String, String> jsonTags = new HashMap<>();
        for (com.amazonaws.networkmanager.transitgatewaypeering.Tag tag: tagList) {
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
    public static Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> mergeTags(
            Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new HashSet<>();
        }
        final Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> tags = jsonTagsToCfnTags(desiredResourceTags);

        if(tags.isEmpty()) {
            return modelTags;
        } else if(modelTags == null || modelTags.isEmpty()) {
            return tags;
        } else {
            return Stream.concat(modelTags.stream(), tags.stream())
                    .collect(Collectors.toSet());
        }
    }

    public static String getPeeringArn(String resourceId, String resourceArn, String ownerAccountId) {
        String[] arnSplits = resourceArn.split(":");
        String awsPartition = arnSplits[1];
        return "arn:" + awsPartition + ":" + "networkmanager" + "::" + ownerAccountId + ":" + "peering" + "/" + resourceId;
    }

    public static Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> tagsDifference(
            Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> tags1,
            Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> tags2) {
        return Sets.difference(tags1, tags2).immutableCopy();
    }

    // TransitGatewayPeering Create/Delete can take up to hours due to some intermittent issues
    public static Constant getBackOffStrategy() {
        return Constant.of()
                .timeout(Duration.ofMinutes(90))
                .delay(Duration.ofSeconds(20))
                .build();
    }
}
