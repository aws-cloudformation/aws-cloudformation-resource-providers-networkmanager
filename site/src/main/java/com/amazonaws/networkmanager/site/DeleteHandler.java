package com.amazonaws.networkmanager.site;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeleteSiteRequest;
import software.amazon.awssdk.services.networkmanager.model.DeleteSiteResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;


public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // Initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();

        // Delete the site
        try {
            deleteSite(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();
    }

    private DeleteSiteResponse deleteSite(final NetworkManagerClient client,
                                              final ResourceModel model,
                                              final AmazonWebServicesClientProxy proxy) {
        final DeleteSiteRequest deleteSiteResponse =
                DeleteSiteRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .siteId(model.getSiteId())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(deleteSiteResponse, client::deleteSite);
    }
}
