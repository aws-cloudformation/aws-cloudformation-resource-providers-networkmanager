package com.amazonaws.networkmanager.site;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.networkmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.UpdateSiteRequest;
import software.amazon.awssdk.services.networkmanager.model.UpdateSiteResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends TestBase {

    private UpdateHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new UpdateHandler();
        model = buildSimpleResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateSiteResponse updateSiteResponse = UpdateSiteResponse.builder()
                .site(buildSimpleSite())
                .build();
        final UntagResourceResponse untagResourceResponse = UntagResourceResponse.builder().build();
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder().tagList(createNetworkManagerTags()).build();
        doReturn(updateSiteResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(UpdateSiteRequest.class), any());
        doReturn(untagResourceResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        doReturn(listTagsForResourceResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        final ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(UpdateSiteRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
        assertThat(response.getCallbackContext().isUpdateFailed()).isEqualTo(true);
    }

    @Test
    public void handleRequest_testUpdateFailureCallBack() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(buildSimpleResourceModel())
                .previousResourceState(buildResourceModel())
                .build();
        final UpdateSiteResponse updateSiteResponse = UpdateSiteResponse.builder()
                .site(buildSite())
                .build();
        final TagResourceResponse tagResourceResponse = TagResourceResponse.builder().build();
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder().tagList(new ArrayList<>()).build();
        doReturn(updateSiteResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(UpdateSiteRequest.class), any());
        doReturn(tagResourceResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        doReturn(listTagsForResourceResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().updateFailed(true).build(), logger);

        assertThat(response.getResourceModel().getTags()).isEqualTo(createCloudformationTags());
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }
}
