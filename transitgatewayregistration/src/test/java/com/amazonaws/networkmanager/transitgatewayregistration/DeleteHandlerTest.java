package com.amazonaws.networkmanager.transitgatewayregistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.DeregisterTransitGatewayRequest;
import software.amazon.awssdk.services.networkmanager.model.DeregisterTransitGatewayResponse;
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
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final DeregisterTransitGatewayResponse deregisterTransitGatewayResponse = DeregisterTransitGatewayResponse.builder()
                .transitGatewayRegistration(buildTransitGatewayRegistration())
                .build();
        doReturn(deregisterTransitGatewayResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeregisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        final ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeregisterTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
