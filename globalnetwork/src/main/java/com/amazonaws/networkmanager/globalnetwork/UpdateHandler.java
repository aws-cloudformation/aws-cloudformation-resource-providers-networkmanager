package com.amazonaws.networkmanager.globalnetwork;

import com.google.common.collect.Sets;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.awssdk.services.networkmanager.model.UpdateGlobalNetworkResponse;
import software.amazon.awssdk.services.networkmanager.model.UpdateGlobalNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        if(callbackContext == null) {
            // Initiate the request for Update
            model = request.getDesiredResourceState();
        } else {
            // CallBack initiated: Revert to the previous resource state
            model = request.getPreviousResourceState();
        }

        final NetworkManagerClient client = ClientBuilder.getClient();
        final UpdateGlobalNetworkResponse updateGlobalNetworkResponse;

        try {
            // Update description
            updateGlobalNetworkResponse = updateGlobalNetwork(client, model, proxy);
            // Update Tags
            updateTags(client, updateGlobalNetworkResponse.globalNetwork().globalNetworkArn(), proxy, model);
        } catch (final Exception e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .errorCode(ExceptionMapper.mapToHandlerErrorCode(e))
                    .message(e.getMessage())
                    // For failure update: adding CallBackContext to revert to the last version
                    .callbackContext(callbackContext == null ? CallbackContext.builder().build() : null)
                    .build();
        }

        logger.log(String.format("%s [%s] update succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private UpdateGlobalNetworkResponse updateGlobalNetwork(final NetworkManagerClient client,
                                                            final ResourceModel model,
                                                            final AmazonWebServicesClientProxy proxy) {
        final UpdateGlobalNetworkRequest updateGlobalNetworkRequest =
                UpdateGlobalNetworkRequest.builder()
                        .globalNetworkId(model.getId())
                        .description(model.getDescription())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(updateGlobalNetworkRequest, client::updateGlobalNetwork);
    }

    private void updateTags(final NetworkManagerClient client,
                            final String arn,
                            final AmazonWebServicesClientProxy proxy,
                            final ResourceModel model) {
        // Add tag
        final TagResourceRequest tagResourceRequest =
                TagResourceRequest.builder()
                        .resourceArn(arn)
                        .tags(Utils.cfnTagsToSdkTags(model.getTags()))
                        .build();
        proxy.injectCredentialsAndInvokeV2(tagResourceRequest, client::tagResource);

        // Get current tags
        final ListTagsForResourceRequest listTagsForResource =
                ListTagsForResourceRequest.builder()
                        .resourceArn(arn)
                        .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = proxy.injectCredentialsAndInvokeV2(listTagsForResource, client::listTagsForResource);
        final Set<Tag> currentTags = new HashSet<>(listTagsForResourceResponse.tagList());
        final Set<Tag> desiredTags = new HashSet<>(Utils.cfnTagsToSdkTags(model.getTags()));

        // Remove tag
        final Set<Tag> tagsToRemove = Sets.difference(currentTags, desiredTags);
        if(tagsToRemove.isEmpty()) {
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
