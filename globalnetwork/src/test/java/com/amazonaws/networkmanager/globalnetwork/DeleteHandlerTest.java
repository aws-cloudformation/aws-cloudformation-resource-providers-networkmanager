package com.amazonaws.networkmanager.globalnetwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.DeleteGlobalNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.DeleteGlobalNetworkResponse;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends TestBase {
    private DeleteHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new DeleteHandler();
        model = buildResourceModelWithOnlyId();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final DeleteGlobalNetworkResponse deleteGlobalNetworkResponse = DeleteGlobalNetworkResponse.builder()
                .globalNetwork(buildGlobalNetwork())
                .build();
        doReturn(deleteGlobalNetworkResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeleteGlobalNetworkRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        final ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeleteGlobalNetworkRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
