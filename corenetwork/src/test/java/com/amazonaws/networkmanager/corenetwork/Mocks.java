package com.amazonaws.networkmanager.corenetwork;

import com.amazonaws.networkmanager.corenetwork.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mocks {
    public String primaryIdentifier;
    public String globalNetworkId;
    public String description;
    public String defaultCoreNetworkAvailableState;
    public String coreNetworkArn;
    public String edgeLocation;
    public String segmentName;
    public String ownerAccount;
    public String policyDocument;
    public String executedSuccessfullyChangeSetState;
    public String executingChangeSetState;
    public Instant currentTime;
    public Integer counter;
    public Double asn;
    public Integer policyVersionId;
    public List<String> insideCidrBlocks;
    public List<String> sharedSegments;
    public Mocks(
    ) {
        this.primaryIdentifier = "core-network-02bb79002EXAMPLE";
        this.coreNetworkArn = "arn:aws:networkmanager::123456789012:core-network/core-network-02bb79002EXAMPLE";
        this.description = "Mock description";
        this.globalNetworkId = "global-network-01231231212345566";
        this.policyDocument = "{fake policy}";
        this.defaultCoreNetworkAvailableState = CoreNetworkState.AVAILABLE.toString();
        this.currentTime = Instant.now();
        this.counter = 0;
        this.policyVersionId = 2;
        this.asn = 650050012232312d;
        this.edgeLocation = "Location1";
        this.insideCidrBlocks = new ArrayList<>(Collections.singleton("20.20.1.0/24"));
        this.sharedSegments = new ArrayList<>(Collections.singleton("segment1"));
        this.segmentName = "segment";
        this.ownerAccount = "123456789012";
        this.executedSuccessfullyChangeSetState = ChangeSetState.EXECUTION_SUCCEEDED.toString();
        this.executingChangeSetState = ChangeSetState.EXECUTING.toString();
    }

    public ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
    }

    public ResourceHandlerRequest<ResourceModel> request(ResourceModel model, ResourceModel previousModel) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(previousModel)
                .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String state,
                                                           String description, String policyDocument) {
        return ResourceModel.builder()
                .coreNetworkId(this.primaryIdentifier)
                .coreNetworkArn(this.coreNetworkArn)
                .globalNetworkId(null)
                .ownerAccount(this.ownerAccount)
                .createdAt(this.currentTime.toString())
                .description(description)
                .policyDocument(policyDocument)
                .segments(getMockCfnSegments())
                .edges(getMockCfnEdges())
                .state(state)
                .tags(tags)
                .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags) {
        return modelWithoutCreateOnlyProperties(tags, this.defaultCoreNetworkAvailableState, this.description, this.policyDocument);
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String description, String policyDocument) {
        return modelWithoutCreateOnlyProperties(tags, this.defaultCoreNetworkAvailableState, description, policyDocument);
    }

    public ResourceModel modelWithoutCreateOnlyProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutCreateOnlyProperties(tags, this.defaultCoreNetworkAvailableState, this.description, this.policyDocument);
    }

    public  ResourceModel modelWithNullProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .globalNetworkId(this.globalNetworkId)
                .coreNetworkId(this.primaryIdentifier)
                .coreNetworkArn(this.coreNetworkArn)
                .policyDocument(this.policyDocument)
                .ownerAccount(null)
                .createdAt(null)
                .description(this.description)
                .segments(null)
                .edges(getMockCfnEdges())
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithNullProperties(List<Tag> tags) {
        return this.modelWithNullProperties(tags, this.defaultCoreNetworkAvailableState);
    }

    public  ResourceModel modelWithoutPrimaryIdentifier(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .globalNetworkId(this.globalNetworkId)
                .coreNetworkId(null)
                .coreNetworkArn(null)
                .ownerAccount(null)
                .createdAt(this.currentTime.toString())
                .policyDocument(this.policyDocument)
                .description(this.description)
                .segments(getMockCfnSegments())
                .edges(getMockCfnEdges())
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithoutPrimaryIdentifier(List<Tag> tags) {
        return this.modelWithoutPrimaryIdentifier(tags, this.defaultCoreNetworkAvailableState);
    }

    public  ResourceModel model(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .globalNetworkId(this.globalNetworkId)
                .coreNetworkId(this.primaryIdentifier)
                .coreNetworkArn(this.coreNetworkArn)
                .ownerAccount(null)
                .createdAt(this.currentTime.toString())
                .policyDocument(this.policyDocument)
                .description(this.description)
                .segments(getMockCfnSegments())
                .edges(getMockCfnEdges())
                .state(state)
                .tags(tags)
                .build();
    }

    public ResourceModel model() {
        final List<Tag> tags = new ArrayList<>();
        return this.model(tags, this.defaultCoreNetworkAvailableState);
    }

    public ResourceModel model(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.model(tags, state);
    }

    public ResourceModel model(List<Tag> tags) {
        return this.model(tags, this.defaultCoreNetworkAvailableState);
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

    public CoreNetwork sdkModel(List<Tag> tags, String state, String description) {
        return CoreNetwork.builder()
                .coreNetworkId(this.primaryIdentifier)
                .globalNetworkId(this.globalNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .createdAt(this.currentTime)
                .description(description)
                .segments(getMockSdkSegments())
                .edges(getMockSdkEdges())
                .state(state)
                .tags(Utils.cfnTagsToSdkTags(tags))
                .build();
    }

    public CoreNetwork sdkModel() {
        return this.sdkModel(new ArrayList<>(), this.defaultCoreNetworkAvailableState, this.description);
    }

    public CoreNetwork sdkModel(List<Tag> tags) {
        return this.sdkModel(tags, this.defaultCoreNetworkAvailableState, this.description);
    }

    public GetCoreNetworkResponse describeResponse(List<Tag> tags, String state, String description) {
        return GetCoreNetworkResponse.builder()
                .coreNetwork(this.sdkModel(tags, state, description))
                .build();
    }

    public GetCoreNetworkResponse describeResponse(List<Tag> tags, String state) {
        return GetCoreNetworkResponse.builder()
                .coreNetwork(this.sdkModel(tags, state, this.description))
                .build();
    }

    public GetCoreNetworkResponse describeResponse(String state, String description) {
        return GetCoreNetworkResponse.builder()
                .coreNetwork(this.sdkModel(new ArrayList<>(), state, description))
                .build();
    }

    public GetCoreNetworkResponse describeResponse() {
        return GetCoreNetworkResponse.builder()
                .coreNetwork(this.sdkModel())
                .build();
    }

    public ListCoreNetworksResponse emptyListResponse() {
        return ListCoreNetworksResponse.builder()
                .coreNetworks(new ArrayList<>())
                .build();
    }

    public ListCoreNetworksResponse listResponse() {
        return ListCoreNetworksResponse.builder().coreNetworks(this.getCoreNetworkSummaries()).build();
    }

    public UpdateCoreNetworkResponse updateResponse(List<Tag> tags, String description) {
        CoreNetwork coreNetwork = CoreNetwork.builder().coreNetworkId(this.primaryIdentifier)
                .globalNetworkId(this.globalNetworkId)
                .coreNetworkArn(this.coreNetworkArn)
                .createdAt(this.currentTime)
                .description(description)
                .segments(getMockSdkSegments())
                .edges(getMockSdkEdges())
                .state(this.defaultCoreNetworkAvailableState)
                .tags(Utils.cfnTagsToSdkTags(tags))
                .build();

        return UpdateCoreNetworkResponse.builder().coreNetwork(coreNetwork).build();
    }

    public UpdateCoreNetworkResponse updateResponse(String description) {
        return updateResponse(new ArrayList<>(), description);
    }

    public PutCoreNetworkPolicyResponse putCoreNetworkPolicyResponse(String policyDocument) {
        return PutCoreNetworkPolicyResponse.builder()
                .coreNetworkPolicy(CoreNetworkPolicy.builder()
                        .policyDocument(policyDocument)
                        .coreNetworkId(this.primaryIdentifier)
                        .description(this.description)
                        .createdAt(this.currentTime)
                        .policyVersionId(this.policyVersionId + 1)
                        .policyErrors(new ArrayList<>())
                        .alias("LATEST")
                        .changeSetState(ChangeSetState.READY_TO_EXECUTE)
                        .build())
                .build();
    }

    public GetCoreNetworkPolicyResponse getCoreNetworkPolicyResponse(String policyDocument, String changeSetState) {
        return GetCoreNetworkPolicyResponse.builder()
                .coreNetworkPolicy(CoreNetworkPolicy.builder()
                        .policyDocument(policyDocument)
                        .coreNetworkId(this.primaryIdentifier)
                        .description(this.description)
                        .createdAt(this.currentTime)
                        .policyVersionId(this.policyVersionId + 1)
                        .policyErrors(new ArrayList<>())
                        .alias("LATEST")
                        .changeSetState(changeSetState)
                        .build())
                .build();
    }

    public GetCoreNetworkPolicyResponse getCoreNetworkPolicyResponse() {
        return getCoreNetworkPolicyResponse(this.policyDocument, this.executedSuccessfullyChangeSetState);
    }

    public ExecuteCoreNetworkChangeSetResponse getExecuteChangeSetResponse() {
        return ExecuteCoreNetworkChangeSetResponse.builder().build();
    }

    public List<CoreNetworkSummary> getCoreNetworkSummaries() {
        return Collections.singletonList(CoreNetworkSummary.builder()
                .globalNetworkId(this.globalNetworkId)
                .coreNetworkId(this.primaryIdentifier)
                .coreNetworkArn(this.coreNetworkArn)
                .description(this.description)
                .ownerAccountId(this.ownerAccount)
                .state(this.defaultCoreNetworkAvailableState)
                .build());
    }

    public GetCoreNetworkResponse describeResponse(List<Tag> tags) {
        return this.describeResponse(tags, this.defaultCoreNetworkAvailableState, this.description);
    }

    public TagResourceResponse tagResourceResponse() {
        return TagResourceResponse.builder().build();
    }

    public UntagResourceResponse untagResourceResponse() {
        return UntagResourceResponse.builder().build();
    }

    public DeleteCoreNetworkResponse deleteResponse() {
        return DeleteCoreNetworkResponse.builder()
                .coreNetwork(this.sdkModel())
                .build();
    }

    public CreateCoreNetworkResponse createResponse() {
        return CreateCoreNetworkResponse.builder()
                .coreNetwork(this.sdkModel())
                .build();
    }

    public List<CoreNetworkSegment> getMockCfnSegments() {
        return Collections.singletonList(CoreNetworkSegment.builder()
                .edgeLocations(new ArrayList<>(Collections.singleton(this.edgeLocation)))
                .name(this.segmentName)
                .sharedSegments(this.sharedSegments)
                .build());
    }

    public List<CoreNetworkEdge> getMockCfnEdges() {
        return Collections.singletonList(CoreNetworkEdge.builder()
                .asn(this.asn)
                .edgeLocation(this.edgeLocation)
                .insideCidrBlocks(this.insideCidrBlocks)
                .build());
    }

    public List<software.amazon.awssdk.services.networkmanager.model.CoreNetworkSegment> getMockSdkSegments() {
        return Collections.singletonList(software.amazon.awssdk.services.networkmanager.model.CoreNetworkSegment.builder()
                .edgeLocations(this.edgeLocation)
                .name(this.segmentName)
                .sharedSegments(this.sharedSegments)
                .build());
    }

    public List<software.amazon.awssdk.services.networkmanager.model.CoreNetworkEdge> getMockSdkEdges() {
        return Collections.singletonList(software.amazon.awssdk.services.networkmanager.model.CoreNetworkEdge.builder()
                .asn(this.asn.longValue())
                .edgeLocation(this.edgeLocation)
                .insideCidrBlocks(this.insideCidrBlocks)
                .build());
    }
}
