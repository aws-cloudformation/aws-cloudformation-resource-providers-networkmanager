package com.amazonaws.networkmanager.connectattachment;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {
  @Override
  public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
          final AmazonWebServicesClientProxy proxy,
          final ResourceHandlerRequest<ResourceModel> request,
          final CallbackContext callbackContext,
          final Logger logger) {
    CallbackContext context = callbackContext != null ? callbackContext : new CallbackContext();
    context.setAttempts(context.getAttempts() + 1);
    return handleRequest(
            proxy,
            request,
            context,
            proxy.newProxy(ClientBuilder::getClient),
            logger
    );
  }

  protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
          final AmazonWebServicesClientProxy proxy,
          final ResourceHandlerRequest<ResourceModel> request,
          final CallbackContext callbackContext,
          final ProxyClient<NetworkManagerClient> proxyClient,
          final Logger logger);
}
