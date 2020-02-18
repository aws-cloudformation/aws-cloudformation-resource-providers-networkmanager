package com.amazonaws.networkmanager.linkassociation;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsResponse;
import software.amazon.awssdk.services.networkmanager.model.LinkAssociation;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
            // Call NetworkManager API getLinkAssociations
            final GetLinkAssociationsResponse getLinkAssociationsResponse = getLinkAssociations(client, model, proxy);

            // Cloudformation requires a NotFound error code if the resource never existed or was deleted
            if (Boolean.FALSE == getLinkAssociationsResponse.hasLinkAssociations()) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString());
            }
            final LinkAssociation linkAssociation = getLinkAssociationsResponse.linkAssociations().get(0);

            // Convert NetworkManager LinkAssociation to Cloudformation resource model
            readResult = Utils.transformLinkAssociation(linkAssociation);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] read succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(readResult)
                .status(SUCCESS)
                .build();
    }

    private GetLinkAssociationsResponse getLinkAssociations(final NetworkManagerClient client,
                                                            final ResourceModel model,
                                                            final AmazonWebServicesClientProxy proxy) {
        final GetLinkAssociationsRequest getLinkAssociationsRequest = GetLinkAssociationsRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .linkId(model.getLinkId())
                .deviceId(model.getDeviceId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getLinkAssociationsRequest, client::getLinkAssociations);
    }
}
