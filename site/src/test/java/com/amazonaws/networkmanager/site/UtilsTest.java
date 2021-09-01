package com.amazonaws.networkmanager.site;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UtilsTest extends TestBase {
    @Test
    void testCfnToSdkTagTransform() {
        final List<com.amazonaws.networkmanager.site.Tag> tags = createCloudformationTags();
        final List<Tag> networkManagerTags = Utils.cfnTagsToSdkTags(tags);

        assertEquals(networkManagerTags, createNetworkManagerTags());
    }


    @Test
    void testMergeTags() {
        final List<com.amazonaws.networkmanager.site.Tag> tags = createCloudformationTags();
        final List<com.amazonaws.networkmanager.site.Tag> mergedTags = Utils.mergeTags(tags, createDesiredResourceTags());
        assertEquals(mergedTags, createMergedTags());
    }

    @Test
    void testSdkToCfnTagTransform() {
        final List<Tag> tags = createNetworkManagerTags();
        final List<com.amazonaws.networkmanager.site.Tag> cloudformationTags = Utils.sdkTagsToCfnTags(tags);

        assertEquals(cloudformationTags, createCloudformationTags());
    }

    @Test
    void testCfnToSdkLocationTransform() {
        final Location location = Location.builder().build();
        final software.amazon.awssdk.services.networkmanager.model.Location networkManagerLocation =
                Utils.transformLocation(location);

        assertEquals(networkManagerLocation, software.amazon.awssdk.services.networkmanager.model.Location.builder().build());
    }

    @Test
    void testSdkToCfnLocationTransform() {
        final software.amazon.awssdk.services.networkmanager.model.Location location =
                software.amazon.awssdk.services.networkmanager.model.Location.builder().build();
        final Location cloudformationLocation = Utils.transformLocation(location);

        assertEquals(cloudformationLocation, Location.builder().build());
    }
}
