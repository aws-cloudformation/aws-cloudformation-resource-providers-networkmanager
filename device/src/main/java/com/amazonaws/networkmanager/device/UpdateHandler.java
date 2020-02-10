package com.amazonaws.networkmanager.device;

import com.google.common.collect.Sets;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import software.amazon.awssdk.services.networkmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.UpdateDeviceRequest;
import software.amazon.awssdk.services.networkmanager.model.UpdateDeviceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;
import static software.amazon.cloudformation.proxy.OperationStatus.FAILED;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model;
        if (callbackContext != null && callbackContext.isUpdateFailed()) {
            // CallBack initiated: previous update failed, reverting to the previous resource state
            model = request.getPreviousResourceState();
        } else {
            // Initiate the request for Update
            model = request.getDesiredResourceState();
        }
        final NetworkManagerClient client = ClientBuilder.getClient();
        final UpdateDeviceResponse updateDeviceResponse;

        try {
            // Update device
            updateDeviceResponse = updateDevice(client, model, proxy);
            // Update Tags
            updateTags(client, updateDeviceResponse.device().deviceArn(), proxy, model);

        } catch (final Exception e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(FAILED)
                    .errorCode(ExceptionMapper.mapToHandlerErrorCode(e))
                    .message(e.getMessage())
                    // For failure update: adding CallBackContext to revert to the last version
                    .callbackContext(callbackContext == null ? CallbackContext.builder().updateFailed(true).build() : null)
                    .build();
        }

        logger.log(String.format("%s [%s] update succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();
    }

    private UpdateDeviceResponse updateDevice(final NetworkManagerClient client,
                                              final ResourceModel model,
                                              final AmazonWebServicesClientProxy proxy) {
        final UpdateDeviceRequest updateDeviceRequest =
                UpdateDeviceRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .deviceId(model.getDeviceId())
                        .description(model.getDescription())
                        .location(Utils.transformLocation(model.getLocation()))
                        .model(model.getModel())
                        .serialNumber(model.getSerialNumber())
                        .siteId(model.getSiteId())
                        .vendor(model.getVendor())
                        .type(model.getType())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(updateDeviceRequest, client::updateDevice);
    }

    private void updateTags(final NetworkManagerClient client,
                            final String arn,
                            final AmazonWebServicesClientProxy proxy,
                            final ResourceModel model) {
        // Add tag
        if (model.getTags() != null && !model.getTags().isEmpty()) {
            final TagResourceRequest tagResourceRequest =
                    TagResourceRequest.builder()
                            .resourceArn(arn)
                            .tags(Utils.cfnTagsToSdkTags(model.getTags()))
                            .build();
            proxy.injectCredentialsAndInvokeV2(tagResourceRequest, client::tagResource);
        }

        // Get current tags
        final ListTagsForResourceRequest listTagsForResource =
                ListTagsForResourceRequest.builder()
                        .resourceArn(arn)
                        .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = proxy.injectCredentialsAndInvokeV2(listTagsForResource, client::listTagsForResource);
        final Set<software.amazon.awssdk.services.networkmanager.model.Tag> currentTags = new HashSet<>(listTagsForResourceResponse.tagList());
        final Set<software.amazon.awssdk.services.networkmanager.model.Tag> desiredTags = new HashSet<>(Utils.cfnTagsToSdkTags(model.getTags()));

        // Remove tag
        final Set<software.amazon.awssdk.services.networkmanager.model.Tag> tagsToRemove = Sets.difference(currentTags, desiredTags);
        if (tagsToRemove.isEmpty()) {
            return;
        }
        final Set<String> keysToRemove = tagsToRemove.stream().map(Tag::key).collect(Collectors.toSet());
        final UntagResourceRequest untagResourceRequest =
                UntagResourceRequest.builder()
                        .resourceArn(arn)
                        .tagKeys(keysToRemove)
                        .build();
        proxy.injectCredentialsAndInvokeV2(untagResourceRequest, client::untagResource);
    }
}
