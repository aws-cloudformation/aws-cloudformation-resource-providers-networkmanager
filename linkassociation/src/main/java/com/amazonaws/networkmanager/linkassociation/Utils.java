package com.amazonaws.networkmanager.linkassociation;

import software.amazon.awssdk.services.networkmanager.model.LinkAssociation;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;

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

    static GetLinkAssociationsResponse getLinkAssociations(final NetworkManagerClient client,
                                                            final ResourceModel model,
                                                            final AmazonWebServicesClientProxy proxy) {
        final GetLinkAssociationsRequest getLinkAssociationsRequest = GetLinkAssociationsRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .linkId(model.getLinkId())
                .deviceId(model.getDeviceId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getLinkAssociationsRequest, client::getLinkAssociations);
    }

}
