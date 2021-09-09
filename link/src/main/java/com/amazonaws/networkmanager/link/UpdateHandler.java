package com.amazonaws.networkmanager.link;

import com.google.common.collect.Sets;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.awssdk.services.networkmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.UpdateLinkRequest;
import software.amazon.awssdk.services.networkmanager.model.UpdateLinkResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static software.amazon.cloudformation.proxy.OperationStatus.FAILED;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;

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
        final UpdateLinkResponse updateLinkResponse;
        final Map<String, String> desiredResourceTags = request.getDesiredResourceTags();

        try {
            // Update link
            updateLinkResponse = updateLink(client, model, proxy);
            // Update Tags
            updateTags(client, updateLinkResponse.link().linkArn(), proxy, model, desiredResourceTags);

        } catch (final Exception e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(FAILED)
                    .errorCode(ExceptionMapper.mapToHandlerErrorCode(e))
                    .message(e.getMessage())
                    // For failure update: adding CallBackContext to revert to the previous version
                    .callbackContext(callbackContext == null ? CallbackContext.builder().updateFailed(true).build() : null)
                    .build();
        }

        logger.log(String.format("%s [%s] update succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();
    }

    private UpdateLinkResponse updateLink(final NetworkManagerClient client,
                                              final ResourceModel model,
                                              final AmazonWebServicesClientProxy proxy) {
        final UpdateLinkRequest updateLinkRequest =
                UpdateLinkRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .linkId(model.getLinkId())
                        .description(model.getDescription())
                        .bandwidth(Utils.transformBandwidth(model.getBandwidth()))
                        .provider(model.getProvider())
                        .type(model.getType())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(updateLinkRequest, client::updateLink);
    }

    private void updateTags(final NetworkManagerClient client,
                            final String arn,
                            final AmazonWebServicesClientProxy proxy,
                            final ResourceModel model,
                            final Map<String, String> desiredResourceTags) {
        // Add tag
        if (model.getTags() != null && !model.getTags().isEmpty()) {
            final TagResourceRequest tagResourceRequest =
                    TagResourceRequest.builder()
                            .resourceArn(arn)
                            .tags(Utils.cfnTagsToSdkTags(Utils.mergeTags(model.getTags(), desiredResourceTags)))
                            .build();
            proxy.injectCredentialsAndInvokeV2(tagResourceRequest, client::tagResource);
        }

        // Get current tags
        final ListTagsForResourceRequest listTagsForResource =
                ListTagsForResourceRequest.builder()
                        .resourceArn(arn)
                        .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = proxy.injectCredentialsAndInvokeV2(listTagsForResource, client::listTagsForResource);
        final Set<Tag> currentTags = new HashSet<>(listTagsForResourceResponse.tagList());
        final Set<Tag> desiredTags = new HashSet<>(Utils.cfnTagsToSdkTags(Utils.mergeTags(model.getTags(), desiredResourceTags)));

        // Remove tag
        final Set<Tag> tagsToRemove = Sets.difference(currentTags, desiredTags);
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
