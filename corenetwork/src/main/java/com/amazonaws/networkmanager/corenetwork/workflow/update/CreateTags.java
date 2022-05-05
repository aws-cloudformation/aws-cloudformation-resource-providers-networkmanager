package com.amazonaws.networkmanager.corenetwork.workflow.update;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import software.amazon.awssdk.services.networkmanager.model.*;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.corenetwork.workflow.Utils;
import com.amazonaws.networkmanager.corenetwork.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.proxy.*;

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
        ResourceModel previousModel = getPreviousModel(model);
        List<Tag> tags = this.tagsToCreate(previousModel, model);
        return TagResourceRequest.builder()
                .resourceArn(previousModel.getCoreNetworkArn())
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

    private ResourceModel getPreviousModel(ResourceModel model) {
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model);
    }

    private List<Tag> tagsToCreate(ResourceModel previousModel, ResourceModel currentModel) {
        List<com.amazonaws.networkmanager.corenetwork.Tag> prevTags = previousModel.getTags();
        logger.log( "previous tag " + prevTags);
        List<com.amazonaws.networkmanager.corenetwork.Tag> currTags = Utils.mergeTags(currentModel.getTags(),
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
