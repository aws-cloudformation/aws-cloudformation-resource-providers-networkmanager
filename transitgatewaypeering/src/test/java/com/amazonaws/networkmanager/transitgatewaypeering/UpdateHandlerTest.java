package com.amazonaws.networkmanager.transitgatewaypeering;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayPeeringRequest;
import software.amazon.awssdk.services.networkmanager.model.PeeringState;
import software.amazon.awssdk.services.networkmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.networkmanager.model.UntagResourceRequest;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<NetworkManagerClient> proxyClient;

    @Mock
    NetworkManagerClient sdkClient;

    private UpdateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new UpdateHandler();
        sdkClient = mock(NetworkManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_CreateTagsSuccess() {
        Set<Tag> newTags = new HashSet<>();
        newTags.add(MOCKS.getCfnTag());
        when(proxyClient.client().getTransitGatewayPeering(any(GetTransitGatewayPeeringRequest.class)))
                .thenReturn(MOCKS.describeResponse()) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse()) // CreateTag Read
                .thenReturn(MOCKS.describeResponse(newTags, AttachmentState.AVAILABLE.toString())) // DeleteTag Read
                .thenReturn(MOCKS.describeResponse(newTags, AttachmentState.AVAILABLE.toString())); // Final Read
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(newTags);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.updateHandlerRequest(model, new HashSet<>()), new CallbackContext(), proxyClient, logger);

        System.out.println(response.getResourceModel().getTags());
        System.out.println(model.getTags());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_DeleteTagsSuccess() {
        final Set<Tag> tags = new HashSet<>();
        tags.add(MOCKS.getCfnTag());
        when(proxyClient.client().getTransitGatewayPeering(any(GetTransitGatewayPeeringRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags)) // PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tags)) // CreateTags Read
                .thenReturn(MOCKS.describeResponse(tags)) // DeleteTags Read
                .thenReturn(MOCKS.describeResponse());  // Get Final Model
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());
        ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.updateHandlerRequest(model, tags), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEmpty();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ChangeTagsSuccess() {
        final Set<Tag> tags1 = new HashSet<>();
        tags1.add(MOCKS.cfnTag("Name", "NAME_1"));
        final Set<Tag> tags2 = new HashSet<>();
        tags2.add(MOCKS.cfnTag("Name", "NAME_2"));
        when(proxyClient.client().getTransitGatewayPeering(any(GetTransitGatewayPeeringRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags1)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Create Read
                .thenReturn(MOCKS.describeResponse(tags2)) //Delete Read
                .thenReturn(MOCKS.describeResponse(tags2)); //Get Final model
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(tags2);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.updateHandlerRequest(model, tags1), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
