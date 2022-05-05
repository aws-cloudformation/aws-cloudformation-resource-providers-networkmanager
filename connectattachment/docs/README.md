# AWS::NetworkManager::ConnectAttachment

AWS::NetworkManager::ConnectAttachment Resource Type Definition

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::ConnectAttachment",
    "Properties" : {
        "<a href="#corenetworkid" title="CoreNetworkId">CoreNetworkId</a>" : <i>String</i>,
        "<a href="#edgelocation" title="EdgeLocation">EdgeLocation</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#transportattachmentid" title="TransportAttachmentId">TransportAttachmentId</a>" : <i>String</i>,
        "<a href="#options" title="Options">Options</a>" : <i><a href="connectattachmentoptions.md">ConnectAttachmentOptions</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::ConnectAttachment
Properties:
    <a href="#corenetworkid" title="CoreNetworkId">CoreNetworkId</a>: <i>String</i>
    <a href="#edgelocation" title="EdgeLocation">EdgeLocation</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#transportattachmentid" title="TransportAttachmentId">TransportAttachmentId</a>: <i>String</i>
    <a href="#options" title="Options">Options</a>: <i><a href="connectattachmentoptions.md">ConnectAttachmentOptions</a></i>
</pre>

## Properties

#### CoreNetworkId

ID of the CoreNetwork that the attachment will be attached to.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### EdgeLocation

Edge location of the attachment.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TransportAttachmentId

Id of transport attachment

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Options

Connect attachment options for protocol

_Required_: No

_Type_: <a href="connectattachmentoptions.md">ConnectAttachmentOptions</a>

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

The type of attachment.

#### State

State of the attachment.

#### ResourceArn

The attachment resource ARN.

#### AttachmentId

The ID of the attachment.

#### OwnerAccountId

The ID of the attachment account owner.

#### AttachmentPolicyRuleNumber

The policy rule number associated with the attachment.

#### ProposedSegmentChange

A key-value pair to associate with a resource.

#### SegmentName

The name of the segment attachment.
