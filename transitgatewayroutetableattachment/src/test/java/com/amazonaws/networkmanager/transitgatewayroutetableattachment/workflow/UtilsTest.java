package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.AbstractTestBase;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.ProposedSegmentChange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UtilsTest extends AbstractTestBase {
    @Test
    public void instance() {
        assertThat(new Utils().toString().contains("Utils")).isTrue();
    }

    @Test
    public void sdkTagsToCfnTags() {
        final Set<Tag> sdkTags = new HashSet<>();
        sdkTags.add(MOCKS.getSdkTag());

        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> cfnTags = Utils.sdkTagsToCfnTags(sdkTags);

        assertThat(cfnTags.size() == 1);
        assertThat(cfnTags.iterator().next());
    }

    @Test
    public void sdkTagsToCfnTagsWhenNull() {
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> cfnTags = Utils.sdkTagsToCfnTags(null);
        assertThat(cfnTags.size()).isEqualTo(0);
    }

    @Test
    public void cfnTagsToSdkTags() {
        final Set<Tag> sdkTags = new HashSet<>();
        sdkTags.add(MOCKS.getSdkTag());
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> cfnTags = Utils.sdkTagsToCfnTags(sdkTags);

        Set<Tag> sdkTagsConvertedBack = Utils.cfnTagsToSdkTags(cfnTags);

        assertThat(cfnTags.iterator().next().getKey()).isEqualTo(sdkTags.iterator().next().key());
        assertThat(cfnTags.iterator().next().getValue()).isEqualTo(sdkTags.iterator().next().value());
        assertThat(cfnTags.iterator().next().getKey()).isEqualTo(sdkTagsConvertedBack.iterator().next().key());
        assertThat(cfnTags.iterator().next().getValue()).isEqualTo(sdkTagsConvertedBack.iterator().next().value());
    }

    @Test
    public void cfnTagsToSdkTagsWhenNull() {
        Set<Tag> sdkTagsConvertedBack = Utils.cfnTagsToSdkTags(null);
        assertThat(sdkTagsConvertedBack.size()).isEqualTo(0);
    }

    @Test
    public void difference1() {
        com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag sharedTag = MOCKS.getCfnTag();
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags1 = new HashSet<>();
        final com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag tag1 = MOCKS.getCfnTag();
        tags1.add(sharedTag);
        tags1.add(tag1);
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags2 = new HashSet<>();
        final com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag tag2 = MOCKS.getCfnTag();
        tags2.add(sharedTag);
        tags2.add(tag2);

        Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> difference = Utils.tagsDifference(tags1, tags2);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.iterator().next().getKey()).isEqualTo(tag1.getKey());
        assertThat(difference.iterator().next().getValue()).isEqualTo(tag1.getValue());
    }

    @Test
    public void difference2() {
        com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag sharedTag = MOCKS.getCfnTag();
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags1 = new HashSet<>();
        final com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag tag1 = MOCKS.getCfnTag();
        tags1.add(sharedTag);
        tags1.add(tag1);
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags2 = new HashSet<>();
        final com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag tag2 = MOCKS.getCfnTag();
        tags2.add(sharedTag);
        tags2.add(tag2);

        Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> difference = Utils.tagsDifference(tags2, tags1);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.iterator().next().getKey()).isEqualTo(tag2.getKey());
        assertThat(difference.iterator().next().getValue()).isEqualTo(tag2.getValue());
    }

    @Test
    public void difference3() {
        com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag sharedTag = MOCKS.getCfnTag();
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags1 = new HashSet<>();
        tags1.add(sharedTag);
        final Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tags2 = new HashSet<>();
        final com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag diffTag = MOCKS.cfnTag(sharedTag.getKey(), "Something Else");
        tags2.add(diffTag);

        Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> difference = Utils.tagsDifference(tags2, tags1);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.iterator().next().getKey()).isEqualTo(diffTag.getKey());
        assertThat(difference.iterator().next().getValue()).isEqualTo(diffTag.getValue());
        assertThat(difference.iterator().next().getValue()).isNotEqualTo(sharedTag.getValue());
    }

    @Test
    public void sdkSegmentChangeToCfnSegmentChange() {
        final Set<Tag> sdkTags = new HashSet<>();
        sdkTags.add(MOCKS.getSdkTag());
        software.amazon.awssdk.services.networkmanager.model.ProposedSegmentChange sdkProposedSegmentChange =
                MOCKS.getMockSdkProposedSegmentChange(new ArrayList<>(sdkTags));

        ProposedSegmentChange cfnProposedSegmentChange = Utils.sdkSegmentChangeToCfnSegmentChange(sdkProposedSegmentChange);

        assertThat(cfnProposedSegmentChange.getSegmentName()).isEqualTo(sdkProposedSegmentChange.segmentName());
        assertThat(cfnProposedSegmentChange.getAttachmentPolicyRuleNumber()).isEqualTo(sdkProposedSegmentChange.attachmentPolicyRuleNumber());
        assertThat(cfnProposedSegmentChange.getTags()).isEqualTo(Utils.sdkTagsToCfnTags(new HashSet<>(sdkProposedSegmentChange.tags())));
    }
}
