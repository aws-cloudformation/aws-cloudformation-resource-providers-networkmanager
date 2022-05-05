# AWS::NetworkManager::VpcAttachment

AWS::NetworkManager::VpcAttachment Resoruce Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::VpcAttachment",
    "Properties" : {
        "<a href="#corenetworkid" title="CoreNetworkId">CoreNetworkId</a>" : <i>String</i>,
        "<a href="#vpcarn" title="VpcArn">VpcArn</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#subnetarns" title="SubnetArns">SubnetArns</a>" : <i>[ String, ... ]</i>,
        "<a href="#options" title="Options">Options</a>" : <i><a href="vpcoptions.md">VpcOptions</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::VpcAttachment
Properties:
    <a href="#corenetworkid" title="CoreNetworkId">CoreNetworkId</a>: <i>String</i>
    <a href="#vpcarn" title="VpcArn">VpcArn</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#subnetarns" title="SubnetArns">SubnetArns</a>: <i>
      - String</i>
    <a href="#options" title="Options">Options</a>: <i><a href="vpcoptions.md">VpcOptions</a></i>
</pre>

## Properties

#### CoreNetworkId

The ID of a core network for the VPC attachment.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### VpcArn

The ARN of the VPC.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SubnetArns

Subnet Arn list

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Options

Vpc options of the attachment.

_Required_: No

_Type_: <a href="vpcoptions.md">VpcOptions</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the AttachmentId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### CoreNetworkArn

The ARN of a core network for the VPC attachment.

#### CreatedAt

Creation time of the attachment.

#### UpdatedAt

Last update time of the attachment.

#### AttachmentType

Attachment type.

#### State

State of the attachment.

#### AttachmentId

Id of the attachment.

#### OwnerAccountId

Owner account of the attachment.

#### EdgeLocation

The Region where the edge is located.

#### AttachmentPolicyRuleNumber

The policy rule number associated with the attachment.

#### ProposedSegmentChange

The attachment to move from one segment to another.

#### SegmentName

The name of the segment attachment..

#### ResourceArn

The ARN of the Resource.
