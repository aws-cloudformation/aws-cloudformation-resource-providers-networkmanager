package com.amazonaws.networkmanager.globalnetwork;


import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.LambdaWrapper;

final class ClientBuilder {
    static NetworkManagerClient getClient() {
        return NetworkManagerClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .build();
    }
}
