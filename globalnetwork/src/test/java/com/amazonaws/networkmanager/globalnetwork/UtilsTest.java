package com.amazonaws.networkmanager.globalnetwork;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class UtilsTest extends TestBase {
    @Test
    void testTagTransform() {
        final List<com.amazonaws.networkmanager.globalnetwork.Tag> tags = createTagsWithOneTag();
        final Collection<Tag> networkManagerTags = Utils.cfnTagsToSdkTags(tags);
        assertEquals(networkManagerTags, createNetworkManagerTagsWithOneTag());
    }


    @Test
    void testMergeTags() {
        final List<com.amazonaws.networkmanager.globalnetwork.Tag> tags = createTagsWithOneTag();
        final List<com.amazonaws.networkmanager.globalnetwork.Tag> mergedTags = Utils.mergeTags(tags, createDesiredResourceTags());
        Assertions.assertEquals(mergedTags, createMergedTags());
    }
}
