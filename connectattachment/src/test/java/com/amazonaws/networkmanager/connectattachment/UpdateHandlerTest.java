package com.amazonaws.networkmanager.connectattachment;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
        List<Tag> newTags = new ArrayList<>();
        newTags.add(MOCKS.getCfnTag());
        when(proxyClient.client().getConnectAttachment(any(GetConnectAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse()) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse()) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(newTags, AttachmentState.AVAILABLE.toString())) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(newTags, AttachmentState.AVAILABLE.toString())); //Final Read
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(newTags);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model),
                new CallbackContext(), proxyClient, logger);

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
        final List<Tag> newTags = new ArrayList<>();
        newTags.add(MOCKS.getCfnTag());
        when(proxyClient.client().getConnectAttachment(any(GetConnectAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse(newTags)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(newTags)) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(newTags)) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse());  //Get Final Model
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());
        ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEmpty();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ChangeTagsAndSuccess() {
        final List<Tag> tags1 = new ArrayList<>();
        tags1.add(MOCKS.cfnTag("Name", "NAME_1"));
        final List<Tag> tags2 = new ArrayList<>();
        tags2.add(MOCKS.cfnTag("Name", "NAME_2"));
        when(proxyClient.client().getConnectAttachment(any(GetConnectAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags1)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(tags2, AttachmentState.AVAILABLE.toString())); //Get Final model
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(tags2);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
