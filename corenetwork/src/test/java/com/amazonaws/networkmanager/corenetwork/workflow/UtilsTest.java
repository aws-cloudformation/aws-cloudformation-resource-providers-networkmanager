package com.amazonaws.networkmanager.corenetwork.workflow;

import com.amazonaws.networkmanager.corenetwork.AbstractTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.CoreNetworkEdge;
import software.amazon.awssdk.services.networkmanager.model.CoreNetworkSegment;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UtilsTest extends AbstractTestBase {

    @Test
    public void instance() {
        assertThat(new Utils().toString().contains("Utils")).isTrue();
    }

    @Test
    public void sdkTagsToCfnTags() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.getSdkTag());

        final List<com.amazonaws.networkmanager.corenetwork.Tag> cfnTags = Utils.sdkTagsToCfnTags(sdkTags);

        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTags.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTags.get(0).value());
    }

    @Test
    public void sdkTagsToCfnTagsWhenNull() {
        final List<com.amazonaws.networkmanager.corenetwork.Tag> cfnTags = Utils.sdkTagsToCfnTags(null);
        assertThat(cfnTags.size()).isEqualTo(0);
    }

    @Test
    public void cfnTagsToSdkTags() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.getSdkTag());
        final List<com.amazonaws.networkmanager.corenetwork.Tag> cfnTags = Utils.sdkTagsToCfnTags(sdkTags);

        List<Tag> sdkTagsConvertedBack = Utils.cfnTagsToSdkTags(cfnTags);

        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTags.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTags.get(0).value());
        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTagsConvertedBack.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTagsConvertedBack.get(0).value());
    }

    @Test
    public void cfnTagsToSdkTagsWhenNull() {
        List<Tag> sdkTagsConvertedBack = Utils.cfnTagsToSdkTags(null);
        assertThat(sdkTagsConvertedBack.size()).isEqualTo(0);
    }

    @Test
    public void tagDifference1() {
        com.amazonaws.networkmanager.corenetwork.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.corenetwork.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);
        tags1.add(MOCKS.getCfnTag());
        final List<com.amazonaws.networkmanager.corenetwork.Tag> tags2 = new ArrayList<>();
        tags2.add(sharedTag);
        tags2.add(MOCKS.getCfnTag());

        List<Tag> difference = Utils.tagsDifference(tags1, tags2);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags1.get(1).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags1.get(1).getValue());
    }

    @Test
    public void tagDifference2() {
        com.amazonaws.networkmanager.corenetwork.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.corenetwork.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);
        tags1.add(MOCKS.getCfnTag());
        final List<com.amazonaws.networkmanager.corenetwork.Tag> tags2 = new ArrayList<>();
        tags2.add(sharedTag);
        tags2.add(MOCKS.getCfnTag());

        List<Tag> difference = Utils.tagsDifference(tags2, tags1);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags2.get(1).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags2.get(1).getValue());
    }

    @Test
    public void tagDifference3() {
        com.amazonaws.networkmanager.corenetwork.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.corenetwork.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);

        final List<com.amazonaws.networkmanager.corenetwork.Tag> tags2 = new ArrayList<>();

        tags2.add(MOCKS.cfnTag(sharedTag.getKey(), "Something Else"));
        List<Tag> difference = Utils.tagsDifference(tags2, tags1);
        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags2.get(0).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags2.get(0).getValue());
        assertThat(difference.get(0).value()).isNotEqualTo(tags1.get(0).getValue());
    }

    @Test
    public void sdkEdgeToCfnEdge() {
        List<CoreNetworkEdge> sdkEdges = MOCKS.getMockSdkEdges();

        List<com.amazonaws.networkmanager.corenetwork.CoreNetworkEdge> cfnEdges = Utils.sdkEdgeToCfnEdge(sdkEdges);

        assertThat(cfnEdges.size()).isEqualTo(1);
        assertThat(cfnEdges.get(0).getAsn().longValue()).isEqualTo(sdkEdges.get(0).asn());
        assertThat(cfnEdges.get(0).getEdgeLocation()).isEqualTo(sdkEdges.get(0).edgeLocation());
        assertThat(cfnEdges.get(0).getInsideCidrBlocks()).isEqualTo(sdkEdges.get(0).insideCidrBlocks());
    }

    @Test
    public void sdkSegmentToCfnSegment() {
        List<CoreNetworkSegment> sdkSegments = MOCKS.getMockSdkSegments();

        List<com.amazonaws.networkmanager.corenetwork.CoreNetworkSegment> cfnSegments = Utils
                .sdkSegmentToCfnSegment(sdkSegments);

        assertThat(cfnSegments.size()).isEqualTo(1);
        assertThat(cfnSegments.get(0).getName()).isEqualTo(sdkSegments.get(0).name());
        assertThat(cfnSegments.get(0).getEdgeLocations()).isEqualTo(sdkSegments.get(0).edgeLocations());
        assertThat(cfnSegments.get(0).getSharedSegments()).isEqualTo(sdkSegments.get(0).sharedSegments());
    }

    @Test
    public void condensedStringTest() {
        String s = "abc def \n ghk";
        String condensedString = Utils.getCondensedString(s);

        assertThat(condensedString).isEqualTo("abcdefghk");
    }
}
