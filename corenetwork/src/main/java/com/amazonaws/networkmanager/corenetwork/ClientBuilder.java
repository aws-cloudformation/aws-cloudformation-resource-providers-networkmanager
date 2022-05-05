package com.amazonaws.networkmanager.corenetwork;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.LambdaWrapper;

import java.net.URI;

final class ClientBuilder {
    static NetworkManagerClient getClient() {
        System.out.println("Initialize the networkmanager client");
        String regionString = System.getenv("AWS_REGION");
        Region region = Region.US_WEST_2;

        if (regionString != null && (regionString.equals("us-gov-west-1") || regionString.equals("us-gov-east-1"))) {
            region = Region.US_GOV_WEST_1;
        }

        return NetworkManagerClient.builder()
                  .endpointOverride(URI.create("https://networkmanager.us-west-2.amazonaws.com"))
                  .httpClient(LambdaWrapper.HTTP_CLIENT)
                  .region(region)
                  .build();
    }
}
