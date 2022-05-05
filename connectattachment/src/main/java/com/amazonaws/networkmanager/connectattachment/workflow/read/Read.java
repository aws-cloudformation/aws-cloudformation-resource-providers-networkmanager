package com.amazonaws.networkmanager.connectattachment.workflow.read;

import com.amazonaws.networkmanager.connectattachment.CallbackContext;
import com.amazonaws.networkmanager.connectattachment.ResourceModel;
import com.amazonaws.networkmanager.connectattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.connectattachment.workflow.Utils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Attachment;
import software.amazon.awssdk.services.networkmanager.model.ConnectAttachment;
import software.amazon.awssdk.services.networkmanager.model.GetConnectAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.GetConnectAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        GetConnectAttachmentRequest request = this.translateModelToRequest(model);
        GetConnectAttachmentResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getConnectAttachment);
        return this.translateResponsesToModel(response, model);
    }

    private GetConnectAttachmentRequest translateModelToRequest(ResourceModel model) {
        return GetConnectAttachmentRequest.builder()
                .attachmentId(model.getAttachmentId())
                .build();
    }

    private GetConnectAttachmentResponse makeServiceCall(GetConnectAttachmentRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getConnectAttachment);
    }

    private ResourceModel translateResponsesToModel(GetConnectAttachmentResponse awsResponse, ResourceModel model) {
        if(awsResponse.connectAttachment() == null) {
            return null;
        } else {
            logger.log("aws response " + awsResponse);
            ConnectAttachment response = awsResponse.connectAttachment();
            Attachment attachment = response.attachment();
            String segmentName = attachment.segmentName();
            Integer attachmentPolicyRuleNumber = attachment.attachmentPolicyRuleNumber();
            return ResourceModel.builder()
                    .attachmentId(attachment.attachmentId())
                    .attachmentType(attachment.attachmentTypeAsString())
                    .coreNetworkId(attachment.coreNetworkId())
                    .ownerAccountId(attachment.ownerAccountId())
                    .attachmentPolicyRuleNumber(attachmentPolicyRuleNumber)
                    .segmentName(segmentName)
                    .proposedSegmentChange(Utils.sdkSegmentChangeToCfnSegmentChange(attachment.proposedSegmentChange()))
                    .resourceArn(attachment.resourceArn())
                    .edgeLocation(attachment.edgeLocation())
                    .createdAt(attachment.createdAt().toString())
                    .updatedAt(attachment.updatedAt().toString())
                    .state(attachment.stateAsString())
                    .tags(Utils.sdkTagsToCfnTags(response.attachment().tags()))
                    .transportAttachmentId(response.transportAttachmentId())
                    .options(Utils.sdkOptionsToCfnOptions(response.options()))
                    .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(GetConnectAttachmentRequest awsRequest,
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

    private ProgressEvent<ResourceModel, CallbackContext> done(GetConnectAttachmentResponse response) {
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
