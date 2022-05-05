package com.amazonaws.networkmanager.corenetwork.workflow.create;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.Tag;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.corenetwork.workflow.Utils;
import com.amazonaws.networkmanager.corenetwork.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

public class Create {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;

    public Create(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<NetworkManagerClient> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.logger = logger;
        this.callbackContext = callbackContext;
        this.client = client;
    }

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();
    }

    private CreateCoreNetworkRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = (model.getTags() != null) ? Utils.mergeTags(new ArrayList<>(model.getTags()),
                this.request.getDesiredResourceTags()) : new ArrayList<>();
        CreateCoreNetworkRequest.Builder requestBuilder = CreateCoreNetworkRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .tags(Utils.cfnTagsToSdkTags(tags));

        if (model.getDescription() != null && !model.getDescription().isEmpty()) {
            requestBuilder.description(model.getDescription());
        }
        if (model.getPolicyDocument() != null && !model.getPolicyDocument().isEmpty()) {
            requestBuilder.policyDocument(model.getPolicyDocument());
        }

        return requestBuilder.build();
    }

    private CreateCoreNetworkResponse makeServiceCall(CreateCoreNetworkRequest awsRequest,
                                                      ProxyClient<NetworkManagerClient> client) {
        logger.log("Making Create service call with request " + awsRequest);
        return proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::createCoreNetwork);
    }

    private boolean stabilize(
            CreateCoreNetworkRequest awsRequest,
            CreateCoreNetworkResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        model.setCoreNetworkId(awsResponse.coreNetwork().coreNetworkId());
        model.setCoreNetworkArn(awsResponse.coreNetwork().coreNetworkArn());
        GetCoreNetworkRequest getCoreNetworkRequest = GetCoreNetworkRequest.builder()
                .coreNetworkId(model.getCoreNetworkId()).build();
        String currentState = this.proxy.injectCredentialsAndInvokeV2(getCoreNetworkRequest,
                client.client()::getCoreNetwork).coreNetwork().stateAsString();
        return CoreNetworkState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateCoreNetworkRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
