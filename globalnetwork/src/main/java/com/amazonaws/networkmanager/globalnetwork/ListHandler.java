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

import java.util.ArrayList;
import java.util.List;


public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // Initiate the request
        String nextToken = request.getNextToken();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final List<ResourceModel> listResult = new ArrayList<>(); // Should return empty list if no global network returned

        // List the global networks
        try {
            final DescribeGlobalNetworksResponse describeGlobalNetworksResponse = describeGlobalNetworks(client, nextToken, proxy);
            nextToken = describeGlobalNetworksResponse.nextToken();
            for (final GlobalNetwork globalNetwork : describeGlobalNetworksResponse.globalNetworks()) {
                listResult.add(Utils.transformGlobalNetwork(globalNetwork));
            }
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s list succeeded", ResourceModel.TYPE_NAME));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(listResult)
                .nextToken(nextToken)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private DescribeGlobalNetworksResponse describeGlobalNetworks(final NetworkManagerClient client,
                                                                  final String nextToken,
                                                                  final AmazonWebServicesClientProxy proxy) {
        final List<ResourceModel> readResult = new ArrayList<>();
        final DescribeGlobalNetworksRequest describeGlobalNetworksRequest = DescribeGlobalNetworksRequest.builder()
                .nextToken(nextToken)
                .build();
        return proxy.injectCredentialsAndInvokeV2(describeGlobalNetworksRequest, client::describeGlobalNetworks);
    }
}
