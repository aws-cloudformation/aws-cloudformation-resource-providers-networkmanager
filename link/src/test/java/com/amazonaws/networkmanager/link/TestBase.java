package com.amazonaws.networkmanager.link;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.services.networkmanager.model.Link;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Test super class containing builder methods for various RPDK and SDK test objects
 */
public class TestBase {

    protected final static String GLOBAL_NETWORK_ID = "global-network-0da702142c70e2699";
    protected final static String LINK_ID = "link-0d6e92168b1f83b83";
    protected final static String LINK_ARN = "arn:aws:networkmanager::039868373529:link/global-network-0da702142c70e2699/link-0d6e92168b1f83b83";
    protected final static String DESCRIPTION = "description";
    protected final static String TAG_KEY_1 = "testKey1";
    protected final static String TAG_VALUE_1 = "testKey1";
    protected final static String TAG_KEY_2 = "testKey2";
    protected final static String TAG_VALUE_2 = "testKey2";

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
                .linkArn(LINK_ARN)
                .linkId(LINK_ID)
                .bandwidth(Bandwidth.builder().build())
                .description(DESCRIPTION)
                .tags(createCloudformationTags())
                .bandwidth(Bandwidth.builder().build())
                .build();
    }

    protected ResourceModel buildSimpleResourceModel() {
        return ResourceModel.builder()
                .linkId(LINK_ID)
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .build();
    }

    protected Link buildLink() {
        return Link.builder()
                .description(DESCRIPTION)
                .linkArn(LINK_ARN)
                .linkId(LINK_ID)
                .bandwidth(Utils.transformBandwidth(Bandwidth.builder().build()))
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .tags(createNetworkManagerTags())
                .build();
    }

    protected Link buildSimpleLink() {
        return Link.builder()
                .linkId(LINK_ID)
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .build();
    }

    protected List<com.amazonaws.networkmanager.link.Tag> createCloudformationTags() {
        final List<com.amazonaws.networkmanager.link.Tag> tags = new ArrayList<>();
        final com.amazonaws.networkmanager.link.Tag t1 = new com.amazonaws.networkmanager.link.Tag(TAG_KEY_1, TAG_VALUE_1);
        tags.add(t1);
        return tags;
    }

    protected List<Tag> createNetworkManagerTags() {
        final List<Tag> tags = new ArrayList<>();
        final Tag t1 = Tag.builder().key(TAG_KEY_1).value(TAG_VALUE_1).build();
        tags.add(t1);
        return tags;
    }

    protected Map<String, String> createDesiredResourceTags() {
        final Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_2, TAG_VALUE_2);
        return tags;
    }

    protected List<com.amazonaws.networkmanager.link.Tag> createMergedTags() {
        final List<com.amazonaws.networkmanager.link.Tag> tags = new ArrayList<>();
        final com.amazonaws.networkmanager.link.Tag t1 = new com.amazonaws.networkmanager.link.Tag(TAG_KEY_1, TAG_VALUE_1);
        tags.add(t1);
        final com.amazonaws.networkmanager.link.Tag t2 = new com.amazonaws.networkmanager.link.Tag(TAG_KEY_2, TAG_VALUE_2);
        tags.add(t2);
        return tags;
    }

}
