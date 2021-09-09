package com.amazonaws.networkmanager.transitgatewayregistration;

import software.amazon.awssdk.services.networkmanager.NetworkManagerClient;
import software.amazon.awssdk.services.networkmanager.model.DeregisterTransitGatewayRequest;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistration;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistrationState;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistrationStateReason;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.MAX_CALLBACK_COUNT;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.CALlBACK_PERIOD_30_SECONDS;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.TIMED_OUT_MESSAGE;
import static com.amazonaws.networkmanager.transitgatewayregistration.Utils.UNRECOGNIZED_STATE_MESSAGE;
import static software.amazon.cloudformation.proxy.OperationStatus.IN_PROGRESS;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;


public class DeleteHandler extends BaseHandler<CallbackContext> {
    /**
     * Delete handler for TransitGatewayRegistration(TGW Registration).
     * Call NetworkManager API getTransitGatewayRegistrations
     * 1. if no TGW Registration returned
     *      1.1 if this is the first time the delete handler was invoked, handler returns a FAILED status with a NotFound error code
     *      1.2 if this is a callback from previous deleteRequest, handler returns a SUCCESS status
     * 2. if TGW Registration is in AVAILABLE state, call NetworkManager API to deregister TGW, handler returns an IN_PROGRESS status
     * 3. if TGW Registration is in DELETING/PENDING state, callback every 30s until the TransitGateway is deregistered, handler returns an IN_PROGRESS status
     * 4. if TGW Registration is in FAILED/DELETED state, handler returns a SUCCESS status because FAILED/DELETED is a terminated state
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
            // Refresh remaining retry count
            int remainingRetryCount = MAX_CALLBACK_COUNT;
            if (callbackContext != null) {
                remainingRetryCount = callbackContext.getRemainingRetryCount();
                if (remainingRetryCount == 0) {
                    throw new RuntimeException(TIMED_OUT_MESSAGE);
                }
                remainingRetryCount--;
            }
            // Call NetworkManager API getTransitGatewayRegistrations to verify registration status
            final List<TransitGatewayRegistration> transitGatewayRegistrations = Utils.getTransitGatewayRegistrations(client, model, proxy).transitGatewayRegistrations();

            // if no TGW Registration returned
            if (transitGatewayRegistrations.isEmpty()) {
                // if this is the first time the delete handler was invoked, return a NotFound error code
                if (callbackContext == null) {
                    throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString());
                } else {
                    // if this is a callback from a previous deletion request, return deletion success
                    logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .status(SUCCESS)
                            .build();
                }
            }
            final TransitGatewayRegistrationStateReason stateReason = transitGatewayRegistrations.get(0).state();
            final TransitGatewayRegistrationState stateCode = stateReason.code();
            switch (stateCode) {
                case AVAILABLE: // call NetworkManager API to deregister TransitGateway from the global network
                    deregisterTransitGateway(client, model, proxy);
                    logger.log(String.format("%s [%s] deletion in progress", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(request.getDesiredResourceState())
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .status(IN_PROGRESS)
                            .build();
                case PENDING:
                case DELETING: // callback every 30s until the TGW is deregistered
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(request.getDesiredResourceState())
                            .status(IN_PROGRESS)
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .build();
                case FAILED:
                case DELETED: // return a deletion success because FAILED/DELETED is a terminated state
                    logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .status(SUCCESS)
                            .build();
                default:
                    throw new RuntimeException(String.format(UNRECOGNIZED_STATE_MESSAGE, stateCode, stateReason.message()));
            }
        } catch (final Exception e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

    }

    private void deregisterTransitGateway(final NetworkManagerClient client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {
        final DeregisterTransitGatewayRequest deregisterTransitGatewayRequest =
                DeregisterTransitGatewayRequest.builder()
                        .globalNetworkId(model.getGlobalNetworkId())
                        .transitGatewayArn(model.getTransitGatewayArn())
                        .build();
        proxy.injectCredentialsAndInvokeV2(deregisterTransitGatewayRequest, client::deregisterTransitGateway);
    }
}
