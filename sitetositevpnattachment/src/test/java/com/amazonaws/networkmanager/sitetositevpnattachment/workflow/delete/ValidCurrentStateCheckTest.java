package com.amazonaws.networkmanager.sitetositevpnattachment.workflow.delete;

import com.amazonaws.networkmanager.sitetositevpnattachment.AbstractTestBase;
import com.amazonaws.networkmanager.sitetositevpnattachment.CallbackContext;
import com.amazonaws.networkmanager.sitetositevpnattachment.ResourceModel;
import com.amazonaws.networkmanager.sitetositevpnattachment.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetSiteToSiteVpnAttachmentRequest;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidCurrentStateCheckTest extends AbstractTestBase {
    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<NetworkManagerClient> proxyClient;

    @Mock
    NetworkManagerClient sdkClient;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        sdkClient = mock(NetworkManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).getSiteToSiteVpnAttachment(any(GetSiteToSiteVpnAttachmentRequest.class));
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void validForAvailableState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        when(proxyClient.client().getSiteToSiteVpnAttachment(any(GetSiteToSiteVpnAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags));
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidCurrentStateCheck(proxy,
                MOCKS.request(model), context, proxyClient, logger)
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
    public void failedForDeletedCurrentState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        when(proxyClient.client().getSiteToSiteVpnAttachment(any(GetSiteToSiteVpnAttachmentRequest.class)))
                .thenThrow(ResourceNotFoundException.class);
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidCurrentStateCheck(proxy,
                MOCKS.request(model), context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
        assertThat(response.getMessage().contains("Cannot find")).isTrue();
    }
}
