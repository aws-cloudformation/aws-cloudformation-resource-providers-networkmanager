package com.amazonaws.networkmanager.linkassociation;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DisassociateLinkRequest;
import software.amazon.awssdk.services.networkmanager.model.DisassociateLinkResponse;
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

        // Call NetworkManager api to disassociate the link from the device
        try {
            disassociateLink(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(SUCCESS)
                .build();
    }

    private DisassociateLinkResponse disassociateLink(final NetworkManagerClient client,
                                                      final ResourceModel model,
                                                      final AmazonWebServicesClientProxy proxy) {
        final DisassociateLinkRequest disassociateLinkRequest =
                DisassociateLinkRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .linkId(model.getLinkId())
                        .deviceId(model.getDeviceId())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(disassociateLinkRequest, client::disassociateLink);
    }
}
