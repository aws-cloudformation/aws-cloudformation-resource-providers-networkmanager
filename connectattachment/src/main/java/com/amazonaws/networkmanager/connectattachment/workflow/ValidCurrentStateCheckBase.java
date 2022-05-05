package com.amazonaws.networkmanager.connectattachment.workflow;

import com.amazonaws.networkmanager.connectattachment.CallbackContext;
import com.amazonaws.networkmanager.connectattachment.ResourceModel;
import com.amazonaws.networkmanager.connectattachment.workflow.read.Read;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

public class ValidCurrentStateCheckBase {
    protected AmazonWebServicesClientProxy proxy;
    protected ResourceHandlerRequest<ResourceModel> request;
    protected CallbackContext callbackContext;
    protected ProxyClient<NetworkManagerClient> client;
    protected Logger logger;
    protected ProgressEvent<ResourceModel, CallbackContext> progress;
    protected ResourceModel model;
    String _currentState;

    public ValidCurrentStateCheckBase(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<NetworkManagerClient> client,
            Logger logger
    ) {
        this.model = request.getDesiredResourceState();
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
        this.logger = logger;
    }

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        if(this.callbackContext.getAttempts() > 0) { return progress; } //skip if this not the first attempt by the lambda function

        try {
            this.progress = progress;
            return this.validate();
        } catch (ResourceNotFoundException e) {
            return this.handleCfnNotFoundException();
        } catch (Exception e) {
            return this.handleError(e);
        }
    }

    protected ResourceModel makeRequest() {
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(this.model);
    }

    protected ProgressEvent<ResourceModel, CallbackContext> validate() {
        String currentState = this.currentState().toUpperCase();
        if((this.invalidStates().isEmpty() && this.validStates().contains(currentState))
                || (this.validStates().isEmpty() && !this.invalidStates().contains(currentState))) {
            return this.progress;
        } else {
            return this.handleCfnConflictException();
        }
    }

    protected String action() {
        String packageName =  this.getClass().getPackage().getName();
        String[] packageParts = packageName.split("\\.");
        return packageParts[packageParts.length - 1];
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleCfnConflictException() {
        CfnResourceConflictException exception =  new CfnResourceConflictException(ResourceModel.TYPE_NAME,
                model.getPrimaryIdentifier().toString().replace("/properties/", ""), "STATE: "
                + this.currentState() + " cannot be modified by ACTION: " + this.action().toUpperCase() + "");
        return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.ResourceConflict);
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleCfnNotFoundException() {
        CfnNotFoundException exception =  new CfnNotFoundException(ResourceModel.TYPE_NAME,
                this.model.getPrimaryIdentifier().toString());
        return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.NotFound);
    }

    protected List<String> validStates() {
        return new ArrayList<>();
    }

    protected List<String> invalidStates() {return new ArrayList<>(); }

    protected String currentState() {
        if(this._currentState != null) {
            return this._currentState;
        } else {
            ResourceModel modelToDelete = this.makeRequest();
            if (modelToDelete == null) {
                return null;
            }
            return this._currentState = modelToDelete.getState();
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(Exception exception) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
