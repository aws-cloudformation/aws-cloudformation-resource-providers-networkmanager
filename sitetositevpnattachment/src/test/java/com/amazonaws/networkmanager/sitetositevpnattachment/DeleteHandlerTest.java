package com.amazonaws.networkmanager.sitetositevpnattachment;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeleteAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.GetSiteToSiteVpnAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.ValidationException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<NetworkManagerClient> proxyClient;

    @Mock
    NetworkManagerClient sdkClient;

    private DeleteHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new DeleteHandler();
        sdkClient = mock(NetworkManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        when(proxyClient.client().deleteAttachment(any(DeleteAttachmentRequest.class))).thenReturn(MOCKS.deleteResponse());
        when(proxyClient.client().getSiteToSiteVpnAttachment(any(GetSiteToSiteVpnAttachmentRequest.class))).thenReturn(MOCKS.describeResponse())
                .thenThrow(ResourceNotFoundException.class);
        ResourceModel model = MOCKS.model();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_Error() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        ValidationException exception = ValidationException.builder().message("Invalid Request").build();
        when(proxyClient.client().deleteAttachment(any(DeleteAttachmentRequest.class))).thenThrow(exception);
        when(proxyClient.client().getSiteToSiteVpnAttachment(any(GetSiteToSiteVpnAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse());

        ResourceModel model = MOCKS.model(tags);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("Invalid Request")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_NotFound() {
        ResourceModel model = MOCKS.model();
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        when(proxyClient.client().getSiteToSiteVpnAttachment(any(GetSiteToSiteVpnAttachmentRequest.class))).thenThrow(exception);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("not found")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
