package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.read;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.CallbackContext;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.ResourceModel;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.Utils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Attachment;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRouteTableAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRouteTableAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRouteTableAttachment;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Set;

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
        GetTransitGatewayRouteTableAttachmentRequest request = this.translateModelToRequest(model);
        logger.log("read request " + request);
        GetTransitGatewayRouteTableAttachmentResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getTransitGatewayRouteTableAttachment);
        logger.log("read response " + response);
        return this.translateResponsesToModel(response, model);
    }

    private GetTransitGatewayRouteTableAttachmentRequest translateModelToRequest(ResourceModel model) {
        return GetTransitGatewayRouteTableAttachmentRequest.builder()
                .attachmentId(model.getAttachmentId())
                .build();
    }

    private GetTransitGatewayRouteTableAttachmentResponse makeServiceCall(GetTransitGatewayRouteTableAttachmentRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getTransitGatewayRouteTableAttachment);
    }

    private ResourceModel translateResponsesToModel(GetTransitGatewayRouteTableAttachmentResponse awsResponse, ResourceModel model) {
        if(awsResponse.transitGatewayRouteTableAttachment() == null) {
            return null;
        } else {
            TransitGatewayRouteTableAttachment response = awsResponse.transitGatewayRouteTableAttachment();
            Attachment attachment = response.attachment();
            String coreNetworkArn = Utils.getCoreNetworkArn(attachment.coreNetworkId(), attachment.resourceArn(), attachment.ownerAccountId());
            Set<Tag> tags = Utils.sdkTagsToCfnTags(Utils.taglistToSet(response.attachment().tags()));
            return ResourceModel.builder()
                    .peeringId(response.peeringId())
                    .transitGatewayRouteTableArn(response.transitGatewayRouteTableArn())
                    .attachmentId(attachment.attachmentId())
                    .attachmentType(attachment.attachmentTypeAsString())
                    .coreNetworkArn(coreNetworkArn)
                    .coreNetworkId(attachment.coreNetworkId())
                    .ownerAccountId(attachment.ownerAccountId())
                    .attachmentPolicyRuleNumber(attachment.attachmentPolicyRuleNumber())
                    .segmentName(attachment.segmentName())
                    .proposedSegmentChange(Utils.sdkSegmentChangeToCfnSegmentChange(attachment.proposedSegmentChange()))
                    .resourceArn(attachment.resourceArn())
                    .edgeLocation(attachment.edgeLocation())
                    .createdAt(attachment.createdAt().toString())
                    .updatedAt(attachment.updatedAt().toString())
                    .state(attachment.stateAsString())
                    .tags(tags)
                    .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(GetTransitGatewayRouteTableAttachmentRequest awsRequest,
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

    private ProgressEvent<ResourceModel, CallbackContext> done(GetTransitGatewayRouteTableAttachmentResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
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
