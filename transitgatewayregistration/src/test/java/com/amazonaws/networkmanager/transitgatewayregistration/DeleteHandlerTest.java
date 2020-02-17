package com.amazonaws.networkmanager.transitgatewayregistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.DeregisterTransitGatewayRequest;
import software.amazon.awssdk.services.networkmanager.model.DeregisterTransitGatewayResponse;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsResponse;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.TIMED_OUT_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static software.amazon.cloudformation.proxy.HandlerErrorCode.InternalFailure;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends TestBase {
    private DeleteHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new DeleteHandler();
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_DeletionInitiated() {
        final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = GetTransitGatewayRegistrationsResponse.builder()
                .transitGatewayRegistrations(buildAvailableTransitGatewayRegistration()) // Before deletion, the state should be available
                .build();
        doReturn(getTransitGatewayRegistrationsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetTransitGatewayRegistrationsRequest.class), any());
        final DeregisterTransitGatewayResponse deregisterTransitGatewayResponse = DeregisterTransitGatewayResponse.builder()
                .transitGatewayRegistration(buildDeletingTransitGatewayRegistration()) // After deletion initiated, the state should be deleting
                .build();
        doReturn(deregisterTransitGatewayResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeregisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }


    @Test
    public void handleRequest_DeletionFinalSucceed() {
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
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }


    @Test
    public void handleRequest_DeletionInProgress() {
        final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = GetTransitGatewayRegistrationsResponse.builder()
                .transitGatewayRegistrations(buildDeletingTransitGatewayRegistration()) // When the deletion is in progress, the state should be deleting
                .build();
        doReturn(getTransitGatewayRegistrationsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetTransitGatewayRegistrationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }

    @Test
    public void handleRequest_DeletionSuccessForFailedState() {
        // if TGW Registration is in FAILED/DELETED state, handler returns a SUCCESS status because FAILED/DELETED is a terminated state
        final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = GetTransitGatewayRegistrationsResponse.builder()
                .transitGatewayRegistrations(buildFailedTransitGatewayRegistration())
                .build();
        doReturn(getTransitGatewayRegistrationsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetTransitGatewayRegistrationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_ResourceNotFound_FirstTimeInvoke() {
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
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
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
}
