package com.amazonaws.networkmanager.link;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UtilsTest extends TestBase {
    @Test
    void testCfnToSdkTagTransform() {
        final List<com.amazonaws.networkmanager.link.Tag> tags = createCloudformationTags();
        final List<Tag> networkManagerTags = Utils.cfnTagsToSdkTags(tags);
        assertEquals(networkManagerTags, createNetworkManagerTags());
    }

    @Test
    void testMergeTags() {
        final List<com.amazonaws.networkmanager.link.Tag> tags = createCloudformationTags();
        final List<com.amazonaws.networkmanager.link.Tag> mergedTags = Utils.mergeTags(tags, createDesiredResourceTags());
        assertEquals(mergedTags, createMergedTags());
    }

    @Test
    void testSdkToCfnTagTransform() {
        final List<Tag> tags = createNetworkManagerTags();
        final List<com.amazonaws.networkmanager.link.Tag> cloudformationTags = Utils.sdkTagsToCfnTags(tags);
        assertEquals(cloudformationTags, createCloudformationTags());
    }

    @Test
    void testCfnToSdkBandwidthTransform() {
        final Bandwidth bandwidth = Bandwidth.builder().build();
        final software.amazon.awssdk.services.networkmanager.model.Bandwidth networkManagerBandwidth =
                Utils.transformBandwidth(bandwidth);
        assertEquals(networkManagerBandwidth, software.amazon.awssdk.services.networkmanager.model.Bandwidth.builder().build());
    }

    @Test
    void testSdkToCfnBandwidthTransform() {
        final software.amazon.awssdk.services.networkmanager.model.Bandwidth bandwidth =
                software.amazon.awssdk.services.networkmanager.model.Bandwidth.builder().build();
        Bandwidth cloudformationBandwidth = Utils.transformBandwidth(bandwidth);
        assertEquals(cloudformationBandwidth, Bandwidth.builder().build()
        );
    }
}
