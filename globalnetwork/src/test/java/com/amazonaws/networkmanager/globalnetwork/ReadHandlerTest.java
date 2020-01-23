package com.amazonaws.networkmanager.globalnetwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.DescribeGlobalNetworksRequest;
import software.amazon.awssdk.services.networkmanager.model.DescribeGlobalNetworksResponse;
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
public class ReadHandlerTest extends TestBase {

    private ReadHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new ReadHandler();
        model = buildDeleteOrReadResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final DescribeGlobalNetworksResponse describeGlobalNetworksResponse = DescribeGlobalNetworksResponse.builder()
                .globalNetworks(buildGlobalNetwork())
                .build();
        doReturn(describeGlobalNetworksResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeGlobalNetworksRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getArn()).isEqualTo(GLOBAL_NETWORK_ARN);
        assertThat(response.getResourceModel().getId()).isEqualTo(GLOBAL_NETWORK_ID);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

    }

    /**
     * A Read call MUST return a NotFound error code if the resource never existed
     * Ref: https://quip-amazon.com/bDEqAaLSgP7g/Uluru-Handler-Interface-Contract
     */
    @Test
    public void handleRequest_ResourceNotFound() {
        final DescribeGlobalNetworksResponse describeGlobalNetworksResponse = DescribeGlobalNetworksResponse.builder()
                .globalNetworks(buildGlobalNetwork())
                .build();
        final ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        doThrow(exception)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeGlobalNetworksRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);

    }


}
