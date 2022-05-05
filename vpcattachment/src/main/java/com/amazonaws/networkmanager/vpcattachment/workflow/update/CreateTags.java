package com.amazonaws.networkmanager.vpcattachment.workflow.update;

import com.amazonaws.networkmanager.vpcattachment.CallbackContext;
import com.amazonaws.networkmanager.vpcattachment.ResourceModel;
import com.amazonaws.networkmanager.vpcattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.vpcattachment.workflow.Utils;
import com.amazonaws.networkmanager.vpcattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.awssdk.services.networkmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.TagResourceResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.Collections;
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
        ResourceModel previousModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model);
        List<Tag> tags = this.tagsToCreate(previousModel, model);
        return TagResourceRequest.builder()
                .resourceArn(Utils.getVpcAttachmentArn(model.getAttachmentId(), previousModel.getResourceArn(),
                        previousModel.getOwnerAccountId()))
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
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> prevTags = previousModel.getTags();
        logger.log("previous tag " + prevTags);
        List<com.amazonaws.networkmanager.vpcattachment.Tag> updateTags = currModel.getTags() == null?
                Collections.emptyList(): currModel.getTags();
        List<com.amazonaws.networkmanager.vpcattachment.Tag> currTags = Utils.mergeTags(updateTags,
                this.request.getDesiredResourceTags());
        logger.log("current tag " + currTags);
        return Utils.tagsDifference(currTags, prevTags);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(TagResourceRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
