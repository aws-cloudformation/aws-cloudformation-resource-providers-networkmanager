package com.amazonaws.networkmanager.transitgatewayregistration;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistration;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistrationState;
import software.amazon.awssdk.services.networkmanager.model.TransitGatewayRegistrationStateReason;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import static org.mockito.Mockito.mock;

/**
 * Test super class containing builder methods for various RPDK and SDK test objects
 */
public class TestBase {

    protected final static String GLOBAL_NETWORK_ID = "global-network-0da702142c70e2699";
    protected final static String TRANSIT_GATEWAY_ARN = "arn:aws:ec2:us-east-1:039868373529:transit-gateway/tgw-0339565f8509c0b39";
    protected final static String FAILED_MESSAGE = "TransitGateway arn is not valid";

    protected CallbackContext context;

    @Mock
    protected AmazonWebServicesClientProxy proxy;

    @Mock
    protected Logger logger;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        context = null;
    }

    protected ResourceModel buildResourceModel() {
        return ResourceModel.builder()
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .transitGatewayArn(TRANSIT_GATEWAY_ARN)
                .build();
    }

    protected TransitGatewayRegistration buildAvailableTransitGatewayRegistration() {
        return TransitGatewayRegistration.builder()
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .transitGatewayArn(TRANSIT_GATEWAY_ARN)
                .state(TransitGatewayRegistrationStateReason.builder().code(TransitGatewayRegistrationState.AVAILABLE).build())
                .build();
    }

    protected TransitGatewayRegistration buildFailedTransitGatewayRegistration() {
        return TransitGatewayRegistration.builder()
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .transitGatewayArn(TRANSIT_GATEWAY_ARN)
                .state(TransitGatewayRegistrationStateReason.builder().code(TransitGatewayRegistrationState.FAILED).message(FAILED_MESSAGE).build())
                .build();
    }

    protected TransitGatewayRegistration buildDeletingTransitGatewayRegistration() {
        return TransitGatewayRegistration.builder()
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .transitGatewayArn(TRANSIT_GATEWAY_ARN)
                .state(TransitGatewayRegistrationStateReason.builder().code(TransitGatewayRegistrationState.DELETING).message(FAILED_MESSAGE).build())
                .build();
    }

    protected TransitGatewayRegistration buildPendingTransitGatewayRegistration() {
        return TransitGatewayRegistration.builder()
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .transitGatewayArn(TRANSIT_GATEWAY_ARN)
                .state(TransitGatewayRegistrationStateReason.builder().code(TransitGatewayRegistrationState.PENDING).message(FAILED_MESSAGE).build())
                .build();
    }

}
