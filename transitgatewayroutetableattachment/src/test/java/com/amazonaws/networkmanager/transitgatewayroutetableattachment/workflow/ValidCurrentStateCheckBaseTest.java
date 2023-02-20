package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.AbstractTestBase;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.CallbackContext;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.ResourceModel;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRouteTableAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
                                   CallbackContext callbackContext, ProxyClient<NetworkManagerClient> client, Logger logger) {
            super(proxy, request, callbackContext, client, logger);
        }

        @Override
        protected List<String> validStates() {
            List<String> list = new ArrayList<>();
            list.add(AttachmentState.AVAILABLE.toString());
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
        verify(sdkClient, atLeastOnce()).getTransitGatewayRouteTableAttachment(any(GetTransitGatewayRouteTableAttachmentRequest.class));
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void validForValidAvailableState() {
        final Set<Tag> tags = new HashSet<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().getTransitGatewayRouteTableAttachment(any(GetTransitGatewayRouteTableAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags));

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger)
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
        final Set<Tag> tags = new HashSet<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.model(tags, AttachmentState.DELETING.toString(), new HashSet<>());
        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().getTransitGatewayRouteTableAttachment(any(GetTransitGatewayRouteTableAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags, AttachmentState.DELETING.toString()));

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
    }

    @Test
    public void throwError() {
        final Set<Tag> tags = new HashSet<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().getTransitGatewayRouteTableAttachment(any(GetTransitGatewayRouteTableAttachmentRequest.class)))
                .thenThrow(ResourceNotFoundException.class);

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger)
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
        final Set<Tag> tags = new HashSet<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().getTransitGatewayRouteTableAttachment(any(GetTransitGatewayRouteTableAttachmentRequest.class))).thenReturn(MOCKS.describeResponse(tags));
        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        context.setAttempts(2);

        ProgressEvent<ResourceModel, CallbackContext> response2 = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger)
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
