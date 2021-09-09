package com.amazonaws.networkmanager.site;

import com.amazonaws.networkmanager.site.CallbackContext;
import com.amazonaws.networkmanager.site.ClientBuilder;
import com.amazonaws.networkmanager.site.ExceptionMapper;
import com.amazonaws.networkmanager.site.Utils;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Site;
import software.amazon.awssdk.services.networkmanager.model.GetSitesRequest;
import software.amazon.awssdk.services.networkmanager.model.GetSitesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
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
        // initiate the request
        String nextToken = request.getNextToken();
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final List<ResourceModel> listResult = new ArrayList<>(); // Should return empty list if no device returned

        try {
            // Call network manager api getDevices
            final GetSitesResponse getSitesResponse = getSites(client, model, nextToken, proxy);
            nextToken = getSitesResponse.nextToken();

            // Convert network manager Device to cloudformation resource model
            for (final Site site: getSitesResponse.sites()) {
                listResult.add(com.amazonaws.networkmanager.site.Utils.transformSite(site));
            }
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] read succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(listResult)
                .status(SUCCESS)
                .nextToken(nextToken)
                .build();
    }

    private GetSitesResponse getSites(final NetworkManagerClient client,
                                     final ResourceModel model,
                                     final String nextToken,
                                     final AmazonWebServicesClientProxy proxy) {
        final GetSitesRequest getSitesRequest = GetSitesRequest.builder()
                .globalNetworkId(model.getGlobalNetworkId())
                .nextToken(nextToken)
                .build();
        return proxy.injectCredentialsAndInvokeV2(getSitesRequest, client::getSites);
    }
}
