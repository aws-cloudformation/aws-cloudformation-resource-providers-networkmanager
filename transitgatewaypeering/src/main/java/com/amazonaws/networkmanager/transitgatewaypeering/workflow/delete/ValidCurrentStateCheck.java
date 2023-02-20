package com.amazonaws.networkmanager.transitgatewaypeering.workflow.delete;

import com.amazonaws.networkmanager.transitgatewaypeering.CallbackContext;
import com.amazonaws.networkmanager.transitgatewaypeering.ResourceModel;
import com.amazonaws.networkmanager.transitgatewaypeering.workflow.ValidCurrentStateCheckBase;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
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
        list.add(PeeringState.CREATING.toString());
        list.add(PeeringState.DELETING.toString());
        return list;
    }
}
