package com.amazonaws.networkmanager.globalnetwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.DescribeGlobalNetworksRequest;
import software.amazon.awssdk.services.networkmanager.model.DescribeGlobalNetworksResponse;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends TestBase {

    private ListHandler handler;


    @BeforeEach
    public void setup() {
        handler = new ListHandler();
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
                .nextToken("nextToken")
                .desiredResourceState(ResourceModel.builder().build())
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getArn()).isEqualTo(GLOBAL_NETWORK_ARN);
        assertThat(response.getResourceModels().get(0).getId()).isEqualTo(GLOBAL_NETWORK_ID);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

    }

    /**
     * A List call MUST return an empty array if there are no resources found
     * Ref: https://quip-amazon.com/bDEqAaLSgP7g/Uluru-Handler-Interface-Contract
     */
    @Test
    public void handleRequest_NoResourceFound() {
        final DescribeGlobalNetworksResponse describeGlobalNetworksResponse = DescribeGlobalNetworksResponse.builder()
                .build();
        doReturn(describeGlobalNetworksResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeGlobalNetworksRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder().build())
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().isEmpty());
        assertThat(response.getResourceModels() != null);

    }

}
