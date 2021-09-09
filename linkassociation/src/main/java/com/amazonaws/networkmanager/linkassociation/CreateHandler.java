package com.amazonaws.networkmanager.linkassociation;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AssociateLinkRequest;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;

public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // Initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();

        // Check duplicate request
        try {
            final GetLinkAssociationsResponse getLinkAssociationsResponse = Utils.getLinkAssociations(client, model, proxy);

            if (getLinkAssociationsResponse.linkAssociations().size() != 0) {
                return ProgressEvent.failed(null, null, HandlerErrorCode.AlreadyExists, null);
            }

        } catch (Exception e) {
            // no action
        }
        // Call NetworkManager api to associate link
        try {
            associateLink(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();
    }

    private void associateLink(final NetworkManagerClient client,
                               final ResourceModel model,
                               final AmazonWebServicesClientProxy proxy) {
        final AssociateLinkRequest associateLinkRequest =
                AssociateLinkRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .deviceId(model.getDeviceId())
                        .linkId(model.getLinkId())
                        .build();
        proxy.injectCredentialsAndInvokeV2(associateLinkRequest, client::associateLink);
    }
}
