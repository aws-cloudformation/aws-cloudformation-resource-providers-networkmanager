package com.amazonaws.networkmanager.vpcattachment;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.*;
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
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_CreateTagsSuccess() {
        List<Tag> newTags = new ArrayList<>();
        newTags.add(MOCKS.getCfnTag());
        ResourceModel updateModel = MOCKS.modelWithoutCreateOnlyProperties(newTags);
        when(proxyClient.client().getVpcAttachment(any(GetVpcAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse()) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse()) //Update getPreviousModel Read
                .thenReturn(MOCKS.describeResponse()) //Update stabilize Read
                .thenReturn(MOCKS.describeResponse()) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(newTags, AttachmentState.PENDING_TAG_ACCEPTANCE.toString())) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(newTags, AttachmentState.PENDING_TAG_ACCEPTANCE.toString())); //Final Read
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(updateModel),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(updateModel.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getSubnetArns()).isEqualTo(updateModel.getSubnetArns());
        assertThat(response.getResourceModel().getTags()).isEqualTo(updateModel.getTags());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_DeleteTagsSuccess() {
        ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties();
        List<Tag> newTags = new ArrayList<>();
        newTags.add(MOCKS.getCfnTag());
        when(proxyClient.client().getVpcAttachment(any(GetVpcAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse()) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(newTags))   //Update getPreviousModel Read
                .thenReturn(MOCKS.describeResponse(newTags))   //Update Stabilize Read
                .thenReturn(MOCKS.describeResponse(newTags))   //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(newTags))   //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse());         //Get Final Model
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEmpty();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_ChangeTagsAndSubnetArnsWhenAvailableExpectSuccess() {
        List<String> newSubnetArns = new ArrayList<>();
        newSubnetArns.add("arn:aws:ec2:region:account:subnet/subnet-hh223344987657889");
        newSubnetArns.add("arn:aws:ec2:region:account:subnet/subnet-bb2233449876578tt");
        List<Tag> tags1 = new ArrayList<>();
        tags1.add(MOCKS.cfnTag("Name1", "NAME_1"));
        List<Tag> tags2 = new ArrayList<>();
        tags2.add(MOCKS.cfnTag("Name2", "NAME_2"));
        List<Tag> tags3 = new ArrayList<>();
        tags3.addAll(tags1);
        tags3.addAll(tags2);
        when(proxyClient.client().getVpcAttachment(any(GetVpcAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags1)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Update getPreviousModel Read
                .thenReturn(MOCKS.describeResponse(tags1, AttachmentState.AVAILABLE.toString(), newSubnetArns)) //Update stabilize
                .thenReturn(MOCKS.describeResponse(tags1, AttachmentState.AVAILABLE.toString(), newSubnetArns)) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(tags3, AttachmentState.AVAILABLE.toString(), newSubnetArns)) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(tags2, AttachmentState.AVAILABLE.toString())); //Get Final model
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());
        when(proxyClient.client().updateVpcAttachment(any(UpdateVpcAttachmentRequest.class)))
                .thenReturn(MOCKS.updateResponse(newSubnetArns));

        ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(tags2, newSubnetArns);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_ChangeSubnetArnsSuccess() {
        List<String> newSubnetArns = new ArrayList<>();
        newSubnetArns.add("arn:aws:ec2:region:account:subnet/subnet-bb2233449876578tt");
        ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(), AttachmentState.AVAILABLE.toString());
        newSubnetArns.addAll(model.getSubnetArns());
        model.setSubnetArns(newSubnetArns);
        when(proxyClient.client().getVpcAttachment(any(GetVpcAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse(new ArrayList<>(), AttachmentState.AVAILABLE.toString())) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(new ArrayList<>(), AttachmentState.AVAILABLE.toString())) //Update Read
                .thenReturn(MOCKS.describeResponse(new ArrayList<>(), AttachmentState.AVAILABLE.toString(), newSubnetArns)) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(new ArrayList<>(), AttachmentState.AVAILABLE.toString(), newSubnetArns)) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(new ArrayList<>(), AttachmentState.AVAILABLE.toString(), newSubnetArns)); //Get Final Model
        when(proxyClient.client().updateVpcAttachment(any(UpdateVpcAttachmentRequest.class)))
                .thenReturn(MOCKS.updateResponse(newSubnetArns));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getResourceModel().getSubnetArns()).isEqualTo(model.getSubnetArns());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_ChangeSubnetArnsWhenPendingTagAcceptanceExpectFailure() {
        List<String> newSubnetArns = new ArrayList<>();
        newSubnetArns.add("arn:aws:ec2:region:account:subnet/subnet-bb2233449876578tt");
        ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(), AttachmentState.AVAILABLE.toString());
        newSubnetArns.addAll(model.getSubnetArns());
        model.setSubnetArns(newSubnetArns);
        when(proxyClient.client().getVpcAttachment(any(GetVpcAttachmentRequest.class)))
                .thenReturn(MOCKS.describeResponse(new ArrayList<>(), AttachmentState.PENDING_TAG_ACCEPTANCE.toString())) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(new ArrayList<>(), AttachmentState.AVAILABLE.toString())); //Update Read

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        System.out.println(response.getMessage());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getMessage().contains("Cannot update vpc attachment when attachment is in PENDING_TAG_ACCEPTANCE state")).isTrue();
    }
}
