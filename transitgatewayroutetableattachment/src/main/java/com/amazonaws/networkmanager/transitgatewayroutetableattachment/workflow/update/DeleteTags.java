package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.update;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.CallbackContext;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.ResourceModel;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.Utils;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
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
    String attachmentState =  null;

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
                .backoffDelay(Utils.getBackOffStrategy())
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();
    }

    private UntagResourceRequest translateModelToRequest(ResourceModel model) {
        ResourceModel previousModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        Set<Tag> currTags = Utils.mergeTags(model.getTags(), this.request.getDesiredResourceTags());
        Set<Tag> prevTags;
        if (previousModel.getState().equals(AttachmentState.PENDING_TAG_ACCEPTANCE.toString())) {
            prevTags = previousModel.getProposedSegmentChange().getTags();
        } else {
            prevTags = previousModel.getTags();
        }
        logger.log("attachment state " + this.attachmentState);
        Set<Tag> tags = this.tagsToDelete(prevTags, currTags);
        final Set<String> keysToRemove = tags.stream().map(Tag::getKey).collect(Collectors.toSet());
        return UntagResourceRequest.builder()
                .resourceArn(Utils.getTgwRtbAttachmentArn(model.getAttachmentId(), previousModel.getTransitGatewayRouteTableArn(),
                        previousModel.getOwnerAccountId()))
                .tagKeys(keysToRemove)
                .build();
    }

    private UntagResourceResponse makeServiceCall(UntagResourceRequest request, ProxyClient<NetworkManagerClient> client) {
        if (request.tagKeys().isEmpty()) {
            return UntagResourceResponse.builder().build();
        } else {
            logger.log("Prepare to delete tags");
            return proxy.injectCredentialsAndInvokeV2(request, client.client()::untagResource);
        }
    }

    private boolean stabilize (
            UntagResourceRequest request,
            UntagResourceResponse response,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model).getState();
        logger.log("current state is " + currentState);
        if (AttachmentState.FAILED.toString().equals(currentState)) {
            throw new RuntimeException("Fail to Update Resource: " + model.getAttachmentId());
        }
        return AttachmentState.AVAILABLE.toString().equals(currentState)
                || AttachmentState.PENDING_TAG_ACCEPTANCE.toString().equals(currentState);
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
