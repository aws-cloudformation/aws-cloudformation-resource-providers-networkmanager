package com.amazonaws.networkmanager.vpcattachment.workflow.read;

import com.amazonaws.networkmanager.vpcattachment.CallbackContext;
import com.amazonaws.networkmanager.vpcattachment.ResourceModel;
import com.amazonaws.networkmanager.vpcattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.vpcattachment.workflow.Utils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Attachment;
import software.amazon.awssdk.services.networkmanager.model.GetVpcAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.GetVpcAttachmentResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.networkmanager.model.VpcAttachment;
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
        GetVpcAttachmentRequest request = this.translateModelToRequest(model);
        GetVpcAttachmentResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getVpcAttachment);
        return this.translateResponsesToModel(response, model);
    }

    private GetVpcAttachmentRequest translateModelToRequest(ResourceModel model) {
        return GetVpcAttachmentRequest.builder()
                .attachmentId(model.getAttachmentId())
                .build();
    }

    private GetVpcAttachmentResponse makeServiceCall(GetVpcAttachmentRequest awsRequest,
                                                     ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getVpcAttachment);
    }

    private ResourceModel translateResponsesToModel(GetVpcAttachmentResponse awsResponse, ResourceModel model) {
        if(awsResponse.vpcAttachment() == null) {
            return null;
        } else {
            VpcAttachment response = awsResponse.vpcAttachment();
            Attachment attachment = response.attachment();
            String segmentName = attachment.segmentName();
            Integer attachmentPolicyRuleNumber = attachment.attachmentPolicyRuleNumber();
            return ResourceModel.builder()
                    .attachmentId(attachment.attachmentId())
                    .attachmentType(attachment.attachmentTypeAsString())
                    .coreNetworkId(attachment.coreNetworkId())
                    .coreNetworkArn(attachment.coreNetworkArn())
                    .ownerAccountId(attachment.ownerAccountId())
                    .attachmentPolicyRuleNumber(attachmentPolicyRuleNumber)
                    .segmentName(segmentName)
                    .options(Utils.sdkOptionsToCfnOptions(response.options()))
                    .proposedSegmentChange(Utils.sdkSegmentChangeToCfnSegmentChange(attachment.proposedSegmentChange()))
                    .edgeLocation(attachment.edgeLocation())
                    .resourceArn(attachment.resourceArn())
                    .createdAt(attachment.createdAt().toString())
                    .updatedAt(attachment.updatedAt().toString())
                    .state(attachment.stateAsString())
                    .tags(Utils.sdkTagsToCfnTags(response.attachment().tags()))
                    .subnetArns(response.subnetArns())
                    .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(GetVpcAttachmentRequest awsRequest,
                                                                      Exception exception,
                                                                      ProxyClient<NetworkManagerClient> client,
                                                                      ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException || exception instanceof ResourceNotFoundException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder()
                    .awsErrorDetails(AwsErrorDetails.builder()
                    .errorCode("NotFound")
                    .errorMessage("Not Found")
                    .build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> done(GetVpcAttachmentResponse response) {
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
