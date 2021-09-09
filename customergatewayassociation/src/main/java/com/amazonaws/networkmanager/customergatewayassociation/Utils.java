package com.amazonaws.networkmanager.customergatewayassociation;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CustomerGatewayAssociation;
import software.amazon.awssdk.services.networkmanager.model.GetCustomerGatewayAssociationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetCustomerGatewayAssociationsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;

final class Utils {

    /**
     * Converter method to convert NetworkManager SDK CustomerGatewayAssociation to CFN ResourceModel for READ request
     */
    static ResourceModel transformCustomerGatewayAssociation(final CustomerGatewayAssociation customerGatewayAssociation) {
        return ResourceModel.builder()
                .customerGatewayArn(customerGatewayAssociation.customerGatewayArn())
                .globalNetworkId(customerGatewayAssociation.globalNetworkId())
                .deviceId(customerGatewayAssociation.deviceId())
                .linkId(customerGatewayAssociation.linkId())
                .build();
    }

    static GetCustomerGatewayAssociationsResponse getCustomerGatewayAssociations(final NetworkManagerClient client,
                                                                                 final ResourceModel model,
                                                                                 final AmazonWebServicesClientProxy proxy) {
        final GetCustomerGatewayAssociationsRequest getCustomerGatewayAssociationsRequest = GetCustomerGatewayAssociationsRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .customerGatewayArns(model.getCustomerGatewayArn())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getCustomerGatewayAssociationsRequest, client::getCustomerGatewayAssociations);
    }

}
