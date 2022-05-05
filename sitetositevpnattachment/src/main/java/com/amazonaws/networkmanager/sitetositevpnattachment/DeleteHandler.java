package com.amazonaws.networkmanager.sitetositevpnattachment;

import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.delete.Delete;
import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.delete.ValidCurrentStateCheck;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<NetworkManagerClient> proxyClient,
            final Logger logger) {
        logger.log("Invoking DELETE HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new ValidCurrentStateCheck(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new Delete(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(_progress -> ProgressEvent.defaultSuccessHandler(null)); //After delete successfully completes we want to return a null response because the model no longer exits
    }
}
