package com.amazonaws.networkmanager.globalnetwork;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DescribeGlobalNetworksRequest;
import software.amazon.awssdk.services.networkmanager.model.DescribeGlobalNetworksResponse;
import software.amazon.awssdk.services.networkmanager.model.GlobalNetwork;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // intitate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final ResourceModel readResult;

        // describe global network
        try {
            readResult = describeGlobalNetwork(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] read succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(readResult)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ResourceModel describeGlobalNetwork(final NetworkManagerClient client,
                                                final ResourceModel model,
                                                final AmazonWebServicesClientProxy proxy) {
        final DescribeGlobalNetworksRequest describeGlobalNetworksRequest = DescribeGlobalNetworksRequest.builder()
                .globalNetworkIds(model.getId())
                .build();
        final DescribeGlobalNetworksResponse describeGlobalNetworksResponse = proxy.injectCredentialsAndInvokeV2(describeGlobalNetworksRequest, client::describeGlobalNetworks);
        final GlobalNetwork globalNetwork = describeGlobalNetworksResponse.globalNetworks().get(0);

        return Utils.transformGlobalNetwork(globalNetwork);

    }
}
