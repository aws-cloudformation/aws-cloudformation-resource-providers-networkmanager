package com.amazonaws.networkmanager.globalnetwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.UpdateGlobalNetworkResponse;
import software.amazon.awssdk.services.networkmanager.model.UpdateGlobalNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends TestBase {
    private UpdateHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new UpdateHandler();
        model = buildUpdateResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateGlobalNetworkResponse updateGlobalNetworkResponse = UpdateGlobalNetworkResponse.builder()
                .globalNetwork(buildGlobalNetwork())
                .build();
        final TagResourceResponse tagResourceResponse = TagResourceResponse.builder().build();
        final UntagResourceResponse untagResourceResponse = UntagResourceResponse.builder().build();
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder().build();
        doReturn(updateGlobalNetworkResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(UpdateGlobalNetworkRequest.class), any());
        doReturn(tagResourceResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        doReturn(untagResourceResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        doReturn(listTagsForResourceResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);
        System.out.println(response);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        final UpdateGlobalNetworkResponse updateGlobalNetworkResponse = UpdateGlobalNetworkResponse.builder()
                .globalNetwork(buildGlobalNetwork())
                .build();
        final ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(UpdateGlobalNetworkRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);
        System.out.println(response);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);

    }
}
