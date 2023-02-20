package com.amazonaws.networkmanager.transitgatewaypeering;

import com.amazonaws.networkmanager.transitgatewaypeering.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.model.CreateTransitGatewayPeeringResponse;
import software.amazon.awssdk.services.networkmanager.model.DeletePeeringResponse;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayPeeringResponse;
import software.amazon.awssdk.services.networkmanager.model.ListPeeringsResponse;
import software.amazon.awssdk.services.networkmanager.model.Peering;
import software.amazon.awssdk.services.networkmanager.model.PeeringState;
import software.amazon.awssdk.services.networkmanager.model.PeeringType;
import software.amazon.awssdk.services.networkmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayPeering;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Mocks {
    public String primaryIdentifier;
    public String coreNetworkId;
    public String resourceArn;
    public String edgeLocation;
    public String ownerAccountId;
    public String transitGatewayArn;
    public String transitGatewayPeeringAttachmentId;
    public String peeringType;
    public String coreNetworkArn;
    public String peeringAvailableState;
    public Instant createAt;
    public Integer counter;
    public Mocks(
    ) {
        this.primaryIdentifier = "peering-02bb79002EXAMPLE";
        this.resourceArn = "arn:aws:ec2:us-west-1:123456789012:transit-gateway/tgw-02bb79002EXAMPLE";
        this.coreNetworkId = "core-network-01231231212345566";
        this.coreNetworkArn = "arn:aws:networkmanager::123456789012:core-network/core-network-01231231212345566";
        this.transitGatewayPeeringAttachmentId = "tgw-attach-02bb79002EXAMPLE";
        this.peeringAvailableState = PeeringState.AVAILABLE.toString();
        this.ownerAccountId = "123123123123";
        this.transitGatewayArn = "arn:aws:ec2:us-west-1:123456789012:transit-gateway/tgw-02bb79002EXAMPLE";
        this.peeringType = PeeringType.TRANSIT_GATEWAY.toString();
        this.createAt = Instant.now();
        this.counter = 0;
        this.edgeLocation = "us-east-1";
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

    public ResourceHandlerRequest<ResourceModel> requestWithDifferentCreateOnlyProperty(ResourceModel model) {
        ResourceModel prevModel = model;
        prevModel.setCoreNetworkId("core-network-123");
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
    }

    public ResourceModel modelWithoutCreateOnlyProperties(Set<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(null)
                .resourceArn(this.resourceArn)
                .edgeLocation(this.edgeLocation)
                .peeringType(this.peeringAvailableState)
                .createdAt(this.createAt.toString())
                .transitGatewayArn(null)
                .peeringId(this.primaryIdentifier)
                .coreNetworkArn(this.coreNetworkArn)
                .ownerAccountId(this.ownerAccountId)
                .state(state)
                .tags(tags)
                .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(Set<Tag> tags) {
        return modelWithoutCreateOnlyProperties(tags, this.peeringAvailableState);
    }

    public ResourceModel modelWithoutCreateOnlyProperties() {
        final Set<Tag> tags = new HashSet<>();
        return this.modelWithoutCreateOnlyProperties(tags, this.peeringAvailableState);
    }


    public  ResourceModel modelWithNullProperties(Set<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .transitGatewayArn(this.transitGatewayArn)
                .resourceArn(this.resourceArn)
                .edgeLocation(null)
                .peeringType(null)
                .createdAt(this.createAt.toString())
                .ownerAccountId(this.ownerAccountId)
                .coreNetworkArn(this.coreNetworkArn)
                .peeringId(this.primaryIdentifier)
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithNullProperties(Set<Tag> tags) {
        return this.modelWithNullProperties(tags, this.peeringAvailableState);
    }

    public  ResourceModel modelWithoutPrimaryIdentifier(Set<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .transitGatewayArn(this.transitGatewayArn)
                .transitGatewayPeeringAttachmentId(this.transitGatewayPeeringAttachmentId)
                .resourceArn(this.resourceArn)
                .edgeLocation(null)
                .peeringType(null)
                .createdAt(this.createAt.toString())
                .ownerAccountId(this.ownerAccountId)
                .coreNetworkArn(this.coreNetworkArn)
                .peeringId(null)
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithoutPrimaryIdentifier(Set<Tag> tags) {
        return this.modelWithoutPrimaryIdentifier(tags, this.peeringAvailableState);
    }

    public  ResourceModel model(Set<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .transitGatewayArn(this.transitGatewayArn)
                .transitGatewayPeeringAttachmentId(this.transitGatewayPeeringAttachmentId)
                .resourceArn(this.resourceArn)
                .edgeLocation(this.edgeLocation)
                .peeringType(this.peeringType)
                .createdAt(this.createAt.toString())
                .ownerAccountId(this.ownerAccountId)
                .coreNetworkArn(this.coreNetworkArn)
                .peeringId(this.primaryIdentifier)
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel model() {
        final Set<Tag> tags = new HashSet<>();
        return this.model(tags, this.peeringAvailableState);
    }

    public ResourceModel model(String state) {
        final Set<Tag> tags = new HashSet<>();
        return this.model(tags, state);
    }

    public ResourceModel model(Set<Tag> tags) {
        return this.model(tags, this.peeringAvailableState);
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

    public TransitGatewayPeering sdkModelForTransitGatewayPeering(Set<Tag> tags, String state) {
        return TransitGatewayPeering.builder()
                .peering(Peering.builder()
                        .coreNetworkId(this.coreNetworkId)
                        .peeringId(this.primaryIdentifier)
                        .resourceArn(this.resourceArn)
                        .coreNetworkArn(this.coreNetworkArn)
                        .edgeLocation(this.edgeLocation)
                        .peeringType(this.peeringType)
                        .createdAt(this.createAt)
                        .ownerAccountId(this.ownerAccountId)
                        .state(state)
                        .tags(Utils.cfnTagsToSdkTags(tags))
                        .build())
                .transitGatewayArn(this.transitGatewayArn)
                .transitGatewayPeeringAttachmentId(this.transitGatewayPeeringAttachmentId)
                .build();
    }

    public Peering sdkModelForPeering(Set<Tag> tags, String state) {
        return Peering.builder()
                .coreNetworkId(this.coreNetworkId)
                .peeringId(this.primaryIdentifier)
                .resourceArn(this.resourceArn)
                .coreNetworkArn(this.coreNetworkArn)
                .edgeLocation(this.edgeLocation)
                .peeringType(this.peeringType)
                .createdAt(this.createAt)
                .ownerAccountId(this.ownerAccountId)
                .state(state)
                .tags(Utils.cfnTagsToSdkTags(tags))
                .build();
    }

    public TransitGatewayPeering sdkModelForTransitGatewayPeering() {
        return this.sdkModelForTransitGatewayPeering(new HashSet<>(), this.peeringAvailableState);
    }

    public Peering sdkModelForPeering() {
        return this.sdkModelForPeering(new HashSet<>(), this.peeringAvailableState);
    }

    public TransitGatewayPeering sdkModelForTransitGatewayPeering(Set<Tag> tags) {
        return this.sdkModelForTransitGatewayPeering(tags, this.peeringAvailableState);
    }

    public TransitGatewayPeering sdkModelForTransitGatewayPeering(String state) {
        return this.sdkModelForTransitGatewayPeering(new HashSet<>(), state);
    }

    public GetTransitGatewayPeeringResponse describeResponse(Set<Tag> tags, String state) {
        return GetTransitGatewayPeeringResponse.builder()
                .transitGatewayPeering(this.sdkModelForTransitGatewayPeering(tags, state))
                .build();
    }

    public GetTransitGatewayPeeringResponse describeResponse(String state) {
        return GetTransitGatewayPeeringResponse.builder()
                .transitGatewayPeering(this.sdkModelForTransitGatewayPeering(new HashSet<>(), state))
                .build();
    }

    public GetTransitGatewayPeeringResponse describeResponse() {
        return GetTransitGatewayPeeringResponse.builder()
                .transitGatewayPeering(sdkModelForTransitGatewayPeering())
                .build();
    }

    public ListPeeringsResponse emptyListResponse() {
        return ListPeeringsResponse.builder()
                .peerings(new ArrayList<>())
                .build();
    }

    public GetTransitGatewayPeeringResponse describeResponse(Set<Tag> tags) {
        return GetTransitGatewayPeeringResponse.builder()
                .transitGatewayPeering(this.sdkModelForTransitGatewayPeering(tags))
                .build();
    }

    public ListPeeringsResponse listResponse() {
        return ListPeeringsResponse.builder().peerings(this.sdkModelForPeering(new HashSet<>(),
                this.peeringAvailableState)).build();
    }

    public TagResourceResponse tagResourceResponse() {
        return TagResourceResponse.builder().build();
    }

    public UntagResourceResponse untagResourceResponse() {
        return UntagResourceResponse.builder().build();
    }

    public DeletePeeringResponse deleteResponse() {
        return DeletePeeringResponse.builder()
                .peering(this.sdkModelForPeering())
                .build();
    }

    public CreateTransitGatewayPeeringResponse createResponse() {
        return CreateTransitGatewayPeeringResponse.builder()
                .transitGatewayPeering(this.sdkModelForTransitGatewayPeering())
                .build();
    }

    public CreateTransitGatewayPeeringResponse createResponse(String state) {
        return CreateTransitGatewayPeeringResponse.builder()
                .transitGatewayPeering(this.sdkModelForTransitGatewayPeering(state))
                .build();
    }
}
