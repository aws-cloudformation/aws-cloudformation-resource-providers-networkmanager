package com.amazonaws.networkmanager.sitetositevpnattachment.workflow.create;

import com.amazonaws.networkmanager.sitetositevpnattachment.CallbackContext;
import com.amazonaws.networkmanager.sitetositevpnattachment.ResourceModel;
import com.amazonaws.networkmanager.sitetositevpnattachment.Tag;
import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.Utils;
import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.CreateSiteToSiteVpnAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.CreateSiteToSiteVpnAttachmentResponse;
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

    private CreateSiteToSiteVpnAttachmentRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = (model.getTags() != null) ? Utils.mergeTags(new ArrayList<>(model.getTags()), this.request.getDesiredResourceTags())
                : new ArrayList<>();
        CreateSiteToSiteVpnAttachmentRequest.Builder requestBuilder = CreateSiteToSiteVpnAttachmentRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .vpnConnectionArn(model.getVpnConnectionArn())
                .tags(Utils.cfnTagsToSdkTags(tags));

        return requestBuilder.build();
    }

    private CreateSiteToSiteVpnAttachmentResponse makeServiceCall(CreateSiteToSiteVpnAttachmentRequest awsRequest,
                                                                  ProxyClient<NetworkManagerClient> client) {
        logger.log("create request " + awsRequest);
        return proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::createSiteToSiteVpnAttachment);
    }

    private boolean stabilize(
            CreateSiteToSiteVpnAttachmentRequest awsRequest,
            CreateSiteToSiteVpnAttachmentResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        model.setAttachmentId(awsResponse.siteToSiteVpnAttachment().attachment().attachmentId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model).getState();
        logger.log("current state is " + currentState);
        if (AttachmentState.FAILED.toString().equals(currentState)) {
            throw new RuntimeException("Fail to create Resource: " + model.getAttachmentId());
        }
        return AttachmentState.AVAILABLE.toString().equals(currentState)
                || AttachmentState.PENDING_ATTACHMENT_ACCEPTANCE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateSiteToSiteVpnAttachmentRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
