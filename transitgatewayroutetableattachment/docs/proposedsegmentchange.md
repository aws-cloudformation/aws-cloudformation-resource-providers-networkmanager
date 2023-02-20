# AWS::NetworkManager::TransitGatewayRouteTableAttachment ProposedSegmentChange

The attachment to move from one segment to another.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
    "<a href="#attachmentpolicyrulenumber" title="AttachmentPolicyRuleNumber">AttachmentPolicyRuleNumber</a>" : <i>Integer</i>,
    "<a href="#segmentname" title="SegmentName">SegmentName</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
<a href="#attachmentpolicyrulenumber" title="AttachmentPolicyRuleNumber">AttachmentPolicyRuleNumber</a>: <i>Integer</i>
<a href="#segmentname" title="SegmentName">SegmentName</a>: <i>String</i>
</pre>

## Properties

#### Tags

The key-value tags that changed for the segment.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AttachmentPolicyRuleNumber

The rule number in the policy document that applies to this change.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SegmentName

The name of the segment to change.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
