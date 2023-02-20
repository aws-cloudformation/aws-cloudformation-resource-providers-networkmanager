package com.amazonaws.networkmanager.transitgatewayroutetableattachment;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.model.Attachment;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.AttachmentType;
import software.amazon.awssdk.services.networkmanager.model.CreateTransitGatewayRouteTableAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.DeleteAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRouteTableAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.ListAttachmentsResponse;
import software.amazon.awssdk.services.networkmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRouteTableAttachment;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Mocks {
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
    public String routeTableArn;
    public String peeringId;
    public Instant createAt;
    public Instant updateAt;
    public Integer attachmentPolicyRuleNumber;
    public Integer counter;
    public Mocks(
    ) {
        this.primaryIdentifier = "nm-attach-02bb79002EXAMPLE";
        this.resourceArn = "arn:aws:ec2:us-west-1:123456789012:routetable/rtb-02bb79002EXAMPLE";
        this.coreNetworkId = "core-network-01231231212345566";
        this.coreNetworkArn = "arn:aws:networkmanager::123456789012:core-network/core-network-01231231212345566";
        this.attachmentAvailableState = AttachmentState.AVAILABLE.toString();
        this.attachmentPendingAcceptanceState = AttachmentState.PENDING_ATTACHMENT_ACCEPTANCE.toString();
        this.routeTableArn = "arn:aws:ec2:region:123123123123:routetable/rtb-02bb79002EXAMPLE";
        this.attachmentType = AttachmentType.TRANSIT_GATEWAY_ROUTE_TABLE.toString();
        this.createAt = Instant.now();
        this.updateAt = Instant.now().plusMillis(100);
        this.counter = 0;
        this.edgeLocation = "Location1";
        this.attachmentPolicyRuleNumber = 2;
        this.segmentName = "segment";
        this.ownerAccount = "123456789012";
        this.peeringId = "peering-123123123123";
    }

    public ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
    }

    public ResourceHandlerRequest<ResourceModel> updateHandlerRequest(ResourceModel model, Set<Tag> prevTags) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(Utils.cfnTagsToJsonTags(prevTags))
                .previousResourceState(model(prevTags))
                .build();
    }

    public ResourceHandlerRequest<ResourceModel> updateHandlerRequest(ResourceModel model, ResourceModel prevModel) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(Set<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .transitGatewayRouteTableArn(null)
                .peeringId(null)
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


    public  ResourceModel modelWithoutCreateOnlyProperties(Set<Tag> tags) {
        return modelWithoutCreateOnlyProperties(tags, this.attachmentAvailableState);
    }

    public ResourceModel modelWithoutCreateOnlyProperties() {
        final Set<Tag> tags = new HashSet<>();
        return this.modelWithoutCreateOnlyProperties(tags, this.attachmentAvailableState);
    }

    public  ResourceModel modelWithoutPrimaryIdentifier(Set<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .peeringId(this.peeringId)
                .transitGatewayRouteTableArn(this.routeTableArn)
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

    public ResourceModel modelWithoutPrimaryIdentifier(Set<Tag> tags) {
        return this.modelWithoutPrimaryIdentifier(tags, this.attachmentAvailableState);
    }

    public  ResourceModel model(Set<Tag> tags, String state, Set<Tag> proposedTag) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .transitGatewayRouteTableArn(this.routeTableArn)
                .peeringId(this.peeringId)
                .resourceArn(this.resourceArn)
                .attachmentId(this.primaryIdentifier)
                .segmentName(this.segmentName)
                .edgeLocation(this.edgeLocation)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .proposedSegmentChange(this.getMockCfnProposedSegmentChange(proposedTag))
                .createdAt(this.createAt.toString())
                .updatedAt(this.updateAt.toString())
                .ownerAccountId(this.ownerAccount)
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel model() {
        final Set<Tag> tags = new HashSet<>();
        return this.model(tags, this.attachmentAvailableState, new HashSet<>());
    }

    public ResourceModel model(String state) {
        final Set<Tag> tags = new HashSet<>();
        return this.model(tags, state, new HashSet<>());
    }

    public ResourceModel model(Set<Tag> tags) {
        return this.model(tags, this.attachmentAvailableState, new HashSet<>());
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

    public TransitGatewayRouteTableAttachment sdkModelForTgwRouteTableAttachment(Set<Tag> tags, String state, Set<Tag> proposedTags) {
        return TransitGatewayRouteTableAttachment.builder()
                .attachment(Attachment.builder()
                        .coreNetworkId(this.coreNetworkId)
                        .attachmentId(this.primaryIdentifier)
                        .coreNetworkArn(this.coreNetworkArn)
                        .resourceArn(this.resourceArn)
                        .segmentName(this.segmentName)
                        .edgeLocation(this.edgeLocation)
                        .attachmentType(this.attachmentType)
                        .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                        .proposedSegmentChange(this.getMockSdkProposedSegmentChange(
                                new ArrayList<>(Utils.cfnTagsToSdkTags(proposedTags))))
                        .createdAt(this.createAt)
                        .updatedAt(this.updateAt)
                        .ownerAccountId(this.ownerAccount)
                        .state(state)
                        .tags(Utils.cfnTagsToSdkTags(tags))
                        .build())
                .transitGatewayRouteTableArn(this.routeTableArn)
                .peeringId(this.peeringId)
                .build();
    }

    public Attachment sdkModelForAttachment(Set<Tag> tags, String state) {
        return Attachment.builder()
                .coreNetworkId(this.coreNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .attachmentId(this.primaryIdentifier)
                .resourceArn(this.resourceArn)
                .segmentName(this.segmentName)
                .edgeLocation(this.edgeLocation)
                .attachmentType(this.attachmentType)
                .attachmentPolicyRuleNumber(this.attachmentPolicyRuleNumber)
                .proposedSegmentChange(this.getMockSdkProposedSegmentChange(new ArrayList<>(Utils.cfnTagsToSdkTags(tags))))
                .createdAt(this.createAt)
                .updatedAt(this.updateAt)
                .ownerAccountId(this.ownerAccount)
                .state(state)
                .tags(Utils.cfnTagsToSdkTags(tags))
                .build();
    }

    public TransitGatewayRouteTableAttachment sdkModelForTgwRouteTableAttachment() {
        return this.sdkModelForTgwRouteTableAttachment(new HashSet<>(), this.attachmentAvailableState, new HashSet<>());
    }

    public Attachment sdkModelForAttachment() {
        return this.sdkModelForAttachment(new HashSet<>(), this.attachmentAvailableState);
    }

    public TransitGatewayRouteTableAttachment sdkModelForTgwRouteTableAttachment(Set<Tag> tags) {
        return this.sdkModelForTgwRouteTableAttachment(tags, this.attachmentAvailableState, new HashSet<>());
    }

    public TransitGatewayRouteTableAttachment sdkModelForTgwRouteTableAttachment(String state) {
        return this.sdkModelForTgwRouteTableAttachment(new HashSet<>(), state, new HashSet<>());
    }

    public GetTransitGatewayRouteTableAttachmentResponse describeResponse(Set<Tag> tags, String state) {
        return GetTransitGatewayRouteTableAttachmentResponse.builder()
                .transitGatewayRouteTableAttachment(this.sdkModelForTgwRouteTableAttachment(tags, state, new HashSet<>()))
                .build();
    }

    public GetTransitGatewayRouteTableAttachmentResponse describeResponse(Set<Tag> tags, Set<Tag> proposedSegmentTags) {
        return GetTransitGatewayRouteTableAttachmentResponse.builder()
                .transitGatewayRouteTableAttachment(this.sdkModelForTgwRouteTableAttachment(tags,
                        AttachmentState.PENDING_TAG_ACCEPTANCE.toString(), proposedSegmentTags))
                .build();
    }


    public GetTransitGatewayRouteTableAttachmentResponse describeResponse(String state) {
        return GetTransitGatewayRouteTableAttachmentResponse.builder()
                .transitGatewayRouteTableAttachment(this.sdkModelForTgwRouteTableAttachment(new HashSet<>(), state, new HashSet<>()))
                .build();
    }

    public GetTransitGatewayRouteTableAttachmentResponse describeResponse() {
        return GetTransitGatewayRouteTableAttachmentResponse.builder()
                .transitGatewayRouteTableAttachment(this.sdkModelForTgwRouteTableAttachment())
                .build();
    }

    public ListAttachmentsResponse emptyListResponse() {
        return ListAttachmentsResponse.builder()
                .attachments(new ArrayList<>())
                .build();
    }

    public GetTransitGatewayRouteTableAttachmentResponse describeResponse(Set<Tag> tags) {
        return GetTransitGatewayRouteTableAttachmentResponse.builder()
                .transitGatewayRouteTableAttachment(this.sdkModelForTgwRouteTableAttachment(tags))
                .build();
    }

    public ListAttachmentsResponse listResponse() {
        return ListAttachmentsResponse.builder().attachments(this.sdkModelForAttachment(new HashSet<>(),
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

    public CreateTransitGatewayRouteTableAttachmentResponse createResponse() {
        return CreateTransitGatewayRouteTableAttachmentResponse.builder()
                .transitGatewayRouteTableAttachment(this.sdkModelForTgwRouteTableAttachment())
                .build();
    }

    public CreateTransitGatewayRouteTableAttachmentResponse createResponse(String state) {
        return CreateTransitGatewayRouteTableAttachmentResponse.builder()
                .transitGatewayRouteTableAttachment(this.sdkModelForTgwRouteTableAttachment(state))
                .build();
    }

    public ProposedSegmentChange getMockCfnProposedSegmentChange(Set<Tag> tags) {
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
