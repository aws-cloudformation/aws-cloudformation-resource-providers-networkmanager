# AWS::NetworkManager::ConnectPeer

AWS::NetworkManager::ConnectPeer Resource Type Definition.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::ConnectPeer",
    "Properties" : {
        "<a href="#peeraddress" title="PeerAddress">PeerAddress</a>" : <i>String</i>,
        "<a href="#corenetworkaddress" title="CoreNetworkAddress">CoreNetworkAddress</a>" : <i>String</i>,
        "<a href="#bgpoptions" title="BgpOptions">BgpOptions</a>" : <i><a href="bgpoptions.md">BgpOptions</a></i>,
        "<a href="#insidecidrblocks" title="InsideCidrBlocks">InsideCidrBlocks</a>" : <i>[ String, ... ]</i>,
        "<a href="#connectattachmentid" title="ConnectAttachmentId">ConnectAttachmentId</a>" : <i>String</i>,
        "<a href="#configuration" title="Configuration">Configuration</a>" : <i><a href="connectpeerconfiguration.md">ConnectPeerConfiguration</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::ConnectPeer
Properties:
    <a href="#peeraddress" title="PeerAddress">PeerAddress</a>: <i>String</i>
    <a href="#corenetworkaddress" title="CoreNetworkAddress">CoreNetworkAddress</a>: <i>String</i>
    <a href="#bgpoptions" title="BgpOptions">BgpOptions</a>: <i><a href="bgpoptions.md">BgpOptions</a></i>
    <a href="#insidecidrblocks" title="InsideCidrBlocks">InsideCidrBlocks</a>: <i>
      - String</i>
    <a href="#connectattachmentid" title="ConnectAttachmentId">ConnectAttachmentId</a>: <i>String</i>
    <a href="#configuration" title="Configuration">Configuration</a>: <i><a href="connectpeerconfiguration.md">ConnectPeerConfiguration</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### PeerAddress

The IP address of the Connect peer.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### CoreNetworkAddress

The IP address of a core network.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### BgpOptions

Bgp options

_Required_: No

_Type_: <a href="bgpoptions.md">BgpOptions</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### InsideCidrBlocks

The inside IP addresses used for a Connect peer configuration.

_Required_: No

_Type_: List of String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### ConnectAttachmentId

The ID of the attachment to connect.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Configuration

_Required_: No

_Type_: <a href="connectpeerconfiguration.md">ConnectPeerConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

An array of key-value pairs to apply to this resource.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the ConnectPeerId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ConnectPeerId

The ID of the Connect peer.

#### State

State of the connect peer.

#### CreatedAt

Connect peer creation time.

#### Configuration

Returns the <code>Configuration</code> value.

#### CoreNetworkId

The ID of the core network.

#### EdgeLocation

The Connect peer Regions where edges are located.
