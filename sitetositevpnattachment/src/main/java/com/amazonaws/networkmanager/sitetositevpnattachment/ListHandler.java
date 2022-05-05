package com.amazonaws.networkmanager.sitetositevpnattachment;

import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.list.List;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.proxy.*;

public class ListHandler extends BaseHandlerStd {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<NetworkManagerClient> proxyClient,
            final Logger logger) {
        logger.log("Invoking LIST HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new List(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
