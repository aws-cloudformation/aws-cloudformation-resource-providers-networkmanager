package com.amazonaws.networkmanager.connectpeer.workflow.delete;

import com.amazonaws.networkmanager.connectpeer.CallbackContext;
import com.amazonaws.networkmanager.connectpeer.ResourceModel;
import com.amazonaws.networkmanager.connectpeer.workflow.ValidCurrentStateCheckBase;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.ConnectPeerState;
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

    //TODO Confirm if this is correct.
    @Override
    protected List<String> invalidStates() {
        List<String> list = new ArrayList<>();
        list.add(ConnectPeerState.CREATING.toString());
        return list;
    }
}
