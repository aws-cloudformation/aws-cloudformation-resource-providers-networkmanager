package com.amazonaws.networkmanager.link;

import software.amazon.awssdk.services.networkmanager.model.Link;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Utils {
    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is Cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    static List<Tag> cfnTagsToSdkTags(final List<com.amazonaws.networkmanager.link.Tag> tags) {
        if (tags == null) {
            return new ArrayList<Tag>();
        }
        for (final com.amazonaws.networkmanager.link.Tag tag : tags) {
            if (tag.getKey() == null) {
                throw new CfnInvalidRequestException("Tags cannot have a null key");
            }
            if (tag.getValue() == null) {
                throw new CfnInvalidRequestException("Tags cannot have a null value");
            }
        }
        return tags.stream()
                .map(e -> Tag.builder()
                        .key(e.getKey())
                        .value(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Merge Tags from CloudFormation stack
     * @param modelTags
     * @param desiredResourceTags
     * @return mergedTags
     */

    static List<com.amazonaws.networkmanager.link.Tag> mergeTags(
            List<com.amazonaws.networkmanager.link.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new ArrayList<com.amazonaws.networkmanager.link.Tag>();
        }
        final List<com.amazonaws.networkmanager.link.Tag> tags = new ArrayList<com.amazonaws.networkmanager.link.Tag>();
        if(desiredResourceTags != null){
            for (Map.Entry<String, String> entry : desiredResourceTags.entrySet()) {
                com.amazonaws.networkmanager.link.Tag tag = com.amazonaws.networkmanager.link.Tag.builder().key(entry.getKey()).value(entry.getValue()).build();
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
     * where Tag1 is NetworkManager SDK Tag object and Tag2 is Cloudformation Tag object
     */
    static List<com.amazonaws.networkmanager.link.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        return tags.stream()
                .map(e -> com.amazonaws.networkmanager.link.Tag.builder()
                        .key(e.key())
                        .value(e.value())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Converter method to convert NetworkManager SDK Link to CFN ResourceModel for LIST/READ request
     */
    static ResourceModel transformLink(final Link link) {
        return ResourceModel.builder()
                .linkId(link.linkId())
                .linkArn(link.linkArn())
                .globalNetworkId(link.globalNetworkId())
                .siteId(link.siteId())
                .description(link.description())
                .bandwidth(transformBandwidth(link.bandwidth()))
                .tags(Utils.sdkTagsToCfnTags(link.tags()))
                .provider(link.provider())
                .type(link.type())
                .build();
    }

    /**
     * Converter method to convert CFN link bandwidth to NetworkManager SDK link bandwidth
     */
    static software.amazon.awssdk.services.networkmanager.model.Bandwidth transformBandwidth(final Bandwidth bandwidth) {
        return bandwidth == null ? null : software.amazon.awssdk.services.networkmanager.model.Bandwidth.builder()
                .downloadSpeed(bandwidth.getDownloadSpeed())
                .uploadSpeed(bandwidth.getUploadSpeed())
                .build();
    }

    /**
     * Converter method to convert NetworkManager SDK link bandwidth to CFN link bandwidth
     */
    static Bandwidth transformBandwidth(final software.amazon.awssdk.services.networkmanager.model.Bandwidth bandwidth) {
        return bandwidth == null ? null : Bandwidth.builder()
                .downloadSpeed(bandwidth.downloadSpeed())
                .uploadSpeed(bandwidth.uploadSpeed())
                .build();
    }

}
