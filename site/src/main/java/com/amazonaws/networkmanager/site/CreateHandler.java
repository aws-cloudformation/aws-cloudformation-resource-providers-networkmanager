package com.amazonaws.networkmanager.site;

import software.amazon.awssdk.services.networkmanager.model.CreateSiteRequest;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CreateSiteResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;

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
        final CreateSiteResponse createSiteResponse;
        final Map<String, String> desiredResourceTags = request.getDesiredResourceTags();

        // Call network manager api to create site
        try {
            createSiteResponse = createSite(client, model, desiredResourceTags, proxy);
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        // Configure the cloudformation resource model
        model.setSiteArn(createSiteResponse.site().siteArn());
        model.setSiteId(createSiteResponse.site().siteId());

        logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();
    }

    private CreateSiteResponse createSite(final NetworkManagerClient client,
                                              final ResourceModel model,
                                              final Map<String, String> desiredResourceTags,
                                              final AmazonWebServicesClientProxy proxy) {
        final CreateSiteRequest createSiteRequest =
                CreateSiteRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .description(model.getDescription())
                        .tags(Utils.cfnTagsToSdkTags(Utils.mergeTags(model.getTags(), desiredResourceTags)))
                        .location(Utils.transformLocation(model.getLocation()))
                        .build();
        return proxy.injectCredentialsAndInvokeV2(createSiteRequest, client::createSite);
    }
}
