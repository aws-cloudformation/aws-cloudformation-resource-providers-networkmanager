package com.amazonaws.networkmanager.customergatewayassociation;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.services.networkmanager.model.CustomerGatewayAssociation;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import static org.mockito.Mockito.mock;

/**
 * Test super class containing builder methods for various RPDK and SDK test objects
 */
public class TestBase {

    protected final static String GLOBAL_NETWORK_ID = "global-network-0da702142c70e2699";
    protected final static String CUSTOMER_GATEWAY_ARN = "arn:aws:ec2:us-east-1:039868373529:customer-gateway/cgw-02c07b580a494a36e";
    protected final static String LINK_ID = "customerGateway-0d6e92168b1f83b83";
    protected final static String DEVICE_ID = "device-0d6e92168b1f83b83";

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
                .customerGatewayArn(CUSTOMER_GATEWAY_ARN)
                .deviceId(DEVICE_ID)
                .linkId(LINK_ID)
                .build();
    }

    protected CustomerGatewayAssociation buildCustomerGatewayAssociation() {
        return CustomerGatewayAssociation.builder()
                .customerGatewayArn(CUSTOMER_GATEWAY_ARN)
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .deviceId(DEVICE_ID)
                .linkId(LINK_ID)
                .build();
    }

}
