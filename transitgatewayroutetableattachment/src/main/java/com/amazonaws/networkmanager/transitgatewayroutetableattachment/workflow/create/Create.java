package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.create;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.CallbackContext;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.ResourceModel;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.Utils;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.CreateSiteToSiteVpnAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.CreateSiteToSiteVpnAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.CreateTransitGatewayRouteTableAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.CreateTransitGatewayRouteTableAttachmentResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;
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

    private CreateTransitGatewayRouteTableAttachmentRequest translateModelToRequest(ResourceModel model) {
        Set<Tag> tags = Utils.jsonTagsToCfnTags(this.request.getDesiredResourceTags());
        CreateTransitGatewayRouteTableAttachmentRequest.Builder requestBuilder = CreateTransitGatewayRouteTableAttachmentRequest.builder()
                .peeringId(model.getPeeringId())
                .transitGatewayRouteTableArn(model.getTransitGatewayRouteTableArn())
                .tags(Utils.cfnTagsToSdkTags(tags));

        return requestBuilder.build();
    }

    private CreateTransitGatewayRouteTableAttachmentResponse makeServiceCall(CreateTransitGatewayRouteTableAttachmentRequest awsRequest,
                                                                             ProxyClient<NetworkManagerClient> client) {
        logger.log("create request " + awsRequest);
        return proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::createTransitGatewayRouteTableAttachment);
    }

    private boolean stabilize(
            CreateTransitGatewayRouteTableAttachmentRequest awsRequest,
            CreateTransitGatewayRouteTableAttachmentResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        model.setAttachmentId(awsResponse.transitGatewayRouteTableAttachment().attachment().attachmentId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model).getState();
        logger.log("current state is " + currentState);
        if (AttachmentState.FAILED.toString().equals(currentState)) {
            throw new RuntimeException("Fail to create Resource: " + model.getAttachmentId());
        }
        return AttachmentState.AVAILABLE.toString().equals(currentState)
                || AttachmentState.PENDING_ATTACHMENT_ACCEPTANCE.toString().equals(currentState)
                || AttachmentState.PENDING_TAG_ACCEPTANCE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayRouteTableAttachmentRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
