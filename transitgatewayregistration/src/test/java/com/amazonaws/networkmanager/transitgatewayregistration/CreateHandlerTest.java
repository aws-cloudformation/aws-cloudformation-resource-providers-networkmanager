package com.amazonaws.networkmanager.transitgatewayregistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsResponse;
import software.amazon.awssdk.services.networkmanager.model.RegisterTransitGatewayRequest;
import software.amazon.awssdk.services.networkmanager.model.RegisterTransitGatewayResponse;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsRequest;
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

import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.TIMED_OUT_MESSAGE;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.MAX_CALLBACK_COUNT;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.FAILED_STATE_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistrationState.FAILED;
import static software.amazon.cloudformation.proxy.HandlerErrorCode.GeneralServiceException;
import static software.amazon.cloudformation.proxy.HandlerErrorCode.InternalFailure;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends TestBase {

    private CreateHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        model = buildResourceModel();
        handler = new CreateHandler();
    }

    @Test
    public void handleRequest_CreationInitiated() {
        final RegisterTransitGatewayResponse transitGatewayRegistrationResponse = RegisterTransitGatewayResponse.builder()
                .transitGatewayRegistration(buildPendingTransitGatewayRegistration()) // a Pending state should be returned for initiation
                .build();
        doReturn(transitGatewayRegistrationResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(RegisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }

    @Test
    public void handleRequest_CreationInProgress() {
        final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = GetTransitGatewayRegistrationsResponse.builder()
                .transitGatewayRegistrations(buildPendingTransitGatewayRegistration()) // a Pending state should be returned for creation still in progress
                .build();
        doReturn(getTransitGatewayRegistrationsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetTransitGatewayRegistrationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(MAX_CALLBACK_COUNT).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }

    @Test
    public void handleRequest_CreationFinalSucceed() {
        final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = GetTransitGatewayRegistrationsResponse.builder()
                .transitGatewayRegistrations(buildAvailableTransitGatewayRegistration()) // an Available state should be returned for the final success
                .build();
        doReturn(getTransitGatewayRegistrationsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetTransitGatewayRegistrationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(MAX_CALLBACK_COUNT).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_CreationCallBackFailed() {
        final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = GetTransitGatewayRegistrationsResponse.builder()
                .transitGatewayRegistrations(buildFailedTransitGatewayRegistration()) // a Failed state should be returned during failed TGW registration process
                .build();
        doReturn(getTransitGatewayRegistrationsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetTransitGatewayRegistrationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(MAX_CALLBACK_COUNT).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).isEqualTo(String.format(GeneralServiceException.getMessage(), String.format(FAILED_STATE_MESSAGE, FAILED, FAILED_MESSAGE)));
        assertThat(response.getErrorCode()).isEqualTo(GeneralServiceException);
    }

    @Test
    public void handleRequest_CreationCallBackExceededMaximumCount() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        // 0 remaining retry count should cause a time out internal failure
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(0).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).isEqualTo(TIMED_OUT_MESSAGE);
        assertThat(response.getErrorCode()).isEqualTo(InternalFailure);
    }

    @Test
    public void handleRequest_ThrottleFailure() {
        final ThrottlingException exception = ThrottlingException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(RegisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.Throttling);
    }

    @Test
    public void handleRequest_AccessDenied() {
        final AccessDeniedException exception = AccessDeniedException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(RegisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.AccessDenied);
    }

    @Test
    public void handleRequest_ResourceConflict() {
        final ConflictException exception = ConflictException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(RegisterTransitGatewayRequest.class), any());
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
                .injectCredentialsAndInvokeV2(any(RegisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceLimitExceeded);
    }

    @Test
    public void handleRequest_InvalidRequest() {
        final ValidationException exception = ValidationException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(RegisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.AlreadyExists);
    }

    @Test
    public void handleRequest_InternalException() {
        final InternalServerException exception = InternalServerException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(RegisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }

    @Test
    public void handleRequest_ResourceNotFoundException() {
        final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = GetTransitGatewayRegistrationsResponse.builder()
                // empty registration returned
                .build();
        doReturn(getTransitGatewayRegistrationsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetTransitGatewayRegistrationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(MAX_CALLBACK_COUNT).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

}
