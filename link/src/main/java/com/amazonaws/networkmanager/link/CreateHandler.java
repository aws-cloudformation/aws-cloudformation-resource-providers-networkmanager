package com.amazonaws.networkmanager.link;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CreateLinkRequest;
import software.amazon.awssdk.services.networkmanager.model.CreateLinkResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;


public class CreateHandler extends BaseHandler<CallbackContext> {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // Initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        final CreateLinkResponse createLinkResponse;

        // Call NetworkManager api to create link
        try {
            createLinkResponse = createLink(client, model, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        // Configure the Cloudformation resource model
        model.setLinkArn(createLinkResponse.link().linkArn());
        model.setLinkId(createLinkResponse.link().linkId());

        logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();
    }

    private CreateLinkResponse createLink(final NetworkManagerClient client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {
        final CreateLinkRequest createLinkRequest =
                CreateLinkRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .siteId(model.getSiteId())
                        .description(model.getDescription())
                        .tags(Utils.cfnTagsToSdkTags(model.getTags()))
                        .bandwidth(Utils.transformBandwidth(model.getBandwidth()))
                        .type(model.getType())
                        .provider(model.getProvider())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(createLinkRequest, client::createLink);
    }
}
