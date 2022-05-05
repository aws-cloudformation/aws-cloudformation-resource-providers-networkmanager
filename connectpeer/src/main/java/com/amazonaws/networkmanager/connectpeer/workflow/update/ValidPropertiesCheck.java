package com.amazonaws.networkmanager.connectpeer.workflow.update;

import com.amazonaws.networkmanager.connectpeer.CallbackContext;
import com.amazonaws.networkmanager.connectpeer.Configuration;
import com.amazonaws.networkmanager.connectpeer.ResourceModel;
import org.json.JSONArray;
import org.json.JSONObject;
import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidPropertiesCheck {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<NetworkManagerClient> client;
    Logger logger;
    JSONObject _config;

    public ValidPropertiesCheck(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<NetworkManagerClient> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
        this.logger = logger;
    }

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        if(this.request.getDesiredResourceState().getConnectPeerId() == null) {
            CfnNotFoundException exception =  new CfnNotFoundException(ResourceModel.TYPE_NAME, "cannot be NULL: "
                    + this.request.getDesiredResourceState().getPrimaryIdentifier());
            return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.NotFound);
        } else if(this.invalidProperties().isEmpty()) {
            logger.log("Finish the properties check");
            return progress;
        } else {
            CfnInvalidRequestException exception = new CfnInvalidRequestException(this.invalidProperties().toString()
                    + ": cannot be changed and can only be used to create " + ResourceModel.TYPE_NAME + ".");
            return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.InvalidRequest);
        }
    }

    private JSONObject getConfig() {
        if(this._config == null) {
            return this._config = new Configuration().resourceSchemaJSONObject();
        } else {
            return this._config;
        }
    }

    private List<String> createOnlyProperties() {
        //PULL CREATE ONLY PROPERTIES FROM THE JSON SCHEMA TO SEE IF THERE ARE ANY DEFINED IN THE REQUEST THAT SHOULD NOT BE

        if(this.getConfig().has("createOnlyProperties")) {
            JSONArray props = this.getConfig().getJSONArray("createOnlyProperties");
            List<String> list = new ArrayList<>();
            for(int i = 0; i < props.length(); i++){
                String prop = props.getString(i).replace("/properties/", "");
                list.add(prop);
            }
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    private List<String> invalidProperties() {
        ResourceModel model = this.request.getDesiredResourceState();
        ResourceModel previousModel = this.request.getPreviousResourceState();
        return this.createOnlyProperties().stream().filter((prop) -> {
            try {
                Method method = model.getClass().getMethod("get" + prop);
                logger.log(String.valueOf(method));
                Method previousMethod = previousModel.getClass().getMethod("get"+prop);
                logger.log(String.valueOf(previousMethod));
                return !method.invoke(model).equals(previousMethod.invoke(previousModel));
            } catch(Exception e) {
                return false;
            }
        }).collect(Collectors.toList());
    }
}
