package com.amazonaws.networkmanager.linkassociation;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsResponse;
import software.amazon.awssdk.services.networkmanager.model.LinkAssociation;
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
        final List<ResourceModel> listResult = new ArrayList<>(); // Should return empty list if no device returned

        try {
            // Call NetworkManager API getLinkAssociations
            final GetLinkAssociationsResponse getLinkAssociationsResponse = getLinkAssociations(client, model, nextToken, proxy);

            nextToken = getLinkAssociationsResponse.nextToken();

            // Convert network manager Device to cloudformation resource model
            for (final LinkAssociation linkAssociation: getLinkAssociationsResponse.linkAssociations()) {
                listResult.add(Utils.transformLinkAssociation(linkAssociation));
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

    private GetLinkAssociationsResponse getLinkAssociations(final NetworkManagerClient client,
                                                           final ResourceModel model,
                                                           final String nextToken,
                                                           final AmazonWebServicesClientProxy proxy) {
        final GetLinkAssociationsRequest getLinkAssociationsRequest = GetLinkAssociationsRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .nextToken(nextToken)
                .build();
        return proxy.injectCredentialsAndInvokeV2(getLinkAssociationsRequest, client::getLinkAssociations);
    }
}
