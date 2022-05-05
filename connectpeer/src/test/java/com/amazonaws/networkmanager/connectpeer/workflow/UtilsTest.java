package com.amazonaws.networkmanager.connectpeer.workflow;

import com.amazonaws.networkmanager.connectpeer.AbstractTestBase;
import com.amazonaws.networkmanager.connectpeer.ConnectPeerBgpConfiguration;
import com.amazonaws.networkmanager.connectpeer.ConnectPeerConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.Tag;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UtilsTest extends AbstractTestBase {

    @Test
    public void instance() {
        assertThat(new Utils().toString().contains("Utils")).isTrue();
    }

    @Test
    public void sdkTagsToCfnTags() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.getSdkTag());

        final List<com.amazonaws.networkmanager.connectpeer.Tag> cfnTags = Utils.sdkTagsToCfnTags(sdkTags);

        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTags.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTags.get(0).value());
    }

    @Test
    public void sdkTagsToCfnTagsWhenNull() {
        final List<com.amazonaws.networkmanager.connectpeer.Tag> cfnTags = Utils.sdkTagsToCfnTags(null);
        assertThat(cfnTags.size()).isEqualTo(0);
    }

    @Test
    public void cfnTagsToSdkTags() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.getSdkTag());
        final List<com.amazonaws.networkmanager.connectpeer.Tag> cfnTags = Utils.sdkTagsToCfnTags(sdkTags);

        List<Tag> sdkTagsConvertedBack = Utils.cfnTagsToSdkTags(cfnTags);

        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTags.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTags.get(0).value());
        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTagsConvertedBack.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTagsConvertedBack.get(0).value());
    }

    @Test
    public void cfnTagsToSdkTagsWhenNull() {
        List<Tag> sdkTagsConvertedBack = Utils.cfnTagsToSdkTags(null);
        assertThat(sdkTagsConvertedBack.size()).isEqualTo(0);
    }

    @Test
    public void tagDifference1() {
        com.amazonaws.networkmanager.connectpeer.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.connectpeer.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);
        tags1.add(MOCKS.getCfnTag());
        final List<com.amazonaws.networkmanager.connectpeer.Tag> tags2 = new ArrayList<>();
        tags2.add(sharedTag);
        tags2.add(MOCKS.getCfnTag());

        List<Tag> difference = Utils.tagsDifference(tags1, tags2);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags1.get(1).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags1.get(1).getValue());
    }

    @Test
    public void tagDifference2() {
        com.amazonaws.networkmanager.connectpeer.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.connectpeer.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);
        tags1.add(MOCKS.getCfnTag());
        final List<com.amazonaws.networkmanager.connectpeer.Tag> tags2 = new ArrayList<>();
        tags2.add(sharedTag);
        tags2.add(MOCKS.getCfnTag());

        List<Tag> difference = Utils.tagsDifference(tags2, tags1);

        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags2.get(1).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags2.get(1).getValue());
    }

    @Test
    public void tagDifference3() {
        com.amazonaws.networkmanager.connectpeer.Tag sharedTag = MOCKS.getCfnTag();
        final List<com.amazonaws.networkmanager.connectpeer.Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);

        final List<com.amazonaws.networkmanager.connectpeer.Tag> tags2 = new ArrayList<>();

        tags2.add(MOCKS.cfnTag(sharedTag.getKey(), "Something Else"));
        List<Tag> difference = Utils.tagsDifference(tags2, tags1);
        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags2.get(0).getKey());
        assertThat(difference.get(0).value()).isEqualTo(tags2.get(0).getValue());
        assertThat(difference.get(0).value()).isNotEqualTo(tags1.get(0).getValue());
    }

    @Test
    public void sdkConfigurationToCfnConfiguration() {
        software.amazon.awssdk.services.networkmanager.model.ConnectPeerConfiguration sdkConfiguration = MOCKS.getMockSdkConfiguration();

        ConnectPeerConfiguration cfnConfiguration = Utils.sdkConfigurationToCfnConfiguration(sdkConfiguration);

        assertThat(cfnConfiguration.getBgpConfigurations().get(0).getPeerAddress()).isEqualTo(sdkConfiguration.bgpConfigurations().get(0).peerAddress());
        assertThat(cfnConfiguration.getBgpConfigurations().get(1).getPeerAddress()).isEqualTo(sdkConfiguration.bgpConfigurations().get(1).peerAddress());
        assertThat(cfnConfiguration.getCoreNetworkAddress()).isEqualTo(sdkConfiguration.coreNetworkAddress());
        assertThat(cfnConfiguration.getInsideCidrBlocks()).isEqualTo(sdkConfiguration.insideCidrBlocks());
        assertThat(cfnConfiguration.getProtocol()).isEqualTo(sdkConfiguration.protocolAsString());
    }

    @Test
    public void sdkBgpConfigurationsToCfnBgpConfigurations() {
        List<software.amazon.awssdk.services.networkmanager.model.ConnectPeerBgpConfiguration> sdkBgpConfigurations = MOCKS.getMockSdkBgpConfigurations();

        List<ConnectPeerBgpConfiguration> cfnBgpConfigurations = Utils.sdkBgpConfigurationsToCfnBgpConfigurations(sdkBgpConfigurations);

        assertThat(cfnBgpConfigurations.get(0).getPeerAddress()).isEqualTo(sdkBgpConfigurations.get(0).peerAddress());
        assertThat(cfnBgpConfigurations.get(1).getPeerAddress()).isEqualTo(sdkBgpConfigurations.get(1).peerAddress());
        assertThat(cfnBgpConfigurations.get(0).getCoreNetworkAddress()).isEqualTo(sdkBgpConfigurations.get(0).coreNetworkAddress());
        assertThat(cfnBgpConfigurations.get(1).getCoreNetworkAddress()).isEqualTo(sdkBgpConfigurations.get(1).coreNetworkAddress());
        assertThat(cfnBgpConfigurations.get(0).getCoreNetworkAsn().longValue()).isEqualTo(sdkBgpConfigurations.get(0).coreNetworkAsn());
        assertThat(cfnBgpConfigurations.get(1).getCoreNetworkAsn().longValue()).isEqualTo(sdkBgpConfigurations.get(1).coreNetworkAsn());
        assertThat(cfnBgpConfigurations.get(0).getPeerAsn().longValue()).isEqualTo(sdkBgpConfigurations.get(0).peerAsn());
        assertThat(cfnBgpConfigurations.get(1).getPeerAsn().longValue()).isEqualTo(sdkBgpConfigurations.get(1).peerAsn());
    }
}
