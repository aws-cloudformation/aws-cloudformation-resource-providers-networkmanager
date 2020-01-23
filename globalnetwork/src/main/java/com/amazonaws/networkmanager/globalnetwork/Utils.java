package com.amazonaws.networkmanager.globalnetwork;

import software.amazon.awssdk.services.networkmanager.model.GlobalNetwork;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

final class Utils {
    /**
     * Converter method to convert Set<Tag1> to Collection<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    static Collection<Tag> tagTransform(final List<com.amazonaws.networkmanager.globalnetwork.Tag> tags) {
        if (tags == null) return null;
        final Collection<Tag> networkManagerTags =
                tags.stream()
                        .map(e -> Tag.builder()
                                .key(e.getKey())
                                .value(e.getValue())
                                .build())
                        .collect(Collectors.toList());
        return networkManagerTags;
    }

    /**
     * Converter method to convert Set<Tag1> to Collection<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    static List<com.amazonaws.networkmanager.globalnetwork.Tag> tagTransformCFN(final List<Tag> tags) {
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
        resourceModel.setTags(Utils.tagTransformCFN(globalNetwork.tags()));
        resourceModel.setArn(globalNetwork.globalNetworkArn());
        resourceModel.setId(globalNetwork.globalNetworkId());
        return resourceModel;
    }



}
