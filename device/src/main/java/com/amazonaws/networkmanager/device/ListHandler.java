package com.amazonaws.networkmanager.device;

import com.amazonaws.networkmanager.device.ResourceModel;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import java.util.ArrayList;
import java.util.List;

import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;


public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // initiate the request
        String nextToken = request.getNextToken();
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final List<ResourceModel> listResult = new ArrayList<>(); // Should return empty list if no device returned

        try {
            // Call network manager api getDevices
            final GetDevicesResponse getDevicesResponse = getDevices(client, model, nextToken, proxy);
            nextToken = getDevicesResponse.nextToken();

            // Convert network manager Device to cloudformation resource model
            for (final Device device: getDevicesResponse.devices()) {
                 listResult.add(Utils.transformDevice(device));
            }
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] read succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(listResult)
                .status(SUCCESS)
                .nextToken(nextToken)
                .build();
    }

    private GetDevicesResponse getDevices(final NetworkManagerClient client,
                                          final ResourceModel model,
                                          final String nextToken,
                                          final AmazonWebServicesClientProxy proxy) {
        final GetDevicesRequest getDevicesRequest = GetDevicesRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .nextToken(nextToken)
                .build();
        return proxy.injectCredentialsAndInvokeV2(getDevicesRequest, client::getDevices);
    }
}
