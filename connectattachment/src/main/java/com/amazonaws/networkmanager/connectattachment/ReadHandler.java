package com.amazonaws.networkmanager.connectattachment;

import com.amazonaws.networkmanager.connectattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.proxy.*;

public class ReadHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<NetworkManagerClient> proxyClient,
            final Logger logger) {
        logger.log("Invoking READ HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
