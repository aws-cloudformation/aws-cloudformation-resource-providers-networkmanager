package com.amazonaws.networkmanager.transitgatewaypeering.workflow.create;

import com.amazonaws.networkmanager.transitgatewaypeering.CallbackContext;
import com.amazonaws.networkmanager.transitgatewaypeering.ResourceModel;
import com.amazonaws.networkmanager.transitgatewaypeering.Tag;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.Utils;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CreateTransitGatewayPeeringRequest;
import software.amazon.awssdk.services.networkmanager.model.CreateTransitGatewayPeeringResponse;
import software.amazon.awssdk.services.networkmanager.model.PeeringState;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashSet;
import java.util.Set;

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
                .backoffDelay(Utils.getBackOffStrategy())
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();
    }

    private CreateTransitGatewayPeeringRequest translateModelToRequest(ResourceModel model) {
        Set<Tag> tags = Utils.jsonTagsToCfnTags(this.request.getDesiredResourceTags());
        CreateTransitGatewayPeeringRequest.Builder requestBuilder =  CreateTransitGatewayPeeringRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .transitGatewayArn(model.getTransitGatewayArn())
                .tags(Utils.cfnTagsToSdkTags(tags));

        return requestBuilder.build();
    }

    private CreateTransitGatewayPeeringResponse makeServiceCall(CreateTransitGatewayPeeringRequest awsRequest,
                                                                  ProxyClient<NetworkManagerClient> client) {
        logger.log("create request " + awsRequest);
        return proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::createTransitGatewayPeering);
    }

    private boolean stabilize(
            CreateTransitGatewayPeeringRequest awsRequest,
            CreateTransitGatewayPeeringResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        model.setPeeringId(awsResponse.transitGatewayPeering().peering().peeringId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model).getState();
        logger.log("current state is " + currentState);
        if (PeeringState.FAILED.toString().equals(currentState)) {
            throw new RuntimeException("Fail to create Resource: " + model.getPeeringId());
        }
        return PeeringState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayPeeringRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
