package com.amazonaws.networkmanager.transitgatewayregistration;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsResponse;
import software.amazon.awssdk.services.networkmanager.model.RegisterTransitGatewayRequest;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistrationState;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.cloudformation.proxy.OperationStatus.IN_PROGRESS;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;


public class CreateHandler extends BaseHandler<CallbackContext> {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // Initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final NetworkManagerClient client = ClientBuilder.getClient();
        try {
            // Call NetworkManager api to register TransitGateway
            if (callbackContext == null) {
                registerTransitGateway(client, model, proxy, logger);
                logger.log(String.format("%s [%s] creation pending", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModel(model)
                        .status(IN_PROGRESS)
                        .callbackDelaySeconds(Utils.CALlBACK_PERIOD_30_SECONDS)
                        .callbackContext(CallbackContext.builder().createStarted(true).build())
                        .build();
            }

            // Call NetworkManager api to verify creation status
            // If state is pending callback every 30s until TransitGatewayRegistrationState is available
            final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = Utils.getTransitGatewayRegistrations(client, model, proxy, logger);
            if(getTransitGatewayRegistrationsResponse.transitGatewayRegistrations().isEmpty()) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME,  model.getPrimaryIdentifier().toString());
            }
            final String state = getTransitGatewayRegistrationsResponse.transitGatewayRegistrations().get(0).state().codeAsString();
            if (TransitGatewayRegistrationState.AVAILABLE.name().equals(state)) {
                logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModel(model)
                        .status(SUCCESS)
                        .build();
            } else if (TransitGatewayRegistrationState.PENDING.name().equals(state)) {
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModel(model)
                        .status(IN_PROGRESS)
                        .callbackDelaySeconds(Utils.CALlBACK_PERIOD_30_SECONDS)
                        .callbackContext(callbackContext)
                        .build();
            } else {
                throw new CfnResourceConflictException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString(),
                        "TransitGateway Registration is not in AVAILABLE or PENDING state");
            }

        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

    }

    private void registerTransitGateway(final NetworkManagerClient client,
                                        final ResourceModel model,
                                        final AmazonWebServicesClientProxy proxy,
                                        final Logger logger) {
        final RegisterTransitGatewayRequest registerTransitGatewayRequest =
                RegisterTransitGatewayRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .transitGatewayArn(model.getTransitGatewayArn())
                        .build();
        proxy.injectCredentialsAndInvokeV2(registerTransitGatewayRequest, client::registerTransitGateway);
    }
}
