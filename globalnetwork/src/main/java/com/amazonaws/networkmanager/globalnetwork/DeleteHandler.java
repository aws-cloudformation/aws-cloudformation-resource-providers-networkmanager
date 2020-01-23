package com.amazonaws.networkmanager.globalnetwork;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeleteGlobalNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.DeleteGlobalNetworkResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        final DeleteGlobalNetworkResponse deleteGlobalNetworkResponse;
        // Delete the globalNetwork
        try {
            deleteGlobalNetworkResponse = deleteGlobalNetwork(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private DeleteGlobalNetworkResponse deleteGlobalNetwork(final NetworkManagerClient client,
                                                            final ResourceModel model,
                                                            final AmazonWebServicesClientProxy proxy) {
        final DeleteGlobalNetworkRequest deleteGlobalNetworkRequest =
                DeleteGlobalNetworkRequest.builder()
                        .globalNetworkId(model.getId())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(deleteGlobalNetworkRequest, client::deleteGlobalNetwork);
    }
}
