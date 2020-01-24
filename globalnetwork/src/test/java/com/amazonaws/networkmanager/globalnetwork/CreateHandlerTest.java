package com.amazonaws.networkmanager.globalnetwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.CreateGlobalNetworkResponse;
import software.amazon.awssdk.services.networkmanager.model.CreateGlobalNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.AccessDeniedException;
import software.amazon.awssdk.services.networkmanager.model.ConflictException;
import software.amazon.awssdk.services.networkmanager.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.networkmanager.model.ThrottlingException;
import software.amazon.awssdk.services.networkmanager.model.ValidationException;
import software.amazon.awssdk.services.networkmanager.model.InternalServerException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends TestBase {
    private CreateHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new CreateHandler();
        model = buildCreateResourceModel();
    }


    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateGlobalNetworkResponse createGlobalNetworkResponse = CreateGlobalNetworkResponse.builder()
                .globalNetwork(buildGlobalNetwork())
                .build();
        doReturn(createGlobalNetworkResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateGlobalNetworkRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getArn()).isEqualTo(GLOBAL_NETWORK_ARN);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
    }

    @Test
    public void handleRequest_ThrottleFailure() {
        final ThrottlingException exception = ThrottlingException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateGlobalNetworkRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.Throttling);
    }

    @Test
    public void handleRequest_AccessDenied() {
        final AccessDeniedException exception = AccessDeniedException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateGlobalNetworkRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.AccessDenied);
    }

    @Test
    public void handleRequest_ResoureConflict() {
        final ConflictException exception = ConflictException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateGlobalNetworkRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
    }

    @Test
    public void handleRequest_ServiceLimitExceeded() {
        final ServiceQuotaExceededException exception = ServiceQuotaExceededException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateGlobalNetworkRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceLimitExceeded);
    }

    @Test
    public void handleRequest_InvalidRequest() {
        final ValidationException exception = ValidationException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateGlobalNetworkRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequest_InternalException() {
        final InternalServerException exception = InternalServerException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateGlobalNetworkRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }

}
