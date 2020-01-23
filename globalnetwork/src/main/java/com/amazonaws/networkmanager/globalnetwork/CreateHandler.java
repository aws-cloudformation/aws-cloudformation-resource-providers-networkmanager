package com.amazonaws.networkmanager.globalnetwork;


import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CreateGlobalNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.CreateGlobalNetworkResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.OperationStatus;


public class CreateHandler extends BaseHandler<CallbackContext> {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // intitate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final CreateGlobalNetworkResponse createGlobalNetworkResponse;

        //create global network
        try {
            createGlobalNetworkResponse = createGlobalNetwork(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        // configure the response CFN resource model; Future enhancement: add State if needed
        model.setArn(createGlobalNetworkResponse.globalNetwork().globalNetworkArn());
        model.setId(createGlobalNetworkResponse.globalNetwork().globalNetworkId());

        logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private CreateGlobalNetworkResponse createGlobalNetwork(final NetworkManagerClient client,
                                                            final ResourceModel model,
                                                            final AmazonWebServicesClientProxy proxy) {
        final CreateGlobalNetworkRequest createGlobalNetworkRequest =
                CreateGlobalNetworkRequest.builder()
                        .description(model.getDescription())
                        .tags(Utils.tagTransform(model.getTags()))
                        .build();
        return proxy.injectCredentialsAndInvokeV2(createGlobalNetworkRequest, client::createGlobalNetwork);
    }
}
