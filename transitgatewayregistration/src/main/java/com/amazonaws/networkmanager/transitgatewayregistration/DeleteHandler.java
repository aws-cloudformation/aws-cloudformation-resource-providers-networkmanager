package com.amazonaws.networkmanager.transitgatewayregistration;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeregisterTransitGatewayRequest;
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

        // Deregister TransitGateway from the global network
        try {
            deregisterTransitGateway(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(request.getDesiredResourceState())
                .status(SUCCESS)
                .build();
    }

    private void deregisterTransitGateway(final NetworkManagerClient client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {
        final DeregisterTransitGatewayRequest deregisterTransitGatewayRequest =
                DeregisterTransitGatewayRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .transitGatewayArn(model.getTransitGatewayArn())
                        .build();
        proxy.injectCredentialsAndInvokeV2(deregisterTransitGatewayRequest, client::deregisterTransitGateway);
    }
}
