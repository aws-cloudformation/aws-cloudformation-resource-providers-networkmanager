package com.amazonaws.networkmanager.connectpeer;

import com.amazonaws.networkmanager.connectpeer.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.awssdk.services.networkmanager.model.ConnectPeerBgpConfiguration;
import software.amazon.awssdk.services.networkmanager.model.ConnectPeerConfiguration;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mocks {
    public String primaryIdentifier;
    public String coreNetworkId;
    public String resourceArn;
    public String connectPeerAvailableState;
    public String edgeLocation;
    public String protocol;
    public String peerAddress;
    public String coreNetworkAddress;
    public String bgpIpAddress1;
    public String bgpIpAddress2;
    public String connectAttachmentId;
    public Double coreNetworkAsn;
    public Double peerAsn;
    public String ownerAccountID;
    public Instant createAt;
    public Integer counter;
    public List<String> insideCidrBlocks;
    public Mocks(
    ) {
        this.primaryIdentifier = "nm-connect-peer-02bb79002EXAMPLE";
        this.resourceArn = "arn:aws:networkmanager::123456789012:nm-attachment/nm-attach-02bb79002EXAMPLE";
        this.coreNetworkId = "core-network-01231231212345566";
        this.connectAttachmentId = "nm-attach-02bb79002EXAM123";
        this.peerAddress = "10.2.3.4";
        this.coreNetworkAddress = "10.3.5.8";
        this.createAt = Instant.now();
        this.connectPeerAvailableState = ConnectPeerState.AVAILABLE.toString();
        this.counter = 0;
        this.peerAsn = 650050012232312d;
        this.coreNetworkAsn = 650050012232312d;
        this.edgeLocation = "Location1";
        this.bgpIpAddress1 = "169.254.40.2";
        this.bgpIpAddress2 = "169.254.40.3";
        this.ownerAccountID = "123456789012";
        this.insideCidrBlocks = new ArrayList<>(Collections.singleton("169.254.40.0/29"));
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
                .connectAttachmentId(null)
                .peerAddress(null)
                .coreNetworkAddress(null)
                .bgpOptions(null)
                .edgeLocation(this.edgeLocation)
                .createdAt(this.createAt.toString())
                .state(state)
                .connectPeerId(this.primaryIdentifier)
                .configuration(getMockCfnConfiguration())
                .insideCidrBlocks(this.insideCidrBlocks)
                .tags(tags)
                .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags) {
        return modelWithoutCreateOnlyProperties(tags, this.connectPeerAvailableState);
    }

    public ResourceModel modelWithoutCreateOnlyProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutCreateOnlyProperties(tags, this.connectPeerAvailableState);
    }

    public  ResourceModel modelWithNullProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(null)
                .edgeLocation(null)
                .peerAddress(this.peerAddress)
                .coreNetworkAddress(this.coreNetworkAddress)
                .bgpOptions(BgpOptions.builder().peerAsn(this.peerAsn).build())
                .createdAt(this.createAt.toString())
                .state(state)
                .connectAttachmentId(this.connectAttachmentId)
                .connectPeerId(this.primaryIdentifier)
                .configuration(getMockCfnConfiguration())
                .insideCidrBlocks(this.insideCidrBlocks)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithNullProperties(List<Tag> tags) {
        return this.modelWithNullProperties(tags, this.connectPeerAvailableState);
    }

    public  ResourceModel modelWithoutPrimaryIdentifier(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .peerAddress(this.peerAddress)
                .coreNetworkAddress(this.coreNetworkAddress)
                .bgpOptions(BgpOptions.builder().peerAsn(this.peerAsn).build())
                .edgeLocation(this.edgeLocation)
                .createdAt(this.createAt.toString())
                .state(state)
                .connectAttachmentId(this.connectAttachmentId)
                .connectPeerId(null)
                .configuration(getMockCfnConfiguration())
                .insideCidrBlocks(this.insideCidrBlocks)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithoutPrimaryIdentifier(List<Tag> tags) {
        return this.modelWithoutPrimaryIdentifier(tags, this.connectPeerAvailableState);
    }

    public  ResourceModel modelWithoutWriteOnlyProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .coreNetworkId(this.coreNetworkId)
                .peerAddress(null)
                .coreNetworkAddress(null)
                .bgpOptions(null)
                .edgeLocation(this.edgeLocation)
                .createdAt(this.createAt.toString())
                .state(state)
                .connectAttachmentId(this.connectAttachmentId)
                .connectPeerId(this.primaryIdentifier)
                .configuration(getMockCfnConfiguration())
                .insideCidrBlocks(null)
                .tags(tags)
                .build();
    }

    public ResourceModel modelWithoutWriteOnlyProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutWriteOnlyProperties(tags, this.connectPeerAvailableState);
    }

    public ResourceModel modelWithoutWriteOnlyProperties(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutWriteOnlyProperties(tags, state);
    }

    public Tag cfnTag(String key, String value) {
        return Tag.builder().key(key).value(value).build();
    }

    public ResourceModel modelWithoutWriteOnlyProperties(List<Tag> tags) {
        return this.modelWithoutWriteOnlyProperties(tags, this.connectPeerAvailableState);
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

    public ConnectPeer sdkModelForConnectPeer(List<Tag> tags, String state) {
        return ConnectPeer.builder()
                .coreNetworkId(this.coreNetworkId)
                .edgeLocation(this.edgeLocation)
                .createdAt(this.createAt)
                .state(state)
                .connectAttachmentId(this.connectAttachmentId)
                .connectPeerId(this.primaryIdentifier)
                .configuration(getMockSdkConfiguration())
                .tags(Utils.cfnTagsToSdkTags(tags))
                .build();
    }

    public ConnectPeer sdkModelForConnectPeer() {
        return this.sdkModelForConnectPeer(new ArrayList<>(), this.connectPeerAvailableState);
    }

    public ConnectPeer sdkModelForConnectPeer(List<Tag> tags) {
        return this.sdkModelForConnectPeer(tags, this.connectPeerAvailableState);
    }

    public ConnectPeer sdkModelForConnectPeer(String state) {
        return this.sdkModelForConnectPeer(new ArrayList<>(), state);
    }

    public TagResourceResponse tagResourceResponse() {
        return TagResourceResponse.builder().build();
    }

    public UntagResourceResponse untagResourceResponse() {
        return UntagResourceResponse.builder().build();
    }

    public CreateConnectPeerResponse createResponse() {
        return CreateConnectPeerResponse.builder()
                .connectPeer(this.sdkModelForConnectPeer())
                .build();
    }

    public CreateConnectPeerResponse createResponse(String state) {
        return CreateConnectPeerResponse.builder()
                .connectPeer(this.sdkModelForConnectPeer(state))
                .build();
    }

    public GetConnectPeerResponse describeResponse(List<Tag> tags, String state) {
        return GetConnectPeerResponse.builder()
                .connectPeer(this.sdkModelForConnectPeer(tags, state))
                .build();
    }

    public GetConnectPeerResponse describeResponse(String state) {
        return GetConnectPeerResponse.builder()
                .connectPeer(this.sdkModelForConnectPeer(new ArrayList<>(), state))
                .build();
    }

    public GetConnectPeerResponse describeResponse() {
        return GetConnectPeerResponse.builder()
                .connectPeer(this.sdkModelForConnectPeer())
                .build();
    }

    public GetConnectPeerResponse describeResponse(List<Tag> tags) {
        return GetConnectPeerResponse.builder()
                .connectPeer(this.sdkModelForConnectPeer(tags))
                .build();
    }

    public ListConnectPeersResponse listResponse() {
        return ListConnectPeersResponse.builder().connectPeers(getConnectPeerSummarys()).build();
    }

    public DeleteConnectPeerResponse deleteResponse() {
        return DeleteConnectPeerResponse.builder()
                .connectPeer(this.sdkModelForConnectPeer())
                .build();
    }

    public List<ConnectPeerSummary> getConnectPeerSummarys() {
        return Collections.singletonList(ConnectPeerSummary.builder()
                .connectAttachmentId(this.connectAttachmentId)
                .connectPeerId(this.primaryIdentifier)
                .connectPeerState(this.connectPeerAvailableState)
                .coreNetworkId(this.coreNetworkId)
                .createdAt(this.createAt)
                .edgeLocation(this.edgeLocation)
                .tags(this.getSdkTag())
                .build());
    }

    public com.amazonaws.networkmanager.connectpeer.ConnectPeerConfiguration getMockCfnConfiguration() {
        return com.amazonaws.networkmanager.connectpeer.ConnectPeerConfiguration.builder()
                .coreNetworkAddress(this.coreNetworkAddress)
                .protocol(this.protocol)
                .insideCidrBlocks(this.insideCidrBlocks)
                .bgpConfigurations(getMockCfnBgpConfigurations())
                .peerAddress(this.peerAddress)
                .build();
    }

    public List<com.amazonaws.networkmanager.connectpeer.ConnectPeerBgpConfiguration> getMockCfnBgpConfigurations() {
        List<com.amazonaws.networkmanager.connectpeer.ConnectPeerBgpConfiguration> bgpConfigurations = new ArrayList<>();
        List<String> bgpIpAddresses = new ArrayList<>();
        bgpIpAddresses.add(this.bgpIpAddress1);
        bgpIpAddresses.add(this.bgpIpAddress2);
        bgpIpAddresses.forEach(bgpIpAddress -> {
            bgpConfigurations.add(com.amazonaws.networkmanager.connectpeer.ConnectPeerBgpConfiguration.builder()
                    .coreNetworkAddress(this.coreNetworkAddress)
                    .coreNetworkAsn(this.coreNetworkAsn)
                    .peerAddress(bgpIpAddress)
                    .peerAsn(this.peerAsn)
                    .build());
        });
        return bgpConfigurations;
    }

    public ConnectPeerConfiguration getMockSdkConfiguration() {
        return ConnectPeerConfiguration.builder()
                .coreNetworkAddress(this.coreNetworkAddress)
                .peerAddress(this.peerAddress)
                .protocol(this.protocol)
                .insideCidrBlocks(this.insideCidrBlocks)
                .bgpConfigurations(getMockSdkBgpConfigurations())
                .build();
    }

    public List<ConnectPeerBgpConfiguration> getMockSdkBgpConfigurations() {
        List<ConnectPeerBgpConfiguration> bgpConfigurations = new ArrayList<>();
        List<String> bgpIpAddresses = new ArrayList<>();
        bgpIpAddresses.add(this.bgpIpAddress1);
        bgpIpAddresses.add(this.bgpIpAddress2);
        bgpIpAddresses.forEach(bgpIpAddress -> {
            bgpConfigurations.add(ConnectPeerBgpConfiguration.builder()
                    .coreNetworkAddress(this.coreNetworkAddress)
                    .coreNetworkAsn(this.coreNetworkAsn.longValue())
                    .peerAddress(bgpIpAddress)
                    .peerAsn(this.peerAsn.longValue())
                    .build());
        });
        return bgpConfigurations;
    }

    public GetConnectAttachmentResponse getConnectAttachmentResponse() {
        return GetConnectAttachmentResponse.builder().connectAttachment(
                ConnectAttachment.builder().attachment(Attachment.builder()
                                .ownerAccountId(this.ownerAccountID)
                                .build()).build()).build();
    }
}
