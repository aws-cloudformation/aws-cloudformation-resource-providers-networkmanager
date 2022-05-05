package com.amazonaws.networkmanager.connectpeer.workflow.list;

import com.amazonaws.networkmanager.connectpeer.CallbackContext;
import com.amazonaws.networkmanager.connectpeer.ResourceModel;
import com.amazonaws.networkmanager.connectpeer.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.connectpeer.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.ListConnectPeersRequest;
import software.amazon.awssdk.services.networkmanager.model.ListConnectPeersResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.Collection;
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
        ListConnectPeersRequest awsRequest = this.translateModelToRequest(progress.getResourceModel());

        try{
            ListConnectPeersResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
            java.util.List<ResourceModel> models = this.translateResponseToModel(awsResponse);
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModels(models)
                    .nextToken(awsResponse.nextToken())
                    .status(OperationStatus.SUCCESS)
                    .build();
        } catch (final Exception e) {
            return this.handleError(awsRequest, e, this.client, this.request.getDesiredResourceState(), this.callbackContext);
        }

    }

    private ListConnectPeersRequest translateModelToRequest(ResourceModel model) {
        ListConnectPeersRequest.Builder requestBuilder = ListConnectPeersRequest.builder()
                .maxResults(50)
                .nextToken(this.request.getNextToken());
        if(model.getCoreNetworkId() != null) {
            requestBuilder.coreNetworkId(model.getCoreNetworkId());
        }

        if(model.getConnectAttachmentId() != null) {
            requestBuilder.connectAttachmentId(model.getConnectAttachmentId());
        }
        return requestBuilder.build();
    }

    private ListConnectPeersResponse makeServiceCall(ListConnectPeersRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::listConnectPeers);
    }

    private java.util.List<ResourceModel> translateResponseToModel(ListConnectPeersResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.connectPeers())
                .map(connectPeerSummary -> ResourceModel.builder()
                        .connectPeerId(connectPeerSummary.connectPeerId())
                        .coreNetworkId(connectPeerSummary.coreNetworkId())
                        .connectAttachmentId(connectPeerSummary.connectAttachmentId())
                        .createdAt(connectPeerSummary.createdAt().toString())
                        .edgeLocation(connectPeerSummary.edgeLocation())
                        .state(connectPeerSummary.connectPeerStateAsString())
                        .tags(Utils.sdkTagsToCfnTags(connectPeerSummary.tags()))
                        .configuration(null)
                        .build())
                .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(ListConnectPeersRequest awsRequest,
                                                                      Exception exception, ProxyClient<NetworkManagerClient> client,
                                                                      ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
