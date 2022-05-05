# AWS::NetworkManager::ConnectPeer ConnectPeerBgpConfiguration

Bgp configuration for connect peer

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#corenetworkasn" title="CoreNetworkAsn">CoreNetworkAsn</a>" : <i>Double</i>,
    "<a href="#peerasn" title="PeerAsn">PeerAsn</a>" : <i>Double</i>,
    "<a href="#corenetworkaddress" title="CoreNetworkAddress">CoreNetworkAddress</a>" : <i>String</i>,
    "<a href="#peeraddress" title="PeerAddress">PeerAddress</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#corenetworkasn" title="CoreNetworkAsn">CoreNetworkAsn</a>: <i>Double</i>
<a href="#peerasn" title="PeerAsn">PeerAsn</a>: <i>Double</i>
<a href="#corenetworkaddress" title="CoreNetworkAddress">CoreNetworkAddress</a>: <i>String</i>
<a href="#peeraddress" title="PeerAddress">PeerAddress</a>: <i>String</i>
</pre>

## Properties

#### CoreNetworkAsn

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PeerAsn

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### CoreNetworkAddress

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PeerAddress

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
