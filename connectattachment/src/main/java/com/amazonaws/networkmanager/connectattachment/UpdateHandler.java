package com.amazonaws.networkmanager.connectattachment;

import com.amazonaws.networkmanager.connectattachment.workflow.read.Read;
import com.amazonaws.networkmanager.connectattachment.workflow.update.CreateTags;
import com.amazonaws.networkmanager.connectattachment.workflow.update.DeleteTags;
import com.amazonaws.networkmanager.connectattachment.workflow.update.ValidCurrentStateCheck;
import com.amazonaws.networkmanager.connectattachment.workflow.update.ValidPropertiesCheck;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.proxy.*;

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
                .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
