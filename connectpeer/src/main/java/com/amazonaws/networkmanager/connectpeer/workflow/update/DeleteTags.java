package com.amazonaws.networkmanager.connectpeer.workflow.update;

import com.amazonaws.networkmanager.connectpeer.CallbackContext;
import com.amazonaws.networkmanager.connectpeer.ResourceModel;
import com.amazonaws.networkmanager.connectpeer.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.connectpeer.workflow.Utils;
import com.amazonaws.networkmanager.connectpeer.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.*;

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
        ResourceModel previousModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        List<Tag> tags = this.tagsToDelete(previousModel, model);
        final Set<String> keysToRemove = tags.stream().map(Tag::key).collect(Collectors.toSet());
        return UntagResourceRequest.builder()
                .resourceArn(Utils.getConnectPeerArn(model.getConnectPeerId(), model.getEdgeLocation(), this.request.getAwsAccountId()))
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
        final List<com.amazonaws.networkmanager.connectpeer.Tag> prevTags = prevModel.getTags();
        final List<com.amazonaws.networkmanager.connectpeer.Tag> currTags = Utils.mergeTags(new ArrayList<>(currModel.getTags()),
                this.request.getDesiredResourceTags());
        return Utils.tagsDifference(prevTags, currTags);
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(UntagResourceRequest awsRequest, Exception exception,
                                                                      ProxyClient<NetworkManagerClient> client,
                                                                      ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
