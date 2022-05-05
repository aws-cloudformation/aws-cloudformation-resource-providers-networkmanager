package com.amazonaws.networkmanager.corenetwork.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.networkmanager.model.CoreNetworkEdge;
import software.amazon.awssdk.services.networkmanager.model.CoreNetworkSegment;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {
    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    public static List<Tag> cfnTagsToSdkTags(final List<com.amazonaws.networkmanager.corenetwork.Tag> tags) {
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
     * Merge Tags from cloudformation stack
     * @param modelTags
     * @param desiredResourceTags
     * @return mergedTags
     */

    // Duplicate tag (leak tag issue)
    public static List<com.amazonaws.networkmanager.corenetwork.Tag> mergeTags(
            List<com.amazonaws.networkmanager.corenetwork.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new ArrayList<>();
        }
        final List<com.amazonaws.networkmanager.corenetwork.Tag> tags = new ArrayList<com.amazonaws.networkmanager.corenetwork.Tag>();
        if(desiredResourceTags != null){
            for (Map.Entry<String, String> entry : desiredResourceTags.entrySet()) {
                com.amazonaws.networkmanager.corenetwork.Tag tag = com.amazonaws.networkmanager.corenetwork.Tag.builder()
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

    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is NetworkManager SDK Tag object and Tag2 is cloudformation Tag object
     */
    public static List<com.amazonaws.networkmanager.corenetwork.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        final List<com.amazonaws.networkmanager.corenetwork.Tag> cfnTags =
                tags.stream()
                        .map(e -> com.amazonaws.networkmanager.corenetwork.Tag.builder()
                                .key(e.key())
                                .value(e.value())
                                .build())
                        .collect(Collectors.toList());
        return cfnTags;
    }

    public static List<com.amazonaws.networkmanager.corenetwork.CoreNetworkEdge> sdkEdgeToCfnEdge(
            final List<CoreNetworkEdge> edges) {
        if (edges.isEmpty()) {
            return new ArrayList<>();
        }
        return edges.stream()
                .map(edge -> com.amazonaws.networkmanager.corenetwork.CoreNetworkEdge.builder()
                        .asn((double)edge.asn())
                        .edgeLocation(edge.edgeLocation())
                        .insideCidrBlocks(edge.insideCidrBlocks())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<com.amazonaws.networkmanager.corenetwork.CoreNetworkSegment> sdkSegmentToCfnSegment(
            final List<CoreNetworkSegment> segments) {
        if (segments.isEmpty()) {
            return new ArrayList<>();
        }
        return segments.stream()
                .map(segment -> com.amazonaws.networkmanager.corenetwork.CoreNetworkSegment.builder()
                        .name(segment.name())
                        .edgeLocations(segment.edgeLocations())
                        .sharedSegments(segment.sharedSegments())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<Tag> tagsDifference(List<com.amazonaws.networkmanager.corenetwork.Tag> tags1,
                                       List<com.amazonaws.networkmanager.corenetwork.Tag> tags2) {
        final List<Tag> sdkTags1 = Utils.cfnTagsToSdkTags(tags1);
        final List<Tag> sdkTags2 = Utils.cfnTagsToSdkTags(tags2);
        return Sets.difference(Utils.listToSet(sdkTags1), Utils.listToSet(sdkTags2)).immutableCopy().asList();
    }

    public static Set<Tag> listToSet(final List<Tag> tags) {
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static String getCondensedString(String s) {
        String condensedString = s.replace("\n", ""); // For linux
        condensedString = condensedString.replace("\r", ""); // For Mac test
        condensedString = condensedString.replace(" ", ""); // Remove all space
        return condensedString;
    }
}
