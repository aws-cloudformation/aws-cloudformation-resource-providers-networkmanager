package com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.delete;

import com.amazonaws.networkmanager.transitgatewayroutetableattachment.CallbackContext;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.ResourceModel;
import com.amazonaws.networkmanager.transitgatewayroutetableattachment.workflow.ValidCurrentStateCheckBase;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.awssdk.services.networkmanager.model.PeeringState;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class ValidCurrentStateCheck extends ValidCurrentStateCheckBase {
    public ValidCurrentStateCheck(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request,
                                  CallbackContext callbackContext, ProxyClient<NetworkManagerClient> client, Logger logger) {
        super(proxy, request, callbackContext, client, logger);
    }

    @Override
    protected List<String> invalidStates() {
        List<String> list = new ArrayList<>();
        list.add(AttachmentState.CREATING.toString());
        list.add(AttachmentState.UPDATING.toString());
        list.add(AttachmentState.DELETING.toString());
        list.add(AttachmentState.PENDING_NETWORK_UPDATE.toString());
        return list;
    }
}
