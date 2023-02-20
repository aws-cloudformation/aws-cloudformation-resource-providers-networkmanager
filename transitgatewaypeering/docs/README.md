# AWS::NetworkManager::TransitGatewayPeering

AWS::NetworkManager::TransitGatewayPeering Resoruce Type.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::TransitGatewayPeering",
    "Properties" : {
        "<a href="#corenetworkid" title="CoreNetworkId">CoreNetworkId</a>" : <i>String</i>,
        "<a href="#transitgatewayarn" title="TransitGatewayArn">TransitGatewayArn</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::TransitGatewayPeering
Properties:
    <a href="#corenetworkid" title="CoreNetworkId">CoreNetworkId</a>: <i>String</i>
    <a href="#transitgatewayarn" title="TransitGatewayArn">TransitGatewayArn</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### CoreNetworkId

The Id of the core network that you want to peer a transit gateway to.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TransitGatewayArn

The ARN (Amazon Resource Name) of the transit gateway that you will peer to a core network

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

An array of key-value pairs to apply to this resource.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the PeeringId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### CoreNetworkArn

The ARN (Amazon Resource Name) of the core network that you want to peer a transit gateway to.

#### PeeringId

The Id of the transit gateway peering

#### State

The state of the transit gateway peering

#### PeeringType

Peering type (TransitGatewayPeering)

#### OwnerAccountId

Peering owner account Id

#### EdgeLocation

The location of the transit gateway peering

#### ResourceArn

The ARN (Amazon Resource Name) of the resource that you will peer to a core network

#### CreatedAt

The creation time of the transit gateway peering

#### TransitGatewayPeeringAttachmentId

The ID of the TransitGatewayPeeringAttachment
