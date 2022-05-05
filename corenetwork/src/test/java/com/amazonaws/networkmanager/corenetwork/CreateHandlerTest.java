package com.amazonaws.networkmanager.corenetwork;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CreateCoreNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.GetCoreNetworkPolicyRequest;
import software.amazon.awssdk.services.networkmanager.model.GetCoreNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.ValidationException;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<NetworkManagerClient> proxyClient;

    @Mock
    NetworkManagerClient sdkClient;

    private CreateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new CreateHandler();
        sdkClient = mock(NetworkManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        ResourceModel model = MOCKS.model();
        when(proxyClient.client().createCoreNetwork(any(CreateCoreNetworkRequest.class))).thenReturn(MOCKS.createResponse());
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class))).thenReturn(MOCKS.describeResponse());
        when(proxyClient.client().getCoreNetworkPolicy(any(GetCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Error() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        ValidationException exception = ValidationException.builder().message("Invalid Request").build();
        when(proxyClient.client().createCoreNetwork(any(CreateCoreNetworkRequest.class))).thenThrow(exception);
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
    }
}
