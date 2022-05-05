package com.amazonaws.networkmanager.sitetositevpnattachment.workflow.update;

import com.amazonaws.networkmanager.sitetositevpnattachment.CallbackContext;
import com.amazonaws.networkmanager.sitetositevpnattachment.ResourceModel;
import com.amazonaws.networkmanager.sitetositevpnattachment.Tag;
import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.Utils;
import com.amazonaws.networkmanager.sitetositevpnattachment.workflow.ValidCurrentStateCheckBase;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

public class ValidCurrentStateCheck extends ValidCurrentStateCheckBase {
    public ValidCurrentStateCheck(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request,
                                  CallbackContext callbackContext, ProxyClient<NetworkManagerClient> client, Logger logger) {
        super(proxy, request, callbackContext, client, logger);
    }

    @Override
    protected List<String> validStates() {
        List<String> list = new ArrayList<>();
        list.add(AttachmentState.AVAILABLE.toString());
        list.add(AttachmentState.PENDING_ATTACHMENT_ACCEPTANCE.toString());
        return list;
    }
}
