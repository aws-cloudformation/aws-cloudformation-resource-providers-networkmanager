package com.amazonaws.networkmanager.transitgatewayregistration;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.ValidationException;
import software.amazon.awssdk.services.networkmanager.model.GetTransitGatewayRegistrationsResponse;
import software.amazon.awssdk.services.networkmanager.model.RegisterTransitGatewayRequest;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistrationState;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistrationStateReason;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.UNRECOGNIZED_STATE_MESSAGE;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.TIMED_OUT_MESSAGE;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.CALlBACK_PERIOD_30_SECONDS;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.MAX_CALLBACK_COUNT;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.FAILED_STATE_MESSAGE;
import static software.amazon.cloudformation.proxy.OperationStatus.IN_PROGRESS;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;


public class CreateHandler extends BaseHandler<CallbackContext> {
    /**
     * Create handler for TransitGatewayRegistration(TGW Registration).
     * 1. if this is the first time the create handler was invoked, then call NetworkManager api to register TransitGateway an IN_PROGRESS status
     * 2. otherwise, call NetworkManager API getTransitGatewayRegistrations
     *    if state is pending, callback every 30s until TransitGatewayRegistrationState is available
     */
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
            int remainingRetryCount = MAX_CALLBACK_COUNT;
            // Call NetworkManager api to register TransitGateway
            if (callbackContext == null) {
                registerTransitGateway(client, model, proxy);
                logger.log(String.format("%s [%s] creation pending", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModel(model)
                        .status(IN_PROGRESS)
                        .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                        .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                        .build();
            }
            // Refresh remaining retry count
            if (callbackContext != null) {
                remainingRetryCount = callbackContext.getRemainingRetryCount();
                if (remainingRetryCount == 0) {
                    throw new RuntimeException(TIMED_OUT_MESSAGE);
                }
                remainingRetryCount--;
            }

            // Call NetworkManager api to verify creation status
            // If state is pending callback every 30s until TransitGatewayRegistrationState is available
            final GetTransitGatewayRegistrationsResponse getTransitGatewayRegistrationsResponse = Utils.getTransitGatewayRegistrations(client, model, proxy);
            if (getTransitGatewayRegistrationsResponse.transitGatewayRegistrations().isEmpty()) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString());
            }
            final TransitGatewayRegistrationStateReason stateReason = getTransitGatewayRegistrationsResponse.transitGatewayRegistrations().get(0).state();
            final TransitGatewayRegistrationState stateCode = stateReason.code();
            switch (stateCode) {
                case AVAILABLE:
                    logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(SUCCESS)
                            .build();
                case PENDING:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(IN_PROGRESS)
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .build();
                case FAILED:
                    throw new CfnGeneralServiceException(String.format(FAILED_STATE_MESSAGE, stateCode, stateReason.message()));
                default:
                    throw new RuntimeException(String.format(UNRECOGNIZED_STATE_MESSAGE, stateCode, stateReason.message()));
            }

        } catch (final ValidationException e){
            return ProgressEvent.failed(null, null, HandlerErrorCode.AlreadyExists, e.getMessage());
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

    }

    private void registerTransitGateway(final NetworkManagerClient client,
                                        final ResourceModel model,
                                        final AmazonWebServicesClientProxy proxy) {
        final RegisterTransitGatewayRequest registerTransitGatewayRequest =
                RegisterTransitGatewayRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .transitGatewayArn(model.getTransitGatewayArn())
                        .build();
        proxy.injectCredentialsAndInvokeV2(registerTransitGatewayRequest, client::registerTransitGateway);
    }
}
