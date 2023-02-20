package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.delete;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.CallbackContext;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.ResourceModel;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.Utils;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeleteAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.DeleteAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.DeletePeeringRequest;
import software.amazon.awssdk.services.networkmanager.model.DeletePeeringResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .backoffDelay(Utils.getBackOffStrategy())
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
        logger.log("delete request " + awsRequest);
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
