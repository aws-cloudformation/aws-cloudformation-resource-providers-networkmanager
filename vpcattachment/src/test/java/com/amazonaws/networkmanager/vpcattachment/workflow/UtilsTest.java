package com.amazonaws.networkmanager.vpcattachment.workflow;

import com.amazonaws.networkmanager.vpcattachment.AbstractTestBase;
import com.amazonaws.networkmanager.vpcattachment.ProposedSegmentChange;
import com.amazonaws.networkmanager.vpcattachment.VpcOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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

        final List<com.amazonaws.networkmanager.vpcattachment.Tag> cfnTags = Utils.sdkTagsToCfnTags(sdkTags);

        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTags.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTags.get(0).value());
    }

    @Test
    public void sdkTagsToCfnTagsWhenNull() {
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> cfnTags = Utils.sdkTagsToCfnTags(null);
        assertThat(cfnTags.size()).isEqualTo(0);
    }

    @Test
    public void cfnTagsToSdkTags() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.getSdkTag());
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> cfnTags = Utils.sdkTagsToCfnTags(sdkTags);

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
    public void difference1() {
        com.amazonaws.networkmanager.vpcattachment.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);
        tags1.add(MOCKS.getCfnTag());
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> tags2 = new ArrayList<>();
        tags2.add(sharedTag);
        tags2.add(MOCKS.getCfnTag());

        List<Tag> difference = Utils.tagsDifference(tags1, tags2);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags1.get(1).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags1.get(1).getValue());
    }

    @Test
    public void difference2() {
        com.amazonaws.networkmanager.vpcattachment.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);
        tags1.add(MOCKS.getCfnTag());
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> tags2 = new ArrayList<>();
        tags2.add(sharedTag);
        tags2.add(MOCKS.getCfnTag());

        List<Tag> difference = Utils.tagsDifference(tags2, tags1);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags2.get(1).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags2.get(1).getValue());
    }

    @Test
    public void difference3() {
        com.amazonaws.networkmanager.vpcattachment.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);

        final List<com.amazonaws.networkmanager.vpcattachment.Tag> tags2 = new ArrayList<>();

        tags2.add(MOCKS.cfnTag(sharedTag.getKey(), "Something Else"));
        List<Tag> difference = Utils.tagsDifference(tags2, tags1);
        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags2.get(0).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags2.get(0).getValue());
        assertThat(difference.get(0).value()).isNotEqualTo(tags1.get(0).getValue());
    }

    @Test
    public void cfnOptionsToSdkOptions() {
        VpcOptions cfnVpcOptions = MOCKS.getMockCfnOptions(true);

        software.amazon.awssdk.services.networkmanager.model.VpcOptions sdkVpcOptions = Utils.cfnOptionsToSdkOptions(cfnVpcOptions);

        assertThat(sdkVpcOptions.ipv6Support()).isEqualTo(cfnVpcOptions.getIpv6Support());
    }

    @Test
    public void sdkOptionsToCfnOptions() {
        software.amazon.awssdk.services.networkmanager.model.VpcOptions sdkVpcOptions = MOCKS.getMockSdkOptions(true);

        VpcOptions cfnOptions = Utils.sdkOptionsToCfnOptions(sdkVpcOptions);

        assertThat(cfnOptions.getIpv6Support()).isEqualTo(sdkVpcOptions.ipv6Support());
    }

    @Test
    public void sdkSegmentChangeToCfnSegmentChange() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.getSdkTag());
        software.amazon.awssdk.services.networkmanager.model.ProposedSegmentChange sdkProposedSegmentChange = MOCKS.getMockSdkProposedSegmentChange(sdkTags);

        ProposedSegmentChange cfnProposedSegmentChange = Utils.sdkSegmentChangeToCfnSegmentChange(sdkProposedSegmentChange);

        assertThat(cfnProposedSegmentChange.getSegmentName()).isEqualTo(sdkProposedSegmentChange.segmentName());
        assertThat(cfnProposedSegmentChange.getAttachmentPolicyRuleNumber()).isEqualTo(sdkProposedSegmentChange.attachmentPolicyRuleNumber());
        assertThat(cfnProposedSegmentChange.getTags()).isEqualTo(Utils.sdkTagsToCfnTags(sdkProposedSegmentChange.tags()));
    }
}
