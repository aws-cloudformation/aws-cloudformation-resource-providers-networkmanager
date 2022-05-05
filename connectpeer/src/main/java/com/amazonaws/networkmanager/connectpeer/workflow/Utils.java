package com.amazonaws.networkmanager.connectpeer.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.networkmanager.model.BgpOptions;
import software.amazon.awssdk.services.networkmanager.model.ConnectPeerBgpConfiguration;
import software.amazon.awssdk.services.networkmanager.model.ConnectPeerConfiguration;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    public static List<Tag> cfnTagsToSdkTags(final List<com.amazonaws.networkmanager.connectpeer.Tag> tags) {
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
    public static List<com.amazonaws.networkmanager.connectpeer.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
                .map(e -> com.amazonaws.networkmanager.connectpeer.Tag.builder()
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
    public static List<com.amazonaws.networkmanager.connectpeer.Tag> mergeTags(
            List<com.amazonaws.networkmanager.connectpeer.Tag> modelTags,
            final Map<String, String> desiredResourceTags) {
        if(modelTags == null) {
            modelTags = new ArrayList<>();
        }
        final List<com.amazonaws.networkmanager.connectpeer.Tag> tags = new ArrayList<>();
        if(desiredResourceTags != null){
            for (Map.Entry<String, String> entry : desiredResourceTags.entrySet()) {
                com.amazonaws.networkmanager.connectpeer.Tag tag = com.amazonaws.networkmanager.connectpeer.Tag.builder()
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

    public static List<Tag> tagsDifference(List<com.amazonaws.networkmanager.connectpeer.Tag> tags1,
                                           List<com.amazonaws.networkmanager.connectpeer.Tag> tags2) {
        final List<Tag> sdkTags1 = Utils.cfnTagsToSdkTags(tags1);
        final List<Tag> sdkTags2 = Utils.cfnTagsToSdkTags(tags2);
        return Sets.difference(Utils.listToSet(sdkTags1), Utils.listToSet(sdkTags2)).immutableCopy().asList();
    }

    public static Set<Tag> listToSet(final List<Tag> tags) {
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static com.amazonaws.networkmanager.connectpeer.ConnectPeerConfiguration sdkConfigurationToCfnConfiguration(
            ConnectPeerConfiguration connectPeerConfiguration) {
        return com.amazonaws.networkmanager.connectpeer.ConnectPeerConfiguration.builder()
                .peerAddress(connectPeerConfiguration.peerAddress())
                .bgpConfigurations(sdkBgpConfigurationsToCfnBgpConfigurations(connectPeerConfiguration.bgpConfigurations()))
                .insideCidrBlocks(connectPeerConfiguration.insideCidrBlocks())
                .protocol(connectPeerConfiguration.protocolAsString())
                .coreNetworkAddress(connectPeerConfiguration.coreNetworkAddress())
                .build();
    }

    public static List<com.amazonaws.networkmanager.connectpeer.ConnectPeerBgpConfiguration> sdkBgpConfigurationsToCfnBgpConfigurations(
            List<ConnectPeerBgpConfiguration> connectPeerBgpConfigurations) {
        return connectPeerBgpConfigurations.stream().map(connectPeerBgpConfiguration ->
             com.amazonaws.networkmanager.connectpeer.ConnectPeerBgpConfiguration.builder()
                    .coreNetworkAddress(connectPeerBgpConfiguration.coreNetworkAddress())
                    .coreNetworkAsn((double) connectPeerBgpConfiguration.coreNetworkAsn())
                    .peerAddress(connectPeerBgpConfiguration.peerAddress())
                    .peerAsn((double) connectPeerBgpConfiguration.peerAsn())
                    .build()
        ).collect(Collectors.toList());
    }

    public static BgpOptions cfnBgpOptionsToSdkBgpOptions(com.amazonaws.networkmanager.connectpeer.BgpOptions bgpOptions) {
        return BgpOptions.builder()
                .peerAsn(bgpOptions.getPeerAsn().longValue())
                .build();
    }

    public static String getConnectPeerArn(String resourceId, String edgeLocation, String ownerAccountId) {
        return "arn:" + getAwsPartition(edgeLocation) + ":" + "networkmanager" + "::" + ownerAccountId + ":" + "connect-peer" + "/" + resourceId;
    }

    public static String getAwsPartition(String region) {
        List<String> awsGov = Arrays.asList("us-gov-west-1", "us-gov-east-1");
        List<String> awsCn = Arrays.asList("cn-north-1", "cn-northwest-1");
        List<String> awsIso = Arrays.asList("us-iso-east-1", "us-iso-west-1");
        List<String> awsIsoB = Collections.singletonList("us-isob-east-1");
        if (awsGov.contains(region)) {
            return "aws-us-gov";
        }
        if (awsCn.contains(region)) {
            return "aws-cn";
        }
        if (awsIso.contains(region)) {
            return "aws-iso";
        }
        if (awsIsoB.contains(region)) {
            return "aws-iso-b";
        }
        return "aws";
    }
}
