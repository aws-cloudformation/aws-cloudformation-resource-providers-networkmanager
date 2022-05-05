package com.amazonaws.networkmanager.corenetwork.workflow.delete;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.corenetwork.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeleteCoreNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.DeleteCoreNetworkResponse;
import software.amazon.awssdk.services.networkmanager.model.GetCoreNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.*;

public class Delete {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext> progress;

    public Delete(
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

    public ProgressEvent<ResourceModel, CallbackContext>  run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        this.progress = progress;
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();

    }

    private DeleteCoreNetworkRequest translateModelToRequest(ResourceModel model) {
        return  DeleteCoreNetworkRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .build();
    }

    private DeleteCoreNetworkResponse makeServiceCall(DeleteCoreNetworkRequest awsRequest,
                                                      ProxyClient<NetworkManagerClient> client) {
        logger.log("Making Delete service call with request " + awsRequest);
        return proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::deleteCoreNetwork);
    }

    private boolean stabilize (
            DeleteCoreNetworkRequest request,
            DeleteCoreNetworkResponse response,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        try {
            GetCoreNetworkRequest getCoreNetworkRequest = GetCoreNetworkRequest.builder()
                    .coreNetworkId(model.getCoreNetworkId()).build();
            String currentState = this.proxy.injectCredentialsAndInvokeV2(getCoreNetworkRequest,
                    client.client()::getCoreNetwork).coreNetwork().stateAsString();
        } catch (ResourceNotFoundException e) {
            return true;
        }
        return false;
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DeleteCoreNetworkRequest awsRequest,
                                                                       Exception exception,
                                                                       ProxyClient<NetworkManagerClient> client,
                                                                       ResourceModel model, CallbackContext context) {
        if (ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
