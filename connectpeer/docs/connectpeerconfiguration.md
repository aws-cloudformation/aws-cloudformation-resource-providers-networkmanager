# AWS::NetworkManager::ConnectPeer ConnectPeerConfiguration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#corenetworkaddress" title="CoreNetworkAddress">CoreNetworkAddress</a>" : <i>String</i>,
    "<a href="#peeraddress" title="PeerAddress">PeerAddress</a>" : <i>String</i>,
    "<a href="#insidecidrblocks" title="InsideCidrBlocks">InsideCidrBlocks</a>" : <i>[ String, ... ]</i>,
    "<a href="#protocol" title="Protocol">Protocol</a>" : <i>String</i>,
    "<a href="#bgpconfigurations" title="BgpConfigurations">BgpConfigurations</a>" : <i>[ <a href="connectpeerbgpconfiguration.md">ConnectPeerBgpConfiguration</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#corenetworkaddress" title="CoreNetworkAddress">CoreNetworkAddress</a>: <i>String</i>
<a href="#peeraddress" title="PeerAddress">PeerAddress</a>: <i>String</i>
<a href="#insidecidrblocks" title="InsideCidrBlocks">InsideCidrBlocks</a>: <i>
      - String</i>
<a href="#protocol" title="Protocol">Protocol</a>: <i>String</i>
<a href="#bgpconfigurations" title="BgpConfigurations">BgpConfigurations</a>: <i>
      - <a href="connectpeerbgpconfiguration.md">ConnectPeerBgpConfiguration</a></i>
</pre>

## Properties

#### CoreNetworkAddress

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PeerAddress

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InsideCidrBlocks

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Protocol

Tunnel protocol type (Only support GRE for now)

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### BgpConfigurations

_Required_: No

_Type_: List of <a href="connectpeerbgpconfiguration.md">ConnectPeerBgpConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
