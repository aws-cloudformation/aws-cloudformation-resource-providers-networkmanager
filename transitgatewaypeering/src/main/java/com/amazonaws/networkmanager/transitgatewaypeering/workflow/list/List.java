package com.amazonaws.networkmanager.transitgatewaypeering.workflow.list;

import com.amazonaws.networkmanager.transitgatewaypeering.CallbackContext;
import com.amazonaws.networkmanager.transitgatewaypeering.ResourceModel;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.ListPeeringsRequest;
import software.amazon.awssdk.services.networkmanager.model.ListPeeringsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;

    public List(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<NetworkManagerClient> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
        this.logger = logger;
    }

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        ListPeeringsRequest awsRequest = this.translateModelToRequest();
        try{
            ListPeeringsResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
            java.util.List<ResourceModel> models = this.translateResponseToModel(awsResponse);
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModels(models)
                    .nextToken(awsResponse.nextToken())
                    .status(OperationStatus.SUCCESS)
                    .build();
        } catch (final Exception e) {
            return this.handleError(e);
        }

    }

    private ListPeeringsRequest translateModelToRequest() {
        return ListPeeringsRequest.builder()
                .peeringType("TRANSIT_GATEWAY")
                .maxResults(50)
                .nextToken(this.request.getNextToken()).build();
    }

    private ListPeeringsResponse makeServiceCall(ListPeeringsRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::listPeerings);
    }

    private java.util.List<ResourceModel> translateResponseToModel(ListPeeringsResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.peerings())
                .map(peering -> ResourceModel.builder()
                        .peeringId(peering.peeringId())
                        .coreNetworkId(peering.coreNetworkId())
                        .peeringType(peering.peeringTypeAsString())
                        .coreNetworkArn(peering.coreNetworkArn())
                        .edgeLocation(peering.edgeLocation())
                        .ownerAccountId(peering.ownerAccountId())
                        .resourceArn(peering.resourceArn())
                        .createdAt(peering.createdAt().toString())
                        .state(peering.stateAsString())
                        .tags(Utils.sdkTagsToCfnTags(new HashSet<>(peering.tags())))
                        .build())
                .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(Exception exception) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
