package com.amazonaws.networkmanager.link;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeleteLinkRequest;
import software.amazon.awssdk.services.networkmanager.model.DeleteLinkResponse;
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

        // Delete the link
        try {
            deleteLink(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(SUCCESS)
                .build();
    }

    private DeleteLinkResponse deleteLink(final NetworkManagerClient client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {
        final DeleteLinkRequest deleteLinkResponse =
                DeleteLinkRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .linkId(model.getLinkId())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(deleteLinkResponse, client::deleteLink);
    }
}
