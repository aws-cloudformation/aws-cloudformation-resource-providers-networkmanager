package com.amazonaws.networkmanager.device;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeleteDeviceRequest;
import software.amazon.awssdk.services.networkmanager.model.DeleteDeviceResponse;
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

        // Delete the device
        try {
            deleteDevice(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(SUCCESS)
                .build();
    }

    private DeleteDeviceResponse deleteDevice(final NetworkManagerClient client,
                                              final ResourceModel model,
                                              final AmazonWebServicesClientProxy proxy) {
        final DeleteDeviceRequest deleteDeviceResponse =
                DeleteDeviceRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .deviceId(model.getDeviceId())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(deleteDeviceResponse, client::deleteDevice);
    }
}
