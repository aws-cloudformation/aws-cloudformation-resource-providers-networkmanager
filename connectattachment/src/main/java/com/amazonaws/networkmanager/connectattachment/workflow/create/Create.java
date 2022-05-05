package com.amazonaws.networkmanager.connectattachment.workflow.create;

import com.amazonaws.networkmanager.connectattachment.CallbackContext;
import com.amazonaws.networkmanager.connectattachment.ResourceModel;
import com.amazonaws.networkmanager.connectattachment.Tag;
import com.amazonaws.networkmanager.connectattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.connectattachment.workflow.Utils;
import com.amazonaws.networkmanager.connectattachment.workflow.read.Read;
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

    private CreateConnectAttachmentRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = (model.getTags() != null) ? Utils.mergeTags(new ArrayList<>(model.getTags()), this.request.getDesiredResourceTags())
                : new ArrayList<>();
        CreateConnectAttachmentRequest.Builder requestBuilder = CreateConnectAttachmentRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .transportAttachmentId(model.getTransportAttachmentId())
                .edgeLocation(model.getEdgeLocation())
                .options(Utils.cfnOptionsToSdkOptions(model.getOptions()))
                .tags(Utils.cfnTagsToSdkTags(tags));

        return requestBuilder.build();
    }

    private CreateConnectAttachmentResponse makeServiceCall(CreateConnectAttachmentRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::createConnectAttachment);
    }

    private boolean stabilize(
            CreateConnectAttachmentRequest awsRequest,
            CreateConnectAttachmentResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        model.setAttachmentId(awsResponse.connectAttachment().attachment().attachmentId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        if (AttachmentState.FAILED.toString().equals(currentState)) {
            throw new RuntimeException("Fail to create Resource: " + model.getAttachmentId());
        }
        return AttachmentState.PENDING_ATTACHMENT_ACCEPTANCE.toString().equals(currentState) || AttachmentState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateConnectAttachmentRequest awsRequest, Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
