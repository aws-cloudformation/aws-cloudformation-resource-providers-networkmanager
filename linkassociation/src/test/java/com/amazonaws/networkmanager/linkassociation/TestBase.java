package com.amazonaws.networkmanager.linkassociation;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.services.networkmanager.model.LinkAssociation;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import static org.mockito.Mockito.mock;

/**
 * Test super class containing builder methods for various RPDK and SDK test objects
 */
public class TestBase {

    protected final static String GLOBAL_NETWORK_ID = "global-network-0da702142c70e2699";
    protected final static String LINK_ID = "link-0d6e92168b1f83b83";
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
                .linkId(LINK_ID)
                .deviceId(DEVICE_ID)
                .build();
    }

    protected LinkAssociation buildLinkAssociation() {
        return LinkAssociation.builder()
                .deviceId(DEVICE_ID)
                .linkId(LINK_ID)
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .build();
    }

}
