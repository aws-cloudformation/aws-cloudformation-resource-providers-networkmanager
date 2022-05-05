package com.amazonaws.networkmanager.sitetositevpnattachment.workflow.read;

import com.amazonaws.networkmanager.sitetositevpnattachment.CallbackContext;
import com.amazonaws.networkmanager.sitetositevpnattachment.ResourceModel;
import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.Utils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.*;

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
        GetSiteToSiteVpnAttachmentRequest request = this.translateModelToRequest(model);
        logger.log("read request " + request);
        GetSiteToSiteVpnAttachmentResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getSiteToSiteVpnAttachment);
        logger.log("read response " + response);
        return this.translateResponsesToModel(response, model);
    }

    private GetSiteToSiteVpnAttachmentRequest translateModelToRequest(ResourceModel model) {
        return GetSiteToSiteVpnAttachmentRequest.builder()
                .attachmentId(model.getAttachmentId())
                .build();
    }

    private GetSiteToSiteVpnAttachmentResponse makeServiceCall(GetSiteToSiteVpnAttachmentRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getSiteToSiteVpnAttachment);
    }

    private ResourceModel translateResponsesToModel(GetSiteToSiteVpnAttachmentResponse awsResponse, ResourceModel model) {
        if(awsResponse.siteToSiteVpnAttachment() == null) {
            return null;
        } else {
            SiteToSiteVpnAttachment response = awsResponse.siteToSiteVpnAttachment();
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
                    .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(GetSiteToSiteVpnAttachmentRequest awsRequest,
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

    private ProgressEvent<ResourceModel, CallbackContext> done(GetSiteToSiteVpnAttachmentResponse response) {
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
