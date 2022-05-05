# AWS::NetworkManager::ConnectAttachment ProposedSegmentChange

A key-value pair to associate with a resource.

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

Proposed tags for the Segment.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AttachmentPolicyRuleNumber

New policy rule number of the attachment

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SegmentName

Proposed segment name

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
