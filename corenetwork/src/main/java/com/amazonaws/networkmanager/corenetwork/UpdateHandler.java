package com.amazonaws.networkmanager.corenetwork;

import com.amazonaws.networkmanager.corenetwork.workflow.read.Read;
import com.amazonaws.networkmanager.corenetwork.workflow.update.*;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<NetworkManagerClient> proxyClient,
            final Logger logger) {
        logger.log("Invoking UPDATE HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new ValidPropertiesCheck(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new ValidCurrentStateCheck(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new CreateTags(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new DeleteTags(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new UpdateDescription(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new PutCoreNetworkPolicy(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new ExecuteCoreNetworkChangeSet(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
