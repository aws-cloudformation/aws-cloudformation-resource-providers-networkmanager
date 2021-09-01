package com.amazonaws.networkmanager.link;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetLinksRequest;
import software.amazon.awssdk.services.networkmanager.model.GetLinksResponse;
import software.amazon.awssdk.services.networkmanager.model.Link;
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
            // Call NetworkManager api getLinks
            final GetLinksResponse getLinksResponse = getLinks(client, model, proxy);
            final Link link = getLinksResponse.links().get(0);

            // Convert NetworkManager Link to Cloudformation resource model
            readResult = Utils.transformLink(link);
        } catch (final IndexOutOfBoundsException e) {
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

    private GetLinksResponse getLinks(final NetworkManagerClient client,
                                      final ResourceModel model,
                                      final AmazonWebServicesClientProxy proxy) {
        final GetLinksRequest getLinksRequest = GetLinksRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .linkIds(model.getLinkId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getLinksRequest, client::getLinks);
    }
}
