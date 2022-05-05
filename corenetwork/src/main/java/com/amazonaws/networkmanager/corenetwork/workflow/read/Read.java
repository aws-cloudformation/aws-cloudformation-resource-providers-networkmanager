package com.amazonaws.networkmanager.corenetwork.workflow.read;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.corenetwork.workflow.Utils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;

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
        GetCoreNetworkRequest request = this.translateModelToRequest(model);
        GetCoreNetworkResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getCoreNetwork);
        return this.translateResponsesToModel(response, model);
    }

    private GetCoreNetworkRequest translateModelToRequest(ResourceModel model) {
        return GetCoreNetworkRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .build();
    }

    private GetCoreNetworkResponse makeServiceCall(GetCoreNetworkRequest awsRequest,
                                                   ProxyClient<NetworkManagerClient> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getCoreNetwork);
    }

    private ResourceModel translateResponsesToModel(GetCoreNetworkResponse awsResponse, ResourceModel model) {
        if(awsResponse.coreNetwork() == null) {
            return null;
        } else {
            String modelPolicyDocument = model.getPolicyDocument() == null? "": model.getPolicyDocument();
            String livePolicyDocument = null;
            String policyDocument;
            // Get the live policy for the core network, use this try catch block because currently if a core network
            // doesn't have LIVE policy, getCoreNetworkPolicy will return validation error with Incorrect error message.
            try {
                GetCoreNetworkPolicyRequest getCoreNetworkPolicyRequest = GetCoreNetworkPolicyRequest.builder()
                        .coreNetworkId(model.getCoreNetworkId())
                        .alias(CoreNetworkPolicyAlias.LIVE.toString())
                        .build();
                GetCoreNetworkPolicyResponse getCoreNetworkPolicyResponse = proxy.injectCredentialsAndInvokeV2(
                        getCoreNetworkPolicyRequest, client.client()::getCoreNetworkPolicy);
                livePolicyDocument = getCoreNetworkPolicyResponse.coreNetworkPolicy().policyDocument();
            } catch (ValidationException e) {
                if (e.getMessage().contains("Incorrect input.")) {
                    logger.log("CoreNetwork doesn't have a policy");
                    livePolicyDocument = "";
                }
            }

            // Determine if the policy document drift
            boolean isPolicyDrift = isPolicyDrift(livePolicyDocument, modelPolicyDocument);
            if (isPolicyDrift) {
                policyDocument = Utils.getCondensedString(livePolicyDocument);
            } else {
                policyDocument = model.getPolicyDocument();
            }

            CoreNetwork response = awsResponse.coreNetwork();
            ResourceModel resourceModel = ResourceModel.builder()
                    .coreNetworkId(response.coreNetworkId())
                    .coreNetworkArn(response.coreNetworkArn())
                    .state(response.stateAsString())
                    .createdAt(response.createdAt().toString())
                    .description(response.description())
                    .policyDocument(policyDocument)
                    .globalNetworkId(response.globalNetworkId())
                    .segments(Utils.sdkSegmentToCfnSegment(response.segments()))
                    .edges(Utils.sdkEdgeToCfnEdge(response.edges()))
                    .ownerAccount(null)
                    .tags(Utils.sdkTagsToCfnTags(new ArrayList<>(response.tags())))
                    .build();
            return resourceModel;
        }
    }

    private boolean isPolicyDrift(String livePolicyDocument, String modelPolicyDocument) {
        String condensedLivePolicy = Utils.getCondensedString(livePolicyDocument);
        String condensedModelPolicy = Utils.getCondensedString(modelPolicyDocument);
        return !condensedLivePolicy.equals(condensedModelPolicy);
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(GetCoreNetworkRequest awsRequest,
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

    private ProgressEvent<ResourceModel, CallbackContext> done(GetCoreNetworkResponse response) {
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
