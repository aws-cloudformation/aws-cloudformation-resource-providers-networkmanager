package com.amazonaws.networkmanager.transitgatewayregistration;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
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
        // Initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final ResourceModel readResult;
        try {
            final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = Utils.getTransitGatewayRegistrations(client, model, proxy);
            if(getTransitGatewayRegistrationsResponse.transitGatewayRegistrations().isEmpty()) {
                // Cloudformation requires a NotFound error code if the resource never existed or was deleted
                return ProgressEvent.failed(null, null, HandlerErrorCode.NotFound, null);
            }
            readResult = Utils.transformTransitGatewayRegistration(getTransitGatewayRegistrationsResponse.transitGatewayRegistrations().get(0));
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(readResult)
                    .status(SUCCESS)
                    .build();
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }
    }
}
