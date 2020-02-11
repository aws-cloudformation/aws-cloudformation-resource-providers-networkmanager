package com.amazonaws.networkmanager.link;

import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

/*
Skipping the implementation of ListHandler for now as there is no use case for listing site through cloudformation.
CloudFormation invokes this handler when summary information about multiple resources of this resource provider is required.
 */
public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final List<ResourceModel> models = new ArrayList<>();

        // TODO : put your code here

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(models)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
