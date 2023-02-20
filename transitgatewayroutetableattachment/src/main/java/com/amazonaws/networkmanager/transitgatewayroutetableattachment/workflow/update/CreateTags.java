package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.update;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.CallbackContext;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.ResourceModel;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.Utils;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.TagResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Set;

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
                .backoffDelay(Utils.getBackOffStrategy())
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();
    }

    private TagResourceRequest translateModelToRequest(ResourceModel model) {
        ResourceModel previousModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model);
        Set<Tag> currTags = Utils.mergeTags(model.getTags(), this.request.getDesiredResourceTags());
        Set<Tag> prevTags;
        if (previousModel.getState().equals(AttachmentState.PENDING_TAG_ACCEPTANCE.toString())) {
            prevTags = previousModel.getProposedSegmentChange().getTags();
        } else {
            prevTags = previousModel.getTags();
        }
        Set<Tag> tags = this.tagsToCreate(prevTags, currTags);
        logger.log("this is tagging request" + this.request);
        return TagResourceRequest.builder()
                .resourceArn(Utils.getTgwRtbAttachmentArn(model.getAttachmentId(), previousModel.getTransitGatewayRouteTableArn(),
                        previousModel.getOwnerAccountId()))
                .tags(Utils.cfnTagsToSdkTags(tags))
                .build();
    }

    private TagResourceResponse makeServiceCall(TagResourceRequest request, ProxyClient<NetworkManagerClient> client) {
        if(request.tags().isEmpty()) {
            return TagResourceResponse.builder().build();
        } else {
            return proxy.injectCredentialsAndInvokeV2(request, client.client()::tagResource);
        }
    }

    private Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> tagsToCreate(
            Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> prevTags,
            Set<com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag> currTags) {
        logger.log("previous tag " + prevTags);
        logger.log("current tag " + currTags);
        return Utils.tagsDifference(currTags, prevTags);
    }

    private boolean stabilize (
            TagResourceRequest request,
            TagResourceResponse response,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model).getState();
        if (AttachmentState.FAILED.toString().equals(currentState)) {
            throw new RuntimeException("Fail to Update Resource: " + model.getAttachmentId());
        }
        return AttachmentState.AVAILABLE.toString().equals(currentState)
                || AttachmentState.PENDING_TAG_ACCEPTANCE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(TagResourceRequest awsRequest, Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client, ResourceModel model,
                                                                         CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
