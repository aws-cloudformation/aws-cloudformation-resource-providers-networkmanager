package com.amazonaws.networkmanager.connectpeer.workflow.read;

import com.amazonaws.networkmanager.connectpeer.CallbackContext;
import com.amazonaws.networkmanager.connectpeer.ResourceModel;
import com.amazonaws.networkmanager.connectpeer.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.connectpeer.workflow.Utils;
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
        GetConnectPeerRequest request = this.translateModelToRequest(model);
        GetConnectPeerResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getConnectPeer);
        return this.translateResponsesToModel(response, model);
    }

    private GetConnectPeerRequest translateModelToRequest(ResourceModel model) {
        return GetConnectPeerRequest.builder()
                .connectPeerId(model.getConnectPeerId())
                .build();
    }

    private GetConnectPeerResponse makeServiceCall(GetConnectPeerRequest awsRequest, ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getConnectPeer);
    }

    private ResourceModel translateResponsesToModel(GetConnectPeerResponse awsResponse, ResourceModel model) {
        if(awsResponse.connectPeer() == null) {
            return null;
        } else {
            ConnectPeer response = awsResponse.connectPeer();
            return ResourceModel.builder()
                    .connectPeerId(response.connectPeerId())
                    .connectAttachmentId(response.connectAttachmentId())
                    .state(response.stateAsString())
                    .coreNetworkId(response.coreNetworkId())
                    .createdAt(response.createdAt().toString())
                    .edgeLocation(response.edgeLocation())
                    .configuration(Utils.sdkConfigurationToCfnConfiguration(response.configuration()))
                    .tags(Utils.sdkTagsToCfnTags(response.tags()))
                    .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(GetConnectPeerRequest awsRequest,
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

    private ProgressEvent<ResourceModel, CallbackContext> done(GetConnectPeerResponse response) {
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
