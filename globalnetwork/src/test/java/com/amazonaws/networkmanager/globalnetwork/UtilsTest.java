package com.amazonaws.networkmanager.globalnetwork;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class UtilsTest extends TestBase {
    @Test
    void testTagTransform() {
        final List<com.amazonaws.networkmanager.globalnetwork.Tag> tags = createTags();
        final Collection<Tag> networkManagerTags = Utils.cfnTagsToSdkTags(tags);
        assertEquals(networkManagerTags, createNetworkManagerTags());
    }
}
