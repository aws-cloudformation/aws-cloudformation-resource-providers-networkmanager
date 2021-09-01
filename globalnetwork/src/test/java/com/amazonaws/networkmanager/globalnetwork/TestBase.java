package com.amazonaws.networkmanager.globalnetwork;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.services.networkmanager.model.GlobalNetwork;
import software.amazon.awssdk.services.networkmanager.model.Tag;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import java.util.*;

import static org.mockito.Mockito.mock;

/**
 * Test super class containing builder methods for various RPDK and SDK test objects
 */
public class TestBase {

    protected final static String GLOBAL_NETWORK_ARN = "arn:aws:networkmanager::039868373529:global-network/global-network-0b84f0efa26317dda";
    protected final static String GLOBAL_NETWORK_ID = "global-network-0b84f0efa26317dda";
    protected final static String DESCRIPTION = "description";
    protected final static String TAG_KEY_1 = "testKey1";
    protected final static String TAG_KEY_2 = "testKey2";
    protected final static String TAG_VALUE_1 = "testKey1";
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

    protected ResourceModel buildCreateResourceModel() {
        return ResourceModel.builder()
                .description(DESCRIPTION)
                .tags(createTagsWithOneTag())
                .build();
    }

    protected ResourceModel buildResourceModelWithOnlyId() {
        return ResourceModel.builder()
                .id(GLOBAL_NETWORK_ID)
                .build();
    }

    protected ResourceModel buildUpdateResourceModel() {
        return ResourceModel.builder()
                .id(GLOBAL_NETWORK_ID)
                .description(DESCRIPTION)
                .tags(createTagsWithOneTag())
                .build();
    }

    protected GlobalNetwork buildGlobalNetwork() {
        return GlobalNetwork.builder()
                .description(DESCRIPTION)
                .globalNetworkArn(GLOBAL_NETWORK_ARN)
                .tags(createNetworkManagerTagsWithTwoTags())
                .globalNetworkId(GLOBAL_NETWORK_ID)
                .build();
    }

    protected List<com.amazonaws.networkmanager.globalnetwork.Tag> createTagsWithOneTag() {
        List<com.amazonaws.networkmanager.globalnetwork.Tag> tags = new ArrayList<>();
        com.amazonaws.networkmanager.globalnetwork.Tag t1 = new com.amazonaws.networkmanager.globalnetwork.Tag(TAG_KEY_1, TAG_VALUE_1);
        tags.add(t1);
        return tags;
    }

    protected Collection<Tag> createNetworkManagerTagsWithOneTag() {
        final List<Tag> tags = new ArrayList<>();
        final Tag t1 = software.amazon.awssdk.services.networkmanager.model.Tag.builder().key(TAG_KEY_1).value(TAG_VALUE_1).build();
        tags.add(t1);
        return tags;
    }

    protected Collection<Tag> createNetworkManagerTagsWithTwoTags() {
        final List<Tag> tags = new ArrayList<>();
        final Tag t1 = software.amazon.awssdk.services.networkmanager.model.Tag.builder().key(TAG_KEY_1).value(TAG_VALUE_1).build();
        final Tag t2 = software.amazon.awssdk.services.networkmanager.model.Tag.builder().key(TAG_KEY_2).value(TAG_VALUE_2).build();
        tags.add(t1);
        tags.add(t2);
        return tags;
    }

    protected Map<String, String> createDesiredResourceTags() {
        final Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_2, TAG_VALUE_2);
        return tags;
    }

    protected List<com.amazonaws.networkmanager.globalnetwork.Tag> createMergedTags() {
        final List<com.amazonaws.networkmanager.globalnetwork.Tag> tags = new ArrayList<>();
        final com.amazonaws.networkmanager.globalnetwork.Tag t1 = new com.amazonaws.networkmanager.globalnetwork.Tag(TAG_KEY_1, TAG_VALUE_1);
        tags.add(t1);
        final com.amazonaws.networkmanager.globalnetwork.Tag t2 = new com.amazonaws.networkmanager.globalnetwork.Tag(TAG_KEY_2, TAG_VALUE_2);
        tags.add(t2);
        return tags;
    }

}
