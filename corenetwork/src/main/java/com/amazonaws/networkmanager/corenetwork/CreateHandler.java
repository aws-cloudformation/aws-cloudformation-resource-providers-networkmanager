package com.amazonaws.networkmanager.corenetwork;

import com.amazonaws.networkmanager.corenetwork.workflow.create.Create;
import com.amazonaws.networkmanager.corenetwork.workflow.read.Read;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandlerStd {
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<NetworkManagerClient> proxyClient,
            final Logger logger) {
        logger.log("Invoking CREATE HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new Create(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
