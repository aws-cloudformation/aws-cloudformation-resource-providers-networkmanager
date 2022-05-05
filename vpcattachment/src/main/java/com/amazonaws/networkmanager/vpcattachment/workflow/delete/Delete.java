package com.amazonaws.networkmanager.vpcattachment.workflow.delete;

import com.amazonaws.networkmanager.vpcattachment.CallbackContext;
import com.amazonaws.networkmanager.vpcattachment.ResourceModel;
import com.amazonaws.networkmanager.vpcattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.vpcattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
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

    private DeleteAttachmentRequest translateModelToRequest(ResourceModel model) {
        return  DeleteAttachmentRequest.builder()
                .attachmentId(model.getAttachmentId())
                .build();
    }

    private DeleteAttachmentResponse makeServiceCall(DeleteAttachmentRequest awsRequest,
                                                     ProxyClient<NetworkManagerClient> client) {
        return proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::deleteAttachment);
    }

    private boolean stabilize (
            DeleteAttachmentRequest request,
            DeleteAttachmentResponse response,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        try {
            String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                    .simpleRequest(model)
                    .getState();
        } catch (ResourceNotFoundException e) {
            return true;
        }
        return false;
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DeleteAttachmentRequest awsRequest,
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
