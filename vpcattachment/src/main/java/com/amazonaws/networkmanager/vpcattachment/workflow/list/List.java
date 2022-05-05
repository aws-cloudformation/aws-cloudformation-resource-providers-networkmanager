package com.amazonaws.networkmanager.vpcattachment.workflow.list;

import com.amazonaws.networkmanager.vpcattachment.CallbackContext;
import com.amazonaws.networkmanager.vpcattachment.ResourceModel;
import com.amazonaws.networkmanager.vpcattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.vpcattachment.workflow.Utils;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;

    public List(
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
        ListAttachmentsRequest awsRequest = this.translateModelToRequest(progress.getResourceModel());

        try{
            ListAttachmentsResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
            java.util.List<ResourceModel> models = this.translateResponseToModel(awsResponse);
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModels(models)
                    .nextToken(awsResponse.nextToken())
                    .status(OperationStatus.SUCCESS)
                    .build();
        } catch (final Exception e) {
            return this.handleError(awsRequest, e, this.client, this.request.getDesiredResourceState(), this.callbackContext);
        }

    }

    private ListAttachmentsRequest translateModelToRequest(ResourceModel model) {
        return ListAttachmentsRequest.builder()
                .maxResults(50)
                .attachmentType(AttachmentType.VPC)
                .nextToken(this.request.getNextToken()).build();
    }

    private ListAttachmentsResponse makeServiceCall(ListAttachmentsRequest awsRequest,
                                                    ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::listAttachments);
    }

    private java.util.List<ResourceModel> translateResponseToModel(ListAttachmentsResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.attachments())
                .map(attachment -> ResourceModel.builder()
                        .coreNetworkId(attachment.coreNetworkId())
                        .attachmentId(attachment.attachmentId())
                        .state(attachment.stateAsString())
                        .attachmentPolicyRuleNumber(attachment.attachmentPolicyRuleNumber())
                        .attachmentType(attachment.attachmentTypeAsString())
                        .ownerAccountId(attachment.ownerAccountId())
                        .createdAt(attachment.createdAt().toString())
                        .edgeLocation(attachment.edgeLocation())
                        .vpcArn(attachment.resourceArn())
                        .segmentName(attachment.segmentName())
                        .updatedAt(attachment.updatedAt().toString())
                        .tags(Utils.sdkTagsToCfnTags(new ArrayList<>(attachment.tags())))
                        .proposedSegmentChange(Utils.sdkSegmentChangeToCfnSegmentChange(attachment.proposedSegmentChange()))
                        .options(null)
                        .subnetArns(null)
                        .build())
                .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(ListAttachmentsRequest awsRequest,
                                                                      Exception exception,
                                                                      ProxyClient<NetworkManagerClient> client,
                                                                      ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
