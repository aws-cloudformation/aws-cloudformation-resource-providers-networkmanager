package com.amazonaws.networkmanager.transitgatewayregistration;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsResponse;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistration;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        // Initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final List<ResourceModel> listResult = new ArrayList<>();
        try {
            final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = getTransitGatewayRegistrations(client, model, proxy);

            for (TransitGatewayRegistration transitGatewayRegistration: getTransitGatewayRegistrationsResponse.transitGatewayRegistrations()) {
                listResult.add(Utils.transformTransitGatewayRegistration(transitGatewayRegistration));
            }
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModels(listResult)
                    .status(SUCCESS)
                    .build();
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }
    }

    private GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrations(final NetworkManagerClient client,
                                                                                 final ResourceModel model,
                                                                                 final AmazonWebServicesClientProxy proxy) {
        final GetTransitGatewayRegistrationsRequest getTransitGatewayRegistrationsRequest =
                GetTransitGatewayRegistrationsRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(getTransitGatewayRegistrationsRequest, client::getTransitGatewayRegistrations);
    }
}
