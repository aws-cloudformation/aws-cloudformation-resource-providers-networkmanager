package com.amazonaws.networkmanager.corenetwork.workflow.list;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.corenetwork.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.ListCoreNetworksRequest;
import software.amazon.awssdk.services.networkmanager.model.ListCoreNetworksResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
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
        ListCoreNetworksRequest awsRequest = this.translateModelToRequest(progress.getResourceModel());

        try{
            ListCoreNetworksResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
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

    private ListCoreNetworksRequest translateModelToRequest(ResourceModel model) {
        return ListCoreNetworksRequest.builder()
                .maxResults(50)
                .nextToken(this.request.getNextToken()).build();
    }

    private ListCoreNetworksResponse makeServiceCall(ListCoreNetworksRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::listCoreNetworks);
    }

    private java.util.List<ResourceModel> translateResponseToModel(ListCoreNetworksResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.coreNetworks())
                .map(coreNetworkSummary -> ResourceModel.builder()
                        .globalNetworkId(coreNetworkSummary.globalNetworkId())
                        .coreNetworkId(coreNetworkSummary.coreNetworkId())
                        .coreNetworkArn(coreNetworkSummary.coreNetworkArn())
                        .state(coreNetworkSummary.stateAsString())
                        .description(coreNetworkSummary.description())
                        .ownerAccount(coreNetworkSummary.ownerAccountId())
                        .tags(Utils.sdkTagsToCfnTags(new ArrayList<>(coreNetworkSummary.tags())))
                        .build())
                .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(ListCoreNetworksRequest awsRequest,
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
