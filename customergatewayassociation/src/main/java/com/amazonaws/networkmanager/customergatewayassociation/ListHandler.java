package com.amazonaws.networkmanager.customergatewayassociation;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetCustomerGatewayAssociationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetCustomerGatewayAssociationsResponse;
import software.amazon.awssdk.services.networkmanager.model.CustomerGatewayAssociation;
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
        String nextToken = request.getNextToken();
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final List<ResourceModel> listResult = new ArrayList<>();

        try {
            // Call NetworkManager API getCustomerGatewayAssociations
            final GetCustomerGatewayAssociationsResponse getCustomerGatewayAssociationsResponse = getCustomerGatewayAssociations(client, model, nextToken, proxy);
            nextToken = getCustomerGatewayAssociationsResponse.nextToken();

            // Convert NetworkManager CustomerGatewayAssociation to Cloudformation resource model
            for (final CustomerGatewayAssociation customerGatewayAssociation: getCustomerGatewayAssociationsResponse.customerGatewayAssociations()) {
                listResult.add(Utils.transformCustomerGatewayAssociation(customerGatewayAssociation));
            }
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] read succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(listResult)
                .nextToken(nextToken)
                .status(SUCCESS)
                .build();
    }


    private GetCustomerGatewayAssociationsResponse getCustomerGatewayAssociations(final NetworkManagerClient client,
                                                                                  final ResourceModel model,
                                                                                  final String nextToken,
                                                                                  final AmazonWebServicesClientProxy proxy) {
        final GetCustomerGatewayAssociationsRequest getCustomerGatewayAssociationsRequest = GetCustomerGatewayAssociationsRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .nextToken(nextToken)
                .build();
        return proxy.injectCredentialsAndInvokeV2(getCustomerGatewayAssociationsRequest, client::getCustomerGatewayAssociations);
    }
}
