package com.amazonaws.networkmanager.vpcattachment.workflow.update;

import com.amazonaws.networkmanager.vpcattachment.AbstractTestBase;
import com.amazonaws.networkmanager.vpcattachment.CallbackContext;
import com.amazonaws.networkmanager.vpcattachment.ResourceModel;
import com.amazonaws.networkmanager.vpcattachment.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ValidPropertiesCheckTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<NetworkManagerClient> proxyClient;

    @Test
    public void run_WithoutPrimaryKey() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.modelWithoutPrimaryIdentifier(tags);
        CallbackContext context =  new CallbackContext();

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidPropertiesCheck(proxy,
                MOCKS.request(model), context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("cannot be NULL")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void run_NullProperties() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.getCfnTag());
        ResourceModel model = MOCKS.modelWithNullProperties(tags);
        CallbackContext context =  new CallbackContext();

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidPropertiesCheck(proxy,
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
}
