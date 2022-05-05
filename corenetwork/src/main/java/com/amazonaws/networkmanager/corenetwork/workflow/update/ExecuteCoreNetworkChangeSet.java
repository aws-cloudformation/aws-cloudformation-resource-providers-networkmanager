package com.amazonaws.networkmanager.corenetwork.workflow.update;

import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.workflow.ExceptionMapper;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.*;

public class ExecuteCoreNetworkChangeSet {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;

    public ExecuteCoreNetworkChangeSet(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<NetworkManagerClient> client,
            Logger logger) {
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

    private ExecuteCoreNetworkChangeSetRequest translateModelToRequest(ResourceModel model) {
        if (!this.callbackContext.isPolicyUpdated) {
            return ExecuteCoreNetworkChangeSetRequest.builder().build();
        }
        return ExecuteCoreNetworkChangeSetRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .policyVersionId(this.callbackContext.updatePolicyVersionId)
                .build();
    }

    private ExecuteCoreNetworkChangeSetResponse makeServiceCall(ExecuteCoreNetworkChangeSetRequest request,
                                                                ProxyClient<NetworkManagerClient> client) {
        if(!this.callbackContext.isPolicyUpdated) {
            return ExecuteCoreNetworkChangeSetResponse.builder().build();
        }
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::executeCoreNetworkChangeSet);
    }

    private boolean stabilize(
            ExecuteCoreNetworkChangeSetRequest awsRequest,
            ExecuteCoreNetworkChangeSetResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        if (!this.callbackContext.isPolicyUpdated) {
            return true;
        }
        GetCoreNetworkPolicyRequest getCoreNetworkPolicyRequest = GetCoreNetworkPolicyRequest.builder()
                .coreNetworkId(model.getCoreNetworkId())
                .policyVersionId(this.callbackContext.updatePolicyVersionId)
                .build();
        GetCoreNetworkPolicyResponse getCoreNetworkPolicyResponse = proxy.injectCredentialsAndInvokeV2(getCoreNetworkPolicyRequest,
                client.client()::getCoreNetworkPolicy);
        String changeSetState = getCoreNetworkPolicyResponse.coreNetworkPolicy().changeSetStateAsString();
        return ChangeSetState.EXECUTION_SUCCEEDED.toString().equals(changeSetState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(ExecuteCoreNetworkChangeSetRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
