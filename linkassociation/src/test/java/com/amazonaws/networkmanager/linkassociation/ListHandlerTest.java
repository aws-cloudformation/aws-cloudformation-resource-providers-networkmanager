package com.amazonaws.networkmanager.linkassociation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsRequest;
import software.amazon.awssdk.services.networkmanager.model.GetLinkAssociationsResponse;
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
public class ListHandlerTest extends TestBase {
    private ListHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new ListHandler();
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final GetLinkAssociationsResponse getLinksAssociationResponse = GetLinkAssociationsResponse.builder()
                .linkAssociations(buildLinkAssociation())
                .build();
        doReturn(getLinksAssociationResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetLinkAssociationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    /**
     * A List call MUST return a empty array if the resource never existed
     */
    @Test
    public void handleRequest_EmptyList() {
        final GetLinkAssociationsResponse getLinksAssociationResponse = GetLinkAssociationsResponse.builder()
                // an empty link association response
                .build();
        doReturn(getLinksAssociationResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetLinkAssociationsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModels()).isEqualTo(new ArrayList<>());
    }
}
