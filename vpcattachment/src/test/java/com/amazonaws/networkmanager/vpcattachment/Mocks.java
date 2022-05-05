package com.amazonaws.networkmanager.vpcattachment;

import com.amazonaws.networkmanager.vpcattachment.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mocks {
    public boolean ipv6Supprot;
    public String primaryIdentifier;
    public String coreNetworkId;
    public String coreNetworkArn;
    public String resourceArn;
    public String attachmentAvailableState;
    public String attachmentPendingAcceptanceState;
    public String attachmentType;
    public String edgeLocation;
    public String segmentName;
    public String ownerAccount;
    public String vpcArn;
    public Instant createAt;
    public Instant updateAt;
    public Integer attachmentPolicyRuleNumber;
    public Integer counter;
    public List<String> subnetArns;
    public Mocks(
    ) {
        this.primaryIdentifier = "nm-attach-02bb79002EXAMPLE";
        this.resourceArn = "arn:aws:ec2:region:123123123123:vpc/vpc-11223344556677889";
        this.coreNetworkArn = "arn:aws:networkmanager::123456789012:core-network/core-network-01231231212345566";
        this.coreNetworkId = "core-network-01231231212345566";
        this.attachmentAvailableState = AttachmentState.AVAILABLE.toString();
        this.attachmentPendingAcceptanceState = AttachmentState.PENDING_ATTACHMENT_ACCEPTANCE.toString();
        this.vpcArn = "arn:aws:ec2:region:123123123123:vpc/vpc-11223344556677889";
        this.attachmentType = AttachmentType.VPC.toString();
        this.createAt = Instant.now();
        this.updateAt = Instant.now().plusMillis(100);
        this.counter = 0;
        this.edgeLocation = "Location1";
        this.subnetArns = new ArrayList<>(Collections.singleton("arn:aws:ec2:region:account:subnet/subnet-11223344556677889"));
        this.attachmentPolicyRuleNumber = 2;
        this.segmentName = "segment";
        this.ownerAccount = "123456789012";
        this.ipv6Supprot = true;
    }

    public ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String state, List<String> subnetArns) {
        return ResourceModel.builder()
                .coreNetworkId(null)
                .attachmentId(this.primaryIdentifier)
                .vpcArn(null)
                .subnetArns(subnetArns)
                .segmentName(this.segmentName)
                .resourceArn(this.resourceArn)
                .edgeLocation(this.edgeLocation)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .options(this.getMockCfnOptions(this.ipv6Supprot))
                .proposedSegmentChange(this.getMockCfnProposedSegmentChange(tags))
                .createdAt(this.createAt.toString())
                .updatedAt(this.updateAt.toString())
                .ownerAccountId(this.ownerAccount)
                .state(state)
                .tags(tags)
                .build();
    }


    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, List<String> subnetArns) {
        return modelWithoutCreateOnlyProperties(tags, this.attachmentAvailableState, subnetArns);
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String state) {
        return modelWithoutCreateOnlyProperties(tags, state, this.subnetArns);
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags) {
        return modelWithoutCreateOnlyProperties(tags, this.attachmentAvailableState, this.subnetArns);
    }

    public ResourceModel modelWithoutCreateOnlyProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutCreateOnlyProperties(tags, this.attachmentAvailableState, this.subnetArns);
    }


    public  ResourceModel modelWithNullProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .attachmentId(this.primaryIdentifier)
                .vpcArn(this.vpcArn)
                .coreNetworkArn(this.coreNetworkArn)
                .subnetArns(this.subnetArns)
                .segmentName(null)
                .edgeLocation(null)
                .attachmentType(null)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .options(this.getMockCfnOptions(this.ipv6Supprot))
                .proposedSegmentChange(this.getMockCfnProposedSegmentChange(tags))
                .createdAt(this.createAt.toString())
                .updatedAt(this.updateAt.toString())
                .ownerAccountId(this.ownerAccount)
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithNullProperties(List<Tag> tags) {
        return this.modelWithNullProperties(tags, this.attachmentAvailableState);
    }

    public  ResourceModel modelWithoutPrimaryIdentifier(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .attachmentId(null)
                .vpcArn(this.vpcArn)
                .subnetArns(this.subnetArns)
                .segmentName(this.segmentName)
                .edgeLocation(this.edgeLocation)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .options(this.getMockCfnOptions(this.ipv6Supprot))
                .proposedSegmentChange(this.getMockCfnProposedSegmentChange(tags))
                .createdAt(this.createAt.toString())
                .updatedAt(this.updateAt.toString())
                .ownerAccountId(this.ownerAccount)
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithoutPrimaryIdentifier(List<Tag> tags) {
        return this.modelWithoutPrimaryIdentifier(tags, this.attachmentAvailableState);
    }

    public  ResourceModel model(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .attachmentId(this.primaryIdentifier)
                .vpcArn(this.vpcArn)
                .subnetArns(this.subnetArns)
                .segmentName(this.segmentName)
                .edgeLocation(this.edgeLocation)
                .resourceArn(this.resourceArn)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .options(this.getMockCfnOptions(this.ipv6Supprot))
                .proposedSegmentChange(this.getMockCfnProposedSegmentChange(tags))
                .createdAt(this.createAt.toString())
                .updatedAt(this.updateAt.toString())
                .ownerAccountId(this.ownerAccount)
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel model() {
        final List<Tag> tags = new ArrayList<>();
        return this.model(tags, this.attachmentAvailableState);
    }

    public ResourceModel model(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.model(tags, state);
    }

    public ResourceModel model(List<Tag> tags) {
        return this.model(tags, this.attachmentAvailableState);
    }

    public Tag cfnTag(String key, String value) {
        return Tag.builder().key(key).value(value).build();
    }

    public Tag getCfnTag() {
        this.counter++;
        return this.cfnTag("KEY_" + this.counter, "VALUE_" + this.counter);
    }

    public software.amazon.awssdk.services.networkmanager.model.Tag getSdkTag() {
        this.counter++;
        return this.sdkTag("KEY_" + this.counter, "VALUE_" + this.counter);
    }

    public software.amazon.awssdk.services.networkmanager.model.Tag sdkTag(String key, String value) {
        return software.amazon.awssdk.services.networkmanager.model.Tag.builder().key(key).value(value).build();
    }

    public VpcAttachment sdkModelForVpcAttachment(List<Tag> tags, String state, List<String> subnetArns) {
        return VpcAttachment.builder()
                .attachment(Attachment.builder()
                        .coreNetworkId(this.coreNetworkId)
                        .coreNetworkArn(this.coreNetworkArn)
                        .attachmentId(this.primaryIdentifier)
                        .resourceArn(this.resourceArn)
                        .segmentName(this.segmentName)
                        .edgeLocation(this.edgeLocation)
                        .attachmentType(this.attachmentType)
                        .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                        .proposedSegmentChange(this.getMockSdkProposedSegmentChange(Utils.cfnTagsToSdkTags(tags)))
                        .createdAt(this.createAt)
                        .updatedAt(this.updateAt)
                        .ownerAccountId(this.ownerAccount)
                        .state(state)
                        .tags(Utils.cfnTagsToSdkTags(tags))
                        .build())
                .subnetArns(subnetArns)
                .options(this.getMockSdkOptions(ipv6Supprot))
                .build();
    }

    public Attachment sdkModelForAttachment(List<Tag> tags, String state) {
        return Attachment.builder()
                .coreNetworkId(this.coreNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .attachmentId(this.primaryIdentifier)
                .resourceArn(this.resourceArn)
                .segmentName(this.segmentName)
                .edgeLocation(this.edgeLocation)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .proposedSegmentChange(this.getMockSdkProposedSegmentChange(Utils.cfnTagsToSdkTags(tags)))
                .createdAt(this.createAt)
                .updatedAt(this.updateAt)
                .ownerAccountId(this.ownerAccount)
                .state(state)
                .tags(Utils.cfnTagsToSdkTags(tags))
                .build();
    }

    public VpcAttachment sdkModelForVpcAttachment() {
        return this.sdkModelForVpcAttachment(new ArrayList<>(), this.attachmentAvailableState, this.subnetArns);
    }

    public Attachment sdkModelForAttachment() {
        return this.sdkModelForAttachment(new ArrayList<>(), this.attachmentAvailableState);
    }

    public VpcAttachment sdkModelForVpcAttachment(List<Tag> tags) {
        return this.sdkModelForVpcAttachment(tags, this.attachmentAvailableState, this.subnetArns);
    }

    public VpcAttachment sdkModelForVpcAttachment(String state) {
        return this.sdkModelForVpcAttachment(new ArrayList<>(), state, this.subnetArns);
    }

    public GetVpcAttachmentResponse describeResponse(List<Tag> tags, String state) {
        return GetVpcAttachmentResponse.builder()
                .vpcAttachment(this.sdkModelForVpcAttachment(tags, state, this.subnetArns))
                .build();
    }

    public GetVpcAttachmentResponse describeResponse(List<Tag> tags, String state, List<String> subnetArns) {
        return GetVpcAttachmentResponse.builder()
                .vpcAttachment(this.sdkModelForVpcAttachment(tags, state, subnetArns))
                .build();
    }

    public GetVpcAttachmentResponse describeResponse(String state) {
        return GetVpcAttachmentResponse.builder()
                .vpcAttachment(this.sdkModelForVpcAttachment(new ArrayList<>(), state, this.subnetArns))
                .build();
    }

    public GetVpcAttachmentResponse describeResponse() {
        return GetVpcAttachmentResponse.builder()
                .vpcAttachment(this.sdkModelForVpcAttachment())
                .build();
    }

    public ListAttachmentsResponse emptyListResponse() {
        return ListAttachmentsResponse.builder()
                .attachments(new ArrayList<>())
                .build();
    }

    public GetVpcAttachmentResponse describeResponse(List<Tag> tags) {
        return GetVpcAttachmentResponse.builder()
                .vpcAttachment(this.sdkModelForVpcAttachment(tags))
                .build();
    }

    public ListAttachmentsResponse listResponse() {
        return ListAttachmentsResponse.builder().attachments(this.sdkModelForAttachment(new ArrayList<>(), this.attachmentAvailableState)).build();
    }

    public UpdateVpcAttachmentResponse updateResponse(boolean ipv6Support, List<String> subnetArns, List<Tag> tags) {
        VpcAttachment vpcAttachment = VpcAttachment.builder()
                .attachment(Attachment.builder()
                        .coreNetworkId(this.coreNetworkId)
                        .coreNetworkArn(this.coreNetworkArn)
                        .attachmentId(this.primaryIdentifier)
                        .resourceArn(this.resourceArn)
                        .segmentName(this.segmentName)
                        .edgeLocation(this.edgeLocation)
                        .attachmentType(this.attachmentType)
                        .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                        .proposedSegmentChange(this.getMockSdkProposedSegmentChange(Utils.cfnTagsToSdkTags(new ArrayList<>())))
                        .createdAt(this.createAt)
                        .updatedAt(this.updateAt)
                        .ownerAccountId(this.ownerAccount)
                        .state(this.attachmentAvailableState)
                        .tags(Utils.cfnTagsToSdkTags(tags))
                        .build())
                .subnetArns(subnetArns)
                .options(this.getMockSdkOptions(ipv6Support))
                .build();
        return UpdateVpcAttachmentResponse.builder().vpcAttachment(vpcAttachment).build();
    }

    public UpdateVpcAttachmentResponse updateResponse(List<String> subnetArns) {
        return updateResponse(this.ipv6Supprot, subnetArns, new ArrayList<>());
    }

    public UpdateVpcAttachmentResponse updateResponse(boolean ipv6Support) {
        return updateResponse(ipv6Support, this.subnetArns, new ArrayList<>());
    }

    public TagResourceResponse tagResourceResponse() {
        return TagResourceResponse.builder().build();
    }

    public UntagResourceResponse untagResourceResponse() {
        return UntagResourceResponse.builder().build();
    }

    public DeleteAttachmentResponse deleteResponse() {
        return DeleteAttachmentResponse.builder()
                .attachment(this.sdkModelForAttachment())
                .build();
    }

    public CreateVpcAttachmentResponse createResponse() {
        return CreateVpcAttachmentResponse.builder()
                .vpcAttachment(this.sdkModelForVpcAttachment())
                .build();
    }

    public CreateVpcAttachmentResponse createResponse(String state) {
        return CreateVpcAttachmentResponse.builder()
                .vpcAttachment(this.sdkModelForVpcAttachment(state))
                .build();
    }

    public VpcOptions getMockCfnOptions(boolean ipv6Support) {
        return VpcOptions.builder()
                .ipv6Support(ipv6Support)
                .build();
    }

    public ProposedSegmentChange getMockCfnProposedSegmentChange(List<Tag> tags) {
        return ProposedSegmentChange.builder()
                .segmentName(this.segmentName)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .tags(tags)
                .build();
    }

    public software.amazon.awssdk.services.networkmanager.model.VpcOptions getMockSdkOptions(boolean ipv6Support) {
        return software.amazon.awssdk.services.networkmanager.model.VpcOptions.builder()
                .ipv6Support(ipv6Support)
                .build();
    }

    public software.amazon.awssdk.services.networkmanager.model.ProposedSegmentChange getMockSdkProposedSegmentChange(
            List<software.amazon.awssdk.services.networkmanager.model.Tag> tags) {
        return software.amazon.awssdk.services.networkmanager.model.ProposedSegmentChange.builder()
                .segmentName(this.segmentName)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .tags(tags)
                .build();
    }
}
