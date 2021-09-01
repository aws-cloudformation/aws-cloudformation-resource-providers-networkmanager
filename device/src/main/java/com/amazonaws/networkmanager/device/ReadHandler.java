package com.amazonaws.networkmanager.device;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Device;
import software.amazon.awssdk.services.networkmanager.model.GetDevicesRequest;
import software.amazon.awssdk.services.networkmanager.model.GetDevicesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;


import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;

public class ReadHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final ResourceModel readResult;

        try {
            // Call network manager api getDevices
            final GetDevicesResponse getDevicesResponse = getDevices(client, model, proxy);
            final Device device = getDevicesResponse.devices().get(0);

            // Convert network manager Device to cloudformation resource model
            readResult = Utils.transformDevice(device);
        } catch (final IndexOutOfBoundsException e) {
            return ProgressEvent.failed(null, null, HandlerErrorCode.NotFound, null);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] read succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(readResult)
                .status(SUCCESS)
                .build();
    }

    private GetDevicesResponse getDevices(final NetworkManagerClient client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {
        final GetDevicesRequest getDevicesRequest = GetDevicesRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .deviceIds(model.getDeviceId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getDevicesRequest, client::getDevices);
    }
}
