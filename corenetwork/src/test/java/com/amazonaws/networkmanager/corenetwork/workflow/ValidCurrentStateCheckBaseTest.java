package com.amazonaws.networkmanager.corenetwork.workflow;

import com.amazonaws.networkmanager.corenetwork.AbstractTestBase;
import com.amazonaws.networkmanager.corenetwork.CallbackContext;
import com.amazonaws.networkmanager.corenetwork.ResourceModel;
import com.amazonaws.networkmanager.corenetwork.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.CoreNetworkState;
import software.amazon.awssdk.services.networkmanager.model.GetCoreNetworkPolicyRequest;
import software.amazon.awssdk.services.networkmanager.model.GetCoreNetworkRequest;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidCurrentStateCheckBaseTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<NetworkManagerClient> proxyClient;

    @Mock
    NetworkManagerClient sdkClient;

    public class ValidAvailableState extends ValidCurrentStateCheckBase {

        public ValidAvailableState(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request,
                                   CallbackContext callbackContext, ProxyClient<NetworkManagerClient> client,
                                   Logger logger) {
            super(proxy, request, callbackContext, client, logger);
        }

        @Override
        protected List<String> validStates() {
            List<String> list = new ArrayList<>();
            list.add(CoreNetworkState.AVAILABLE.toString());
            return list;
        }
    }

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        sdkClient = mock(NetworkManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).getCoreNetwork(any(GetCoreNetworkRequest.class));
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void validForValidAvailableState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class))).thenReturn(MOCKS.describeResponse(tags));

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model),
                context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void failedForValidAvailableState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.model(tags, CoreNetworkState.DELETING.toString());
        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags, CoreNetworkState.DELETING.toString()));

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model),
                context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
    }

    @Test
    public void throwError() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class))).thenThrow(ResourceNotFoundException.class);

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model),
                context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void secondCall() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class))).thenReturn(MOCKS.describeResponse(tags));

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model),
                context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        context.setAttempts(2);

        ProgressEvent<ResourceModel, CallbackContext> response2 = new ValidAvailableState(proxy, MOCKS.request(model),
                context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response2).isNotNull();
        assertThat(response2.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response2.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response2.getResourceModel()).isEqualTo(model);
        assertThat(response2.getResourceModels()).isNull();
        assertThat(response2.getMessage()).isNull();
        assertThat(response2.getErrorCode()).isNull();
    }
}
