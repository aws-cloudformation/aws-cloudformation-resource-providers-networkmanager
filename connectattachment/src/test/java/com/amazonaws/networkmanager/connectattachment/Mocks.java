package com.amazonaws.networkmanager.connectattachment;

import com.amazonaws.networkmanager.connectattachment.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
    public String protocol;
    public String transportAttachmentId;
    public Instant createAt;
    public Instant updateAt;
    public Integer attachmentPolicyRuleNumber;
    public Integer counter;
    public List<String> subnetArns;
    public Mocks(
    ) {
        this.primaryIdentifier = "nm-attach-02bb79002EXAMPLE";
        this.resourceArn = "arn:aws:networkmanager::123456789012:nm-attachment/nm-attach-02bb79002EXAMPLE";
        this.coreNetworkId = "core-network-01231231212345566";
        this.transportAttachmentId = "trans-attach-02bb79002EXAM123";
        this.attachmentAvailableState = AttachmentState.AVAILABLE.toString();
        this.attachmentPendingAcceptanceState = AttachmentState.PENDING_ATTACHMENT_ACCEPTANCE.toString();
        this.attachmentType = AttachmentType.CONNECT.toString();
        this.createAt = Instant.now();
        this.updateAt = Instant.now().plusMillis(100);
        this.counter = 0;
        this.edgeLocation = "Location1";
        this.subnetArns = new ArrayList<>(Collections.singleton("arn:aws:ec2:region:account:subnet/subnet-11223344556677889"));
        this.attachmentPolicyRuleNumber = 2;
        this.segmentName = "segment";
        this.ownerAccount = "123456789012";
        this.protocol = TunnelProtocol.GRE.toString();
    }

    public ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(null)
                .transportAttachmentId(null)
                .options(null)
                .edgeLocation(null)
                .attachmentId(this.primaryIdentifier)
                .resourceArn(this.resourceArn)
                .segmentName(this.segmentName)
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
                .transportAttachmentId(this.transportAttachmentId)
                .attachmentId(this.primaryIdentifier)
                .resourceArn(this.resourceArn)
                .edgeLocation(this.edgeLocation)
                .options(this.getMockCfnConnectAttachmentOptions())
                .segmentName(null)
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
                .transportAttachmentId(this.transportAttachmentId)
                .options(this.getMockCfnConnectAttachmentOptions())
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
                .transportAttachmentId(this.transportAttachmentId)
                .options(this.getMockCfnConnectAttachmentOptions())
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

    public ConnectAttachment sdkModelForConnectAttachment(List<Tag> tags, String state) {
        return ConnectAttachment.builder()
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
                .transportAttachmentId(this.transportAttachmentId)
                .options(this.getMockSdkConnectAttachmentOptions())
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

    public ConnectAttachment sdkModelForConnectAttachment() {
        return this.sdkModelForConnectAttachment(new ArrayList<>(), this.attachmentAvailableState);
    }

    public Attachment sdkModelForAttachment() {
        return this.sdkModelForAttachment(new ArrayList<>(), this.attachmentAvailableState);
    }

    public ConnectAttachment sdkModelForConnectAttachment(List<Tag> tags) {
        return this.sdkModelForConnectAttachment(tags, this.attachmentAvailableState);
    }

    public ConnectAttachment sdkModelForConnectAttachment(String state) {
        return this.sdkModelForConnectAttachment(new ArrayList<>(), state);
    }

    public GetConnectAttachmentResponse describeResponse(List<Tag> tags, String state) {
        return GetConnectAttachmentResponse.builder()
                .connectAttachment(this.sdkModelForConnectAttachment(tags, state))
                .build();
    }

    public GetConnectAttachmentResponse describeResponse(String state) {
        return GetConnectAttachmentResponse.builder()
                .connectAttachment(this.sdkModelForConnectAttachment(new ArrayList<>(), state))
                .build();
    }

    public GetConnectAttachmentResponse describeResponse() {
        return GetConnectAttachmentResponse.builder()
                .connectAttachment(this.sdkModelForConnectAttachment())
                .build();
    }

    public ListAttachmentsResponse emptyListResponse() {
        return ListAttachmentsResponse.builder()
                .attachments(new ArrayList<>())
                .build();
    }

    public GetConnectAttachmentResponse describeResponse(List<Tag> tags) {
        return GetConnectAttachmentResponse.builder()
                .connectAttachment(this.sdkModelForConnectAttachment(tags))
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

    public CreateConnectAttachmentResponse createResponse() {
        return CreateConnectAttachmentResponse.builder()
                .connectAttachment(this.sdkModelForConnectAttachment())
                .build();
    }

    public CreateConnectAttachmentResponse createResponse(String state) {
        return CreateConnectAttachmentResponse.builder()
                .connectAttachment(this.sdkModelForConnectAttachment(state))
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

    public software.amazon.awssdk.services.networkmanager.model.ConnectAttachmentOptions getMockSdkConnectAttachmentOptions() {
        return software.amazon.awssdk.services.networkmanager.model.ConnectAttachmentOptions.builder()
                .protocol(this.protocol)
                .build();
    }

    public ConnectAttachmentOptions getMockCfnConnectAttachmentOptions() {
        return ConnectAttachmentOptions.builder()
                .protocol(this.protocol)
                .build();
    }
}
