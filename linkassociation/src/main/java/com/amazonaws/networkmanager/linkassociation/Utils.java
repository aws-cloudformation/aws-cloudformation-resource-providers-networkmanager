package com.amazonaws.networkmanager.linkassociation;

import software.amazon.awssdk.services.networkmanager.model.LinkAssociation;

final class Utils {

    /**
     * Converter method to convert NetworkManager SDK LinkAssociation to CFN ResourceModel for READ request
     */
    static ResourceModel transformLinkAssociation(final LinkAssociation linkAssociation) {
        return ResourceModel.builder()
                .linkId(linkAssociation.linkId())
                .globalNetworkId(linkAssociation.globalNetworkId())
                .deviceId(linkAssociation.deviceId())
                .build();
    }

}
