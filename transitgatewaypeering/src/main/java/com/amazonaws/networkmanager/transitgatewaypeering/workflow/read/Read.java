package com.amazonaws.networkmanager.transitgatewaypeering.workflow.read;

import com.amazonaws.networkmanager.transitgatewaypeering.CallbackContext;
import com.amazonaws.networkmanager.transitgatewaypeering.ResourceModel;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.Utils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayPeeringRequest;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayPeeringResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayPeering;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashSet;

public class Read {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext> progress;

    public Read(
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
        this.progress = progress;
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .handleError(this::handleError)
                .done(this::done);
    }

    public ResourceModel simpleRequest(ResourceModel model) {
        GetTransitGatewayPeeringRequest request = this.translateModelToRequest(model);
        logger.log("read request " + request);
        GetTransitGatewayPeeringResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getTransitGatewayPeering);
        logger.log("read response " + response);
        return this.translateResponsesToModel(response);
    }

    private GetTransitGatewayPeeringRequest translateModelToRequest(ResourceModel model) {
        return GetTransitGatewayPeeringRequest.builder()
                .peeringId(model.getPeeringId())
                .build();
    }

    private GetTransitGatewayPeeringResponse makeServiceCall(GetTransitGatewayPeeringRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getTransitGatewayPeering);
    }

    private ResourceModel translateResponsesToModel(GetTransitGatewayPeeringResponse awsResponse) {
        if(awsResponse.transitGatewayPeering() == null) {
            return null;
        } else {
            TransitGatewayPeering response = awsResponse.transitGatewayPeering();
            String tgwPeeringAttachmentId = null;
            if (response.transitGatewayPeeringAttachmentId() != null) {
                tgwPeeringAttachmentId = response.transitGatewayPeeringAttachmentId();
            }
            return ResourceModel.builder()
                    .transitGatewayPeeringAttachmentId(tgwPeeringAttachmentId)
                    .peeringId(response.peering().peeringId())
                    .coreNetworkId(response.peering().coreNetworkId())
                    .peeringType(response.peering().peeringTypeAsString())
                    .transitGatewayArn(response.transitGatewayArn())
                    .coreNetworkArn(response.peering().coreNetworkArn())
                    .edgeLocation(response.peering().edgeLocation())
                    .ownerAccountId(response.peering().ownerAccountId())
                    .resourceArn(response.peering().resourceArn())
                    .createdAt(response.peering().createdAt().toString())
                    .state(response.peering().stateAsString())
                    .tags(Utils.sdkTagsToCfnTags(new HashSet<>(response.peering().tags())))
                    .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(GetTransitGatewayPeeringRequest awsRequest,
                                                                      Exception exception, ProxyClient<NetworkManagerClient> client,
                                                                      ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException || exception instanceof ResourceNotFoundException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder()
                    .errorCode("NotFound")
                    .errorMessage("Not Found")
                    .build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> done(GetTransitGatewayPeeringResponse response) {
        ResourceModel model = this.translateResponsesToModel(response);
        if(model == null) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails
                            .builder().errorCode("NotFound").errorMessage("Not Found").build())
                    .build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(model);
        }
    }
}
