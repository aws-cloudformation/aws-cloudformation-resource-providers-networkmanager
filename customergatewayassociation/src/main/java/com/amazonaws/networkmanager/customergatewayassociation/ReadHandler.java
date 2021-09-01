package com.amazonaws.networkmanager.customergatewayassociation;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetCustomerGatewayAssociationsResponse;
import software.amazon.awssdk.services.networkmanager.model.CustomerGatewayAssociation;
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
            // Call NetworkManager API getCustomerGatewayAssociations
            final GetCustomerGatewayAssociationsResponse getCustomerGatewayAssociationsResponse = Utils.getCustomerGatewayAssociations(client, model, proxy);

            final CustomerGatewayAssociation customerGatewayAssociation = getCustomerGatewayAssociationsResponse.customerGatewayAssociations().get(0);

            // Convert NetworkManager CustomerGatewayAssociation to Cloudformation resource model
            readResult = Utils.transformCustomerGatewayAssociation(customerGatewayAssociation);
        } catch (IndexOutOfBoundsException e) {
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
}
