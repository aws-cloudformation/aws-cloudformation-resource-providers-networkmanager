package com.amazonaws.networkmanager.customergatewayassociation;

import software.amazon.awssdk.services.networkmanager.model.CustomerGatewayAssociation;

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

}
