package com.amazonaws.networkmanager.corenetwork;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
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
import static org.mockito.Mockito.*;

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
        final List<Tag> newTags = new ArrayList<>();
        newTags.add(MOCKS.getCfnTag());
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class)))
                .thenReturn(MOCKS.describeResponse()) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse()) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(newTags, CoreNetworkState.AVAILABLE.toString())) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(newTags, CoreNetworkState.AVAILABLE.toString())); //Final Read
        when(proxyClient.client().getCoreNetworkPolicy(any(GetCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse());
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(newTags, CoreNetworkState.AVAILABLE.toString(), null);
        final ResourceModel previousModel = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(), CoreNetworkState.AVAILABLE.toString(), null);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.request(model, previousModel), new CallbackContext(), proxyClient, logger);

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
        final List<Tag> tagsToDelete = new ArrayList<>();
        tagsToDelete.add(MOCKS.getCfnTag());
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class)))
                .thenReturn(MOCKS.describeResponse(tagsToDelete)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tagsToDelete)) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(tagsToDelete)) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(CoreNetworkState.AVAILABLE.toString(), null));  //Get Final Model
        when(proxyClient.client().getCoreNetworkPolicy(any(GetCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(),
                CoreNetworkState.AVAILABLE.toString(), null);
        final ResourceModel previousModel = MOCKS.modelWithoutCreateOnlyProperties(tagsToDelete,
                CoreNetworkState.AVAILABLE.toString(), null);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.request(model, previousModel), new CallbackContext(), proxyClient, logger);

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
        final List<Tag> tags1 = new ArrayList<>();
        tags1.add(MOCKS.cfnTag("Name", "NAME_1"));
        final List<Tag> tags2 = new ArrayList<>();
        tags2.add(MOCKS.cfnTag("Name", "NAME_2"));
        List<Tag> tags3 = new ArrayList<>();
        tags3.addAll(tags1);
        tags3.addAll(tags2);
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags1)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(tags3)) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(tags2, CoreNetworkState.AVAILABLE.toString())); //Get Final model
        when(proxyClient.client().getCoreNetworkPolicy(any(GetCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse());
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(tags2,  CoreNetworkState.AVAILABLE.toString(), null);
        final ResourceModel previousModel = MOCKS.modelWithoutCreateOnlyProperties(tags1,  CoreNetworkState.AVAILABLE.toString(), null);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                MOCKS.request(model, previousModel), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ChangeDescriptionSuccess() {
        String newDescription = "new Description";
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class)))
                .thenReturn(MOCKS.describeResponse()) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse()) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse()) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(CoreNetworkState.AVAILABLE.toString(), newDescription)); //Get Final model
        when(proxyClient.client().getCoreNetworkPolicy(any(GetCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse());
        when(proxyClient.client().updateCoreNetwork(any(UpdateCoreNetworkRequest.class)))
                .thenReturn(MOCKS.updateResponse(newDescription));
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(),
                CoreNetworkState.AVAILABLE.toString(), newDescription, MOCKS.policyDocument);
        final ResourceModel previousModel = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(),
                CoreNetworkState.AVAILABLE.toString(), MOCKS.description, MOCKS.policyDocument);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler
                .handleRequest(proxy, MOCKS.request(model, previousModel), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ChangeTagsAndDescriptionSuccess() {
        final String newDescription = "newDescription";
        final List<Tag> tags1 = new ArrayList<>();
        tags1.add(MOCKS.cfnTag("Name", "NAME_1"));
        final List<Tag> tags2 = new ArrayList<>();
        tags2.add(MOCKS.cfnTag("Name", "NAME_2"));
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags1)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(tags2, CoreNetworkState.AVAILABLE.toString(), newDescription)) //Update description stabilization
                .thenReturn(MOCKS.describeResponse(tags2, CoreNetworkState.AVAILABLE.toString(), newDescription)); //Get Final model
        when(proxyClient.client().getCoreNetworkPolicy(any(GetCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse());
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());
        when(proxyClient.client().updateCoreNetwork(any(UpdateCoreNetworkRequest.class)))
                .thenReturn(MOCKS.updateResponse(newDescription));
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(tags2,
                CoreNetworkState.AVAILABLE.toString(), newDescription, MOCKS.policyDocument);
        final ResourceModel previousModel = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(),
                CoreNetworkState.AVAILABLE.toString(), null, MOCKS.policyDocument);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model, previousModel),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ChangePolicyDocumentSuccess() {
        String newPolicyDocument = "{new policy document}";
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class)))
                .thenReturn(MOCKS.describeResponse()) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse()) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse()) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse()); //Get Description
        when(proxyClient.client().putCoreNetworkPolicy(any(PutCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.putCoreNetworkPolicyResponse(newPolicyDocument));
        when(proxyClient.client().getCoreNetworkPolicy(any(GetCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse(newPolicyDocument, ChangeSetState.READY_TO_EXECUTE.toString())) // Put policy stabilize
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse(newPolicyDocument, ChangeSetState.EXECUTION_SUCCEEDED.toString())); // Execute policy stabilize
        when(proxyClient.client().executeCoreNetworkChangeSet(any(ExecuteCoreNetworkChangeSetRequest.class)))
                .thenReturn(MOCKS.getExecuteChangeSetResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(),
                CoreNetworkState.AVAILABLE.toString(), null, newPolicyDocument);
        final ResourceModel previousModel = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(),
                CoreNetworkState.AVAILABLE.toString(), null, MOCKS.policyDocument);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler
                .handleRequest(proxy, MOCKS.request(model, previousModel), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getPolicyDocument()).isEqualTo(newPolicyDocument);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ChangeTagsAndDescriptionAndPolicyDocumentSuccess() {
        String newPolicyDocument = "{new policy document}";
        final String newDescription = "newDescription";
        final List<Tag> tags1 = new ArrayList<>();
        tags1.add(MOCKS.cfnTag("Name", "NAME_1"));
        final List<Tag> tags2 = new ArrayList<>();
        tags2.add(MOCKS.cfnTag("Name", "NAME_2"));
        when(proxyClient.client().getCoreNetwork(any(GetCoreNetworkRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags1)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Create Tags Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Delete Tags Read
                .thenReturn(MOCKS.describeResponse(tags2, CoreNetworkState.AVAILABLE.toString(), newDescription)) //Update description stabilization
                .thenReturn(MOCKS.describeResponse(tags2, CoreNetworkState.AVAILABLE.toString(), newDescription)); //Get Final model
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(MOCKS.tagResourceResponse());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(MOCKS.untagResourceResponse());
        when(proxyClient.client().updateCoreNetwork(any(UpdateCoreNetworkRequest.class)))
                .thenReturn(MOCKS.updateResponse(newDescription));
        when(proxyClient.client().putCoreNetworkPolicy(any(PutCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.putCoreNetworkPolicyResponse(newPolicyDocument));
        when(proxyClient.client().getCoreNetworkPolicy(any(GetCoreNetworkPolicyRequest.class)))
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse())
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse(newPolicyDocument, ChangeSetState.READY_TO_EXECUTE.toString())) // Put policy stabilize
                .thenReturn(MOCKS.getCoreNetworkPolicyResponse(newPolicyDocument, ChangeSetState.EXECUTION_SUCCEEDED.toString())); // Execute policy stabilize
        when(proxyClient.client().executeCoreNetworkChangeSet(any(ExecuteCoreNetworkChangeSetRequest.class)))
                .thenReturn(MOCKS.getExecuteChangeSetResponse());
        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(tags2,
                CoreNetworkState.AVAILABLE.toString(), newDescription, newPolicyDocument);
        final ResourceModel previousModel = MOCKS.modelWithoutCreateOnlyProperties(new ArrayList<>(),
                CoreNetworkState.AVAILABLE.toString(), null, MOCKS.policyDocument);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model, previousModel),
                new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getResourceModel().getPolicyDocument()).isEqualTo(model.getPolicyDocument());
        assertThat(response.getResourceModel().getDescription()).isEqualTo(model.getDescription());
        assertThat(response.getResourceModel().getTags()).isEqualTo(model.getTags());
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
