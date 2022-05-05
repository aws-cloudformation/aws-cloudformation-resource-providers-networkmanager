package com.amazonaws.networkmanager.connectpeer.workflow.create;

import com.amazonaws.networkmanager.connectpeer.CallbackContext;
import com.amazonaws.networkmanager.connectpeer.ResourceModel;
import com.amazonaws.networkmanager.connectpeer.Tag;
import com.amazonaws.networkmanager.connectpeer.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.connectpeer.workflow.Utils;
import com.amazonaws.networkmanager.connectpeer.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.ConnectPeerState;
import software.amazon.awssdk.services.networkmanager.model.CreateConnectPeerRequest;
import software.amazon.awssdk.services.networkmanager.model.CreateConnectPeerResponse;
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

    private CreateConnectPeerRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = (model.getTags() != null) ? Utils.mergeTags(new ArrayList<>(model.getTags()), this.request.getDesiredResourceTags())
                : new ArrayList<>();
        CreateConnectPeerRequest.Builder requestBuilder = CreateConnectPeerRequest.builder()
                .connectAttachmentId(model.getConnectAttachmentId())
                .peerAddress(model.getPeerAddress())
                .insideCidrBlocks(model.getInsideCidrBlocks())
                .tags(Utils.cfnTagsToSdkTags(tags));

        if(model.getBgpOptions() != null) {
            requestBuilder.bgpOptions(Utils.cfnBgpOptionsToSdkBgpOptions(model.getBgpOptions()));
        }
        if(model.getCoreNetworkAddress() != null && !model.getCoreNetworkAddress().isEmpty()) {
            requestBuilder.coreNetworkAddress(model.getCoreNetworkAddress());
        }

        return requestBuilder.build();
    }

    private CreateConnectPeerResponse makeServiceCall(CreateConnectPeerRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        logger.log("awsRequest  " + awsRequest.toString());
        return proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::createConnectPeer);
    }

    private boolean stabilize(
            CreateConnectPeerRequest awsRequest,
            CreateConnectPeerResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        logger.log("awsResponse  " + awsResponse.toString());
        model.setConnectPeerId(awsResponse.connectPeer().connectPeerId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        if (ConnectPeerState.FAILED.toString().equals(currentState)) {
            throw new RuntimeException("Fail to create Resource: " + model.getConnectPeerId());
        }
        return ConnectPeerState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateConnectPeerRequest awsRequest, Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
