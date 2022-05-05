package com.amazonaws.networkmanager.connectattachment.workflow.update;

import com.amazonaws.networkmanager.connectattachment.CallbackContext;
import com.amazonaws.networkmanager.connectattachment.ResourceModel;
import com.amazonaws.networkmanager.connectattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.connectattachment.workflow.Utils;
import com.amazonaws.networkmanager.connectattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class CreateTags {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext> progress;

    public CreateTags(
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
                .makeServiceCall(this::makeServiceCall)
                .handleError(this::handleError)
                .progress();
    }

    private TagResourceRequest translateModelToRequest(ResourceModel model) {
        ResourceModel previousModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        List<Tag> tags = this.tagsToCreate(previousModel, model);
        return TagResourceRequest.builder()
                .resourceArn(Utils.getConnectAttachmentArn(model.getAttachmentId(), previousModel.getResourceArn(), previousModel.getOwnerAccountId()))
                .tags(tags)
                .build();
    }

    private TagResourceResponse makeServiceCall(TagResourceRequest request, ProxyClient<NetworkManagerClient> client) {
        if(request.tags().isEmpty()) {
            return TagResourceResponse.builder().build();
        } else {
            return proxy.injectCredentialsAndInvokeV2(request, client.client()::tagResource);
        }
    }

    private List<Tag> tagsToCreate(ResourceModel previousModel, ResourceModel currModel) {
        final List<com.amazonaws.networkmanager.connectattachment.Tag> prevTags = previousModel.getTags();
        List<com.amazonaws.networkmanager.connectattachment.Tag> currTags = Utils.mergeTags(new ArrayList<>(currModel.getTags()),
                this.request.getDesiredResourceTags());
        return Utils.tagsDifference(currTags, prevTags);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(TagResourceRequest awsRequest, Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
