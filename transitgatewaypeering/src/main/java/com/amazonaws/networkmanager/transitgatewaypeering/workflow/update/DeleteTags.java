package com.amazonaws.networkmanager.transitgatewaypeering.workflow.update;

import com.amazonaws.networkmanager.transitgatewaypeering.CallbackContext;
import com.amazonaws.networkmanager.transitgatewaypeering.ResourceModel;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.Utils;
import com.amazonaws.networkmanager.transitgatewaypeering.Tag;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        ResourceModel previousModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> currTags = Utils.mergeTags(model.getTags(), this.request.getDesiredResourceTags());
        Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> prevTags = previousModel.getTags();
        Set<com.amazonaws.networkmanager.transitgatewaypeering.Tag> tags = this.tagsToDelete(prevTags, currTags);
        final Set<String> keysToRemove = tags.stream().map(Tag::getKey).collect(Collectors.toSet());
        return UntagResourceRequest.builder()
                .resourceArn(Utils.getPeeringArn(model.getPeeringId(), previousModel.getTransitGatewayArn(),
                        previousModel.getOwnerAccountId()))
                .tagKeys(keysToRemove)
                .build();
    }

    private UntagResourceResponse makeServiceCall(UntagResourceRequest request, ProxyClient<NetworkManagerClient> client) {
        if(request.tagKeys().isEmpty()) {
            return UntagResourceResponse.builder().build();
        } else {
            logger.log("Prepare to delete tags");
            return proxy.injectCredentialsAndInvokeV2(request, client.client()::untagResource);
        }
    }

    private Set<Tag> tagsToDelete(Set<Tag> prevTags, Set<Tag> currTags) {
        logger.log("previous tag " + prevTags);
        logger.log("current tag " + currTags);
        return Utils.tagsDifference(prevTags, currTags);
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(UntagResourceRequest awsRequest, Exception exception,
                                                                      ProxyClient<NetworkManagerClient> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
