package com.amazonaws.networkmanager.vpcattachment.workflow.update;

import com.amazonaws.networkmanager.vpcattachment.CallbackContext;
import com.amazonaws.networkmanager.vpcattachment.ResourceModel;
import com.amazonaws.networkmanager.vpcattachment.workflow.ExceptionMapper;
import com.amazonaws.networkmanager.vpcattachment.workflow.Utils;
import com.amazonaws.networkmanager.vpcattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.*;

import java.util.List;

public class Update {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;

    public Update(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<NetworkManagerClient> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.logger = logger;
        this.callbackContext = callbackContext;
        this.client = client;
    }

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();
    }

    private UpdateVpcAttachmentRequest translateModelToRequest(ResourceModel model) {
        ResourceModel previousModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model);
        List<String> subnetArnsToAdd = Utils.subnetArnsDifference(model.getSubnetArns(), previousModel.getSubnetArns());
        List<String> subnetArnsToRemove = Utils.subnetArnsDifference(previousModel.getSubnetArns(), model.getSubnetArns());
        UpdateVpcAttachmentRequest.Builder requestBuilder = UpdateVpcAttachmentRequest.builder()
                .attachmentId(model.getAttachmentId());
        if (model.getOptions() != null && model.getOptions().getIpv6Support() != previousModel.getOptions().getIpv6Support()) {
            requestBuilder.options(Utils.cfnOptionsToSdkOptions(model.getOptions()));
        }
        logger.log("subnet to add" + subnetArnsToAdd.toString());
        logger.log("subnet to remove" + subnetArnsToRemove.toString());
        if (model.getSubnetArns() != null) {
            if (!subnetArnsToAdd.isEmpty()) {
                requestBuilder.addSubnetArns(subnetArnsToAdd);
            }
            if (!subnetArnsToRemove.isEmpty()) {
                requestBuilder.removeSubnetArns(subnetArnsToRemove);
            }
        }
        return requestBuilder.build();
    }

    private UpdateVpcAttachmentResponse makeServiceCall(UpdateVpcAttachmentRequest updateVpcAttachmentRequest,
                                                        ProxyClient<NetworkManagerClient> client) {
        if (!updateVpcAttachmentRequest.hasAddSubnetArns() && !updateVpcAttachmentRequest.hasRemoveSubnetArns()
                && updateVpcAttachmentRequest.options() == null) {
            return UpdateVpcAttachmentResponse.builder().build();
        }
        return proxy.injectCredentialsAndInvokeV2(updateVpcAttachmentRequest, client.client()::updateVpcAttachment);
    }

    private boolean stabilize(
            UpdateVpcAttachmentRequest awsRequest,
            UpdateVpcAttachmentResponse awsResponse,
            ProxyClient<NetworkManagerClient> client,
            ResourceModel model,
            CallbackContext context
    ) {
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model)
                .getState();
        if (AttachmentState.FAILED.toString().equals(currentState)) {
            throw new RuntimeException("Fail to update Resource: " + model.getAttachmentId());
        }
        return AttachmentState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(UpdateVpcAttachmentRequest awsRequest,
                                                                         Exception exception,
                                                                         ProxyClient<NetworkManagerClient> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
