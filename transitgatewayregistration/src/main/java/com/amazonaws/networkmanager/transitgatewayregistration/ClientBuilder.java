package com.amazonaws.networkmanager.transitgatewayregistration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.LambdaWrapper;

final class ClientBuilder {
    static NetworkManagerClient getClient() {
        return NetworkManagerClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .region(Region.US_WEST_2)
                .build();
    }
}
