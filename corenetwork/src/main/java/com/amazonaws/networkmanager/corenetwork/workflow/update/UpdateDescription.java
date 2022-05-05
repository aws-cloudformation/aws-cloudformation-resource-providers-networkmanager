package com.amazonaws.networkmanager.corenetwork.workflow.update;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.corenetwork.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CoreNetworkState;
import software.amazon.awssdk.services.networkmanager.model.UpdateCoreNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.UpdateCoreNetworkResponse;
import software.amazon.cloudformation.proxy.*;

public class UpdateDescription {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;

    public UpdateDescription(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<NetworkManagerClient> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.logger = logger;
        this.callbackContext = callbackContext;
        this.client = client;
    }

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();
    }

    private UpdateCoreNetworkRequest translateModelToRequest(ResourceModel model) {
        UpdateCoreNetworkRequest.Builder updateCoreNetworkRequest = UpdateCoreNetworkRequest.builder();
        if (model.getDescription() == null || model.getDescription().equals(this.request.getPreviousResourceState().getDescription())) {
            return updateCoreNetworkRequest.build();
        }
        return updateCoreNetworkRequest
                .coreNetworkId(model.getCoreNetworkId())
                .description(model.getDescription())
                .build();
    }

    private UpdateCoreNetworkResponse makeServiceCall(UpdateCoreNetworkRequest request,
                                                      ProxyClient<NetworkManagerClient> client) {
        if (request.description() == null) {
            return UpdateCoreNetworkResponse.builder().build();
        }
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::updateCoreNetwork);
    }

    private boolean stabilize(
            UpdateCoreNetworkRequest awsRequest,
            UpdateCoreNetworkResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        if (awsResponse.coreNetwork() == null) {
            // It means customer didn't update the description and leave it as null or empty
            return true;
        }
        model.setCoreNetworkId(awsResponse.coreNetwork().coreNetworkId());
        model.setCoreNetworkArn(awsResponse.coreNetwork().coreNetworkArn());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model)
                .getState();
        return CoreNetworkState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(UpdateCoreNetworkRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

}
