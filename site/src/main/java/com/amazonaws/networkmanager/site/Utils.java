package com.amazonaws.networkmanager.site;

import software.amazon.awssdk.services.networkmanager.model.Site;
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
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    static List<Tag> cfnTagsToSdkTags(final List<com.amazonaws.networkmanager.site.Tag> tags) {
        if (tags == null) {
            return new ArrayList<Tag>();
        }
        for (final com.amazonaws.networkmanager.site.Tag tag : tags) {
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
     * Merge Tags from cloudformation stack
     * @param modelTags
     * @param desiredResourceTags
     * @return mergedTags
     */

    static List<com.amazonaws.networkmanager.site.Tag> mergeTags(
            List<com.amazonaws.networkmanager.site.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new ArrayList<com.amazonaws.networkmanager.site.Tag>();
        }
        final List<com.amazonaws.networkmanager.site.Tag> tags = new ArrayList<com.amazonaws.networkmanager.site.Tag>();
        if(desiredResourceTags != null){
            for (Map.Entry<String, String> entry : desiredResourceTags.entrySet()) {
                com.amazonaws.networkmanager.site.Tag tag = com.amazonaws.networkmanager.site.Tag.builder().key(entry.getKey()).value(entry.getValue()).build();
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
    static List<com.amazonaws.networkmanager.site.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        return tags.stream()
                .map(e -> com.amazonaws.networkmanager.site.Tag.builder()
                        .key(e.key())
                        .value(e.value())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Converter method to convert Site to CFN ResourceModel for LIST/READ request
     */
    static ResourceModel transformSite(final Site site) {
        return ResourceModel.builder()
                .siteId(site.siteId())
                .siteArn(site.siteArn())
                .globalNetworkId(site.globalNetworkId())
                .description(site.description())
                .location(transformLocation(site.location()))
                .tags(Utils.sdkTagsToCfnTags(site.tags()))
                .build();
    }

    /**
     * Converter method to convert CFN site location to SDK site location
     */
    static software.amazon.awssdk.services.networkmanager.model.Location transformLocation(final Location location) {
        return location == null ? null : software.amazon.awssdk.services.networkmanager.model.Location.builder()
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    /**
     * Converter method to convert SDK site location to CFN site location
     */
    static Location transformLocation(final software.amazon.awssdk.services.networkmanager.model.Location location) {
        return location == null ? null : Location.builder()
                .address(location.address())
                .latitude(location.latitude())
                .longitude(location.longitude())
                .build();
    }

}
