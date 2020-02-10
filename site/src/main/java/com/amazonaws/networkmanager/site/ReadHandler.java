package com.amazonaws.networkmanager.site;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Site;
import software.amazon.awssdk.services.networkmanager.model.GetSitesRequest;
import software.amazon.awssdk.services.networkmanager.model.GetSitesResponse;
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
        // initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final ResourceModel readResult;

        try {
            // Call network manager api getSites
            final GetSitesResponse getSitesResponse = getSites(client, model, proxy);
            final Site site = getSitesResponse.sites().get(0);

            // Convert network manager Site to cloudformation resource model
            readResult = Utils.transformSite(site);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] read succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(readResult)
                .status(SUCCESS)
                .build();
    }

    private GetSitesResponse getSites(final NetworkManagerClient client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {
        final GetSitesRequest getSitesRequest = GetSitesRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .siteIds(model.getSiteId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getSitesRequest, client::getSites);
    }
}
