package com.amazonaws.networkmanager.corenetwork.workflow.update;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.*;

public class PutCoreNetworkPolicy {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;

    public PutCoreNetworkPolicy(
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

    private PutCoreNetworkPolicyRequest translateModelToRequest(ResourceModel model) {
        if (model.getPolicyDocument() == null || model.getPolicyDocument().equals(this.request.getPreviousResourceState().getPolicyDocument())) {
            return PutCoreNetworkPolicyRequest.builder().build();
        }
        this.callbackContext.isPolicyUpdated = true;
        return PutCoreNetworkPolicyRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .policyDocument(model.getPolicyDocument())
                .build();
    }

    private PutCoreNetworkPolicyResponse makeServiceCall(PutCoreNetworkPolicyRequest request,
                                                         ProxyClient<NetworkManagerClient> client) {
        if (!this.callbackContext.isPolicyUpdated) {
            return PutCoreNetworkPolicyResponse.builder().build();
        }
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::putCoreNetworkPolicy);
    }

    private boolean stabilize(
            PutCoreNetworkPolicyRequest awsRequest,
            PutCoreNetworkPolicyResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        if (!this.callbackContext.isPolicyUpdated) {
            return true;
        }

        GetCoreNetworkPolicyRequest getCoreNetworkPolicyRequest = GetCoreNetworkPolicyRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .policyVersionId(awsResponse.coreNetworkPolicy().policyVersionId())
                .build();
        GetCoreNetworkPolicyResponse getCoreNetworkPolicyResponse = proxy.injectCredentialsAndInvokeV2(getCoreNetworkPolicyRequest,
                client.client()::getCoreNetworkPolicy);
        String changeSetState = getCoreNetworkPolicyResponse.coreNetworkPolicy().changeSetStateAsString();

        if (ChangeSetState.FAILED_GENERATION.toString().equals(changeSetState)) {
            throw new RuntimeException("Put core network policy failed");
        }

        this.callbackContext.updatePolicyVersionId = getCoreNetworkPolicyResponse.coreNetworkPolicy().policyVersionId();
        return ChangeSetState.READY_TO_EXECUTE.toString().equals(changeSetState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(PutCoreNetworkPolicyRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
