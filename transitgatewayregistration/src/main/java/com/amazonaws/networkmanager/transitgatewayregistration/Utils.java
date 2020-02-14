package com.amazonaws.networkmanager.transitgatewayregistration;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsResponse;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistration;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

final class Utils {
    final static int CALlBACK_PERIOD_30_SECONDS = 30;

    /**
     * Converter method to convert NetworkManager SDK TransitGatewayRegistration to CFN ResourceModel for READ request
     */
    static ResourceModel transformTransitGatewayRegistration(final TransitGatewayRegistration transitGatewayRegistration) {
        return ResourceModel.builder()
                .globalNetworkId(transitGatewayRegistration.globalNetworkId())
                .transitGatewayArn(transitGatewayRegistration.transitGatewayArn())
                .build();
    }

    /**
     * Shared method to call NetworkManager API getTransitGatewayRegistrations
     */
    static GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrations(final NetworkManagerClient client,
                                                                                 final ResourceModel model,
                                                                                 final AmazonWebServicesClientProxy proxy,
                                                                                 final Logger logger) {
        final GetTransitGatewayRegistrationsRequest getTransitGatewayRegistrationsRequest =
                GetTransitGatewayRegistrationsRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .transitGatewayArns(model.getTransitGatewayArn())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(getTransitGatewayRegistrationsRequest, client::getTransitGatewayRegistrations);
    }

}
