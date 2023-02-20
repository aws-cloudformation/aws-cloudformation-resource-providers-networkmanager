# AWS::NetworkManager::TransitGatewayRouteTableAttachment

AWS::NetworkManager::TransitGatewayRouteTableAttachment Resource Type definition.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::TransitGatewayRouteTableAttachment",
    "Properties" : {
        "<a href="#peeringid" title="PeeringId">PeeringId</a>" : <i>String</i>,
        "<a href="#transitgatewayroutetablearn" title="TransitGatewayRouteTableArn">TransitGatewayRouteTableArn</a>" : <i>String</i>,
        "<a href="#proposedsegmentchange" title="ProposedSegmentChange">ProposedSegmentChange</a>" : <i><a href="proposedsegmentchange.md">ProposedSegmentChange</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::TransitGatewayRouteTableAttachment
Properties:
    <a href="#peeringid" title="PeeringId">PeeringId</a>: <i>String</i>
    <a href="#transitgatewayroutetablearn" title="TransitGatewayRouteTableArn">TransitGatewayRouteTableArn</a>: <i>String</i>
    <a href="#proposedsegmentchange" title="ProposedSegmentChange">ProposedSegmentChange</a>: <i><a href="proposedsegmentchange.md">ProposedSegmentChange</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### PeeringId

The Id of peering between transit gateway and core network.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TransitGatewayRouteTableArn

The Arn of transit gateway route table.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### ProposedSegmentChange

The attachment to move from one segment to another.

_Required_: No

_Type_: <a href="proposedsegmentchange.md">ProposedSegmentChange</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the AttachmentId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### CoreNetworkArn

The ARN of a core network for the VPC attachment.

#### CoreNetworkId

The ID of a core network where you're creating a site-to-site VPN attachment.

#### CreatedAt

Creation time of the attachment.

#### UpdatedAt

Last update time of the attachment.

#### AttachmentType

The type of attachment.

#### State

The state of the attachment.

#### ResourceArn

The ARN of the Resource.

#### AttachmentId

The ID of the attachment.

#### OwnerAccountId

Owner account of the attachment.

#### EdgeLocation

The Region where the edge is located.

#### AttachmentPolicyRuleNumber

The policy rule number associated with the attachment.

#### SegmentName

The name of the segment that attachment is in.
