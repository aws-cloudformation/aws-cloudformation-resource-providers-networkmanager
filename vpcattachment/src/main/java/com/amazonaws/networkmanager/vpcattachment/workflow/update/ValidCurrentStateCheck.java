package com.amazonaws.networkmanager.vpcattachment.workflow.update;

import com.amazonaws.networkmanager.vpcattachment.CallbackContext;
import com.amazonaws.networkmanager.vpcattachment.ResourceModel;
import com.amazonaws.networkmanager.vpcattachment.workflow.ValidCurrentStateCheckBase;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.AttachmentState;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.*;

import java.util.HashSet;
import java.util.Set;

public class ValidCurrentStateCheck extends ValidCurrentStateCheckBase {
    public ValidCurrentStateCheck(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request,
                                  CallbackContext callbackContext, ProxyClient<NetworkManagerClient> client,
                                  Logger logger) {
        super(proxy, request, callbackContext, client, logger);
    }

    protected ProgressEvent<ResourceModel, CallbackContext> validate() {
        ResourceModel previousModel = this.makeRequest();
        ResourceModel currentModel = this.request.getDesiredResourceState();
        String currentState = previousModel.getState();
        if (currentState.equals(AttachmentState.AVAILABLE.toString())) {
            return this.progress;
        } else if (currentState.equals(AttachmentState.PENDING_TAG_ACCEPTANCE.toString())) {
            boolean optionsUpdated = false;
            if (currentModel.getOptions() != null) {
                optionsUpdated = !previousModel.getOptions().getIpv6Support().equals(currentModel
                        .getOptions().getIpv6Support());
            }
            boolean subnetArnsUpdated = false;
            Set<String> prevSubnetArns = new HashSet<>(previousModel.getSubnetArns());
            Set<String> currSubnetArns = new HashSet<>(currentModel.getSubnetArns());
            if (currentModel.getSubnetArns() != null) {
                logger.log(String.valueOf(currentModel.getSubnetArns()));
                subnetArnsUpdated = !prevSubnetArns.equals(currSubnetArns);
            }
            // Cannot update vpc attachment when attachment is in PENDING_TAG_ACCEPTANCE state
            if (subnetArnsUpdated || optionsUpdated) {
                CfnInvalidRequestException exception =  new CfnInvalidRequestException("Cannot update vpc attachment " +
                        "when attachment is in PENDING_TAG_ACCEPTANCE state");
                return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.InvalidRequest);
            }
            return this.progress;
        } else {
            return this.handleCfnConflictException();
        }
    }
}
