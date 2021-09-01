package com.amazonaws.networkmanager.customergatewayassociation;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DisassociateCustomerGatewayRequest;
import software.amazon.awssdk.services.networkmanager.model.DisassociateCustomerGatewayResponse;
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

        // Call NetworkManager api to disassociate the customerGateway from the device
        try {
            disassociateCustomerGateway(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(SUCCESS)
                .build();
    }

    private DisassociateCustomerGatewayResponse disassociateCustomerGateway(final NetworkManagerClient client,
                                                      final ResourceModel model,
                                                      final AmazonWebServicesClientProxy proxy) {
        final DisassociateCustomerGatewayRequest disassociateCustomerGatewayRequest =
                DisassociateCustomerGatewayRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .customerGatewayArn(model.getCustomerGatewayArn())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(disassociateCustomerGatewayRequest, client::disassociateCustomerGateway);
    }
}
