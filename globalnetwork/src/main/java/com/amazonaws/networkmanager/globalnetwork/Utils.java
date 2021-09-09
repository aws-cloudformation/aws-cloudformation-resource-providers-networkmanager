package com.amazonaws.networkmanager.globalnetwork;

import software.amazon.awssdk.services.networkmanager.model.GlobalNetwork;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Utils {
    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    static List<Tag> cfnTagsToSdkTags(final List<com.amazonaws.networkmanager.globalnetwork.Tag> tags) {
        if (tags == null) return null;
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

    static List<com.amazonaws.networkmanager.globalnetwork.Tag> mergeTags(
            List<com.amazonaws.networkmanager.globalnetwork.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new ArrayList<com.amazonaws.networkmanager.globalnetwork.Tag>();
        }
        final List<com.amazonaws.networkmanager.globalnetwork.Tag> tags = new ArrayList<com.amazonaws.networkmanager.globalnetwork.Tag>();
        if(desiredResourceTags != null){
            for (Map.Entry<String, String> entry : desiredResourceTags.entrySet()) {
                com.amazonaws.networkmanager.globalnetwork.Tag tag = com.amazonaws.networkmanager.globalnetwork.Tag.builder().key(entry.getKey()).value(entry.getValue()).build();
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
    static List<com.amazonaws.networkmanager.globalnetwork.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) return null;
        final List<com.amazonaws.networkmanager.globalnetwork.Tag> cfnTags =
                tags.stream()
                        .map(e -> com.amazonaws.networkmanager.globalnetwork.Tag.builder()
                                .key(e.key())
                                .value(e.value())
                                .build())
                        .collect(Collectors.toList());
        return cfnTags;
    }
    /**
     * Converter method to convert GlobalNetwork to CFN ResourceModel for LIST/READ request
     */
    static ResourceModel transformGlobalNetwork(final GlobalNetwork globalNetwork) {
        final ResourceModel resourceModel = ResourceModel.builder().build();

        resourceModel.setDescription(globalNetwork.description());
        resourceModel.setTags(Utils.sdkTagsToCfnTags(globalNetwork.tags()));
        resourceModel.setArn(globalNetwork.globalNetworkArn());
        resourceModel.setId(globalNetwork.globalNetworkId());
        return resourceModel;
    }

}
