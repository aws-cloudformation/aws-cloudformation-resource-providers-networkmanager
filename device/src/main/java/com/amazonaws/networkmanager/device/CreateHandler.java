package com.amazonaws.networkmanager.device;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CreateDeviceRequest;
import software.amazon.awssdk.services.networkmanager.model.CreateDeviceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        final CreateDeviceResponse createDeviceResponse;

        // Call network manager api to create device
        try {
            createDeviceResponse = createDevice(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        // Configure the cloudformation resource model
        model.setDeviceArn(createDeviceResponse.device().deviceArn());
        model.setDeviceId(createDeviceResponse.device().deviceId());

        logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();
    }

    private CreateDeviceResponse createDevice(final NetworkManagerClient client,
                                              final ResourceModel model,
                                              final AmazonWebServicesClientProxy proxy) {
        final CreateDeviceRequest createDeviceRequest =
                CreateDeviceRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .description(model.getDescription())
                        .tags(Utils.cfnTagsToSdkTags(model.getTags()))
                        .location(Utils.transformLocation(model.getLocation()))
                        .model(model.getModel())
                        .serialNumber(model.getSerialNumber())
                        .siteId(model.getSiteId())
                        .vendor(model.getVendor())
                        .type(model.getType())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(createDeviceRequest, client::createDevice);
    }
}
