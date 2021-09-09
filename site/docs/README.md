# AWS::NetworkManager::Site

The AWS::NetworkManager::Site type describes a site.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::Site",
    "Properties" : {
        "<a href="#description" title="Description">Description</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#globalnetworkid" title="GlobalNetworkId">GlobalNetworkId</a>" : <i>String</i>,
        "<a href="#location" title="Location">Location</a>" : <i><a href="location.md">Location</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::Site
Properties:
    <a href="#description" title="Description">Description</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#globalnetworkid" title="GlobalNetworkId">GlobalNetworkId</a>: <i>String</i>
    <a href="#location" title="Location">Location</a>: <i><a href="location.md">Location</a></i>
</pre>

## Properties

#### Description

The description of the site.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

The tags for the site.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### GlobalNetworkId

The ID of the global network.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Location

The location of the site

_Required_: No

_Type_: <a href="location.md">Location</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### SiteId

The ID of the site.

#### SiteArn

The Amazon Resource Name (ARN) of the site.
