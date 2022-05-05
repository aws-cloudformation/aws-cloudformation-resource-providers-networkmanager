package com.amazonaws.networkmanager.vpcattachment.workflow.update;

import com.amazonaws.networkmanager.vpcattachment.CallbackContext;
import com.amazonaws.networkmanager.vpcattachment.ResourceModel;
import com.amazonaws.networkmanager.vpcattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.vpcattachment.workflow.Utils;
import com.amazonaws.networkmanager.vpcattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.*;
import java.util.stream.Collectors;

public class DeleteTags {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;

    public DeleteTags(
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
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .handleError(this::handleError)
                .progress();
    }

    private UntagResourceRequest translateModelToRequest(ResourceModel model) {
        ResourceModel previousModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        List<Tag> tags = this.tagsToDelete(previousModel, model);
        final Set<String> keysToRemove = tags.stream().map(Tag::key).collect(Collectors.toSet());
        return UntagResourceRequest.builder()
                .resourceArn(Utils.getVpcAttachmentArn(model.getAttachmentId(), previousModel.getResourceArn(), previousModel.getOwnerAccountId()))
                .tagKeys(keysToRemove)
                .build();
    }

    private UntagResourceResponse makeServiceCall(UntagResourceRequest request, ProxyClient<NetworkManagerClient> client) {
        if(request.tagKeys().isEmpty()) {
            return UntagResourceResponse.builder().build();
        } else {
            return proxy.injectCredentialsAndInvokeV2(request, client.client()::untagResource);
        }
    }

    private List<Tag> tagsToDelete(ResourceModel prevModel, ResourceModel currModel) {
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> prevTags = prevModel.getTags();
        logger.log("previous tag " + prevTags);
        List<com.amazonaws.networkmanager.vpcattachment.Tag> updateTags = currModel.getTags() == null?
                Collections.emptyList(): currModel.getTags();
        final List<com.amazonaws.networkmanager.vpcattachment.Tag> currTags = Utils.mergeTags(new ArrayList<>(updateTags),
                this.request.getDesiredResourceTags());
        logger.log("current tag " + currTags);
        return Utils.tagsDifference(prevTags, currTags);
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(UntagResourceRequest awsRequest, Exception exception,
                                                                      ProxyClient<NetworkManagerClient> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
