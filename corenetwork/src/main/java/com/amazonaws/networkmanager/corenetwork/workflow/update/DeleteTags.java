package com.amazonaws.networkmanager.corenetwork.workflow.update;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.corenetwork.workflow.Utils;
import com.amazonaws.networkmanager.corenetwork.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        ResourceModel previousModel = getPreviousModel(model);
        List<Tag> tagsToDelete = this.tagsToDelete(previousModel, model);
        final Set<String> keysToRemove = tagsToDelete.stream().map(Tag::key).collect(Collectors.toSet());
        return UntagResourceRequest.builder()
                .resourceArn(previousModel.getCoreNetworkArn())
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

    private ResourceModel getPreviousModel(ResourceModel model) {
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model);
    }

    private List<Tag> tagsToDelete(ResourceModel previousModel, ResourceModel currentModel) {
        final List<com.amazonaws.networkmanager.corenetwork.Tag> prevTags = previousModel.getTags();
        logger.log("previous tag " + prevTags);
        final List<com.amazonaws.networkmanager.corenetwork.Tag> currTags = Utils.mergeTags(currentModel.getTags(),
                this.request.getDesiredResourceTags());
        logger.log("current tag " + currTags);
        return Utils.tagsDifference(prevTags, currTags);
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(UntagResourceRequest awsRequest,
                                                                      Exception exception,
                                                                      ProxyClient<NetworkManagerClient> client,
                                                                      ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
