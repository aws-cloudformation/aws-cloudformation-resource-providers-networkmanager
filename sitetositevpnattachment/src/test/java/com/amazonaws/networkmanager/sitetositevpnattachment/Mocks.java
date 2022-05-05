package com.amazonaws.networkmanager.sitetositevpnattachment;

import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.model.Attachment;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.AttachmentType;
import software.amazon.awssdk.services.networkmanager.model.CreateSiteToSiteVpnAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.DeleteAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.GetSiteToSiteVpnAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.ListAttachmentsResponse;
import software.amazon.awssdk.services.networkmanager.model.SiteToSiteVpnAttachment;
import software.amazon.awssdk.services.networkmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Mocks {
    public String primaryIdentifier;
    public String coreNetworkId;
    public String resourceArn;
    public String attachmentAvailableState;
    public String attachmentPendingAcceptanceState;
    public String attachmentType;
    public String edgeLocation;
    public String segmentName;
    public String ownerAccount;
    public String vpnConnectionArn;
    public Instant createAt;
    public Instant updateAt;
    public Integer attachmentPolicyRuleNumber;
    public Integer counter;
    public Mocks(
    ) {
        this.primaryIdentifier = "nm-attach-02bb79002EXAMPLE";
        this.resourceArn = "arn:aws:ec2:us-west-1:123456789012:vpn/vpn-02bb79002EXAMPLE";
        this.coreNetworkId = "core-network-01231231212345566";
        this.attachmentAvailableState = AttachmentState.AVAILABLE.toString();
        this.attachmentPendingAcceptanceState = AttachmentState.PENDING_ATTACHMENT_ACCEPTANCE.toString();
        this.vpnConnectionArn = "arn:aws:ec2:region:123123123123:vpn-connection/vpn-11223344556677889";
        this.attachmentType = AttachmentType.SITE_TO_SITE_VPN.toString();
        this.createAt = Instant.now();
        this.updateAt = Instant.now().plusMillis(100);
        this.counter = 0;
        this.edgeLocation = "Location1";
        this.attachmentPolicyRuleNumber = 2;
        this.segmentName = "segment";
        this.ownerAccount = "123456789012";
    }

    public ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(null)
                .vpnConnectionArn(null)
                .attachmentId(this.primaryIdentifier)
                .resourceArn(this.resourceArn)
                .segmentName(this.segmentName)
                .edgeLocation(this.edgeLocation)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .proposedSegmentChange(this.getMockCfnProposedSegmentChange(tags))
                .createdAt(this.createAt.toString())
                .updatedAt(this.updateAt.toString())
                .ownerAccountId(this.ownerAccount)
                .state(state)
                .tags(tags)
                .build();
    }


    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags) {
        return modelWithoutCreateOnlyProperties(tags, this.attachmentAvailableState);
    }

    public ResourceModel modelWithoutCreateOnlyProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutCreateOnlyProperties(tags, this.attachmentAvailableState);
    }


    public  ResourceModel modelWithNullProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .vpnConnectionArn(this.vpnConnectionArn)
                .attachmentId(this.primaryIdentifier)
                .resourceArn(this.resourceArn)
                .segmentName(null)
                .edgeLocation(null)
                .attachmentType(null)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
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
                .vpnConnectionArn(this.vpnConnectionArn)
                .attachmentId(null)
                .resourceArn(this.resourceArn)
                .segmentName(this.segmentName)
                .edgeLocation(this.edgeLocation)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
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
                .vpnConnectionArn(this.vpnConnectionArn)
                .resourceArn(this.resourceArn)
                .attachmentId(this.primaryIdentifier)
                .segmentName(this.segmentName)
                .edgeLocation(this.edgeLocation)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
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

    public SiteToSiteVpnAttachment sdkModelForSiteToSiteVpnAttachment(List<Tag> tags, String state) {
        return SiteToSiteVpnAttachment.builder()
                .attachment(Attachment.builder()
                        .coreNetworkId(this.coreNetworkId)
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
                .vpnConnectionArn(this.vpnConnectionArn)
                .build();
    }

    public Attachment sdkModelForAttachment(List<Tag> tags, String state) {
        return Attachment.builder()
                .coreNetworkId(this.coreNetworkId)
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

    public SiteToSiteVpnAttachment sdkModelForSiteToSiteVpnAttachment() {
        return this.sdkModelForSiteToSiteVpnAttachment(new ArrayList<>(), this.attachmentAvailableState);
    }

    public Attachment sdkModelForAttachment() {
        return this.sdkModelForAttachment(new ArrayList<>(), this.attachmentAvailableState);
    }

    public SiteToSiteVpnAttachment sdkModelForSiteToSiteVpnAttachment(List<Tag> tags) {
        return this.sdkModelForSiteToSiteVpnAttachment(tags, this.attachmentAvailableState);
    }

    public SiteToSiteVpnAttachment sdkModelForSiteToSiteVpnAttachment(String state) {
        return this.sdkModelForSiteToSiteVpnAttachment(new ArrayList<>(), state);
    }

    public GetSiteToSiteVpnAttachmentResponse describeResponse(List<Tag> tags, String state) {
        return GetSiteToSiteVpnAttachmentResponse.builder()
                .siteToSiteVpnAttachment(this.sdkModelForSiteToSiteVpnAttachment(tags, state))
                .build();
    }

    public GetSiteToSiteVpnAttachmentResponse describeResponse(String state) {
        return GetSiteToSiteVpnAttachmentResponse.builder()
                .siteToSiteVpnAttachment(this.sdkModelForSiteToSiteVpnAttachment(new ArrayList<>(), state))
                .build();
    }

    public GetSiteToSiteVpnAttachmentResponse describeResponse() {
        return GetSiteToSiteVpnAttachmentResponse.builder()
                .siteToSiteVpnAttachment(this.sdkModelForSiteToSiteVpnAttachment())
                .build();
    }

    public ListAttachmentsResponse emptyListResponse() {
        return ListAttachmentsResponse.builder()
                .attachments(new ArrayList<>())
                .build();
    }

    public GetSiteToSiteVpnAttachmentResponse describeResponse(List<Tag> tags) {
        return GetSiteToSiteVpnAttachmentResponse.builder()
                .siteToSiteVpnAttachment(this.sdkModelForSiteToSiteVpnAttachment(tags))
                .build();
    }

    public ListAttachmentsResponse listResponse() {
        return ListAttachmentsResponse.builder().attachments(this.sdkModelForAttachment(new ArrayList<>(),
                this.attachmentAvailableState)).build();
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

    public CreateSiteToSiteVpnAttachmentResponse createResponse() {
        return CreateSiteToSiteVpnAttachmentResponse.builder()
                .siteToSiteVpnAttachment(this.sdkModelForSiteToSiteVpnAttachment())
                .build();
    }

    public CreateSiteToSiteVpnAttachmentResponse createResponse(String state) {
        return CreateSiteToSiteVpnAttachmentResponse.builder()
                .siteToSiteVpnAttachment(this.sdkModelForSiteToSiteVpnAttachment(state))
                .build();
    }

    public ProposedSegmentChange getMockCfnProposedSegmentChange(List<Tag> tags) {
        return ProposedSegmentChange.builder()
                .segmentName(this.segmentName)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .tags(tags)
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
