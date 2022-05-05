# AWS::NetworkManager::CoreNetwork

AWS::NetworkManager::CoreNetwork Resource Type Definition.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::CoreNetwork",
    "Properties" : {
        "<a href="#globalnetworkid" title="GlobalNetworkId">GlobalNetworkId</a>" : <i>String</i>,
        "<a href="#policydocument" title="PolicyDocument">PolicyDocument</a>" : <i>String</i>,
        "<a href="#description" title="Description">Description</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::CoreNetwork
Properties:
    <a href="#globalnetworkid" title="GlobalNetworkId">GlobalNetworkId</a>: <i>String</i>
    <a href="#policydocument" title="PolicyDocument">PolicyDocument</a>: <i>String</i>
    <a href="#description" title="Description">Description</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### GlobalNetworkId

The ID of the global network that your core network is a part of.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### PolicyDocument

Live policy document for the core network

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Description

The description of core network

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

The tags for the global network.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the CoreNetworkId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### OwnerAccount

Owner of the core network

#### CoreNetworkId

The Id of core network

#### CoreNetworkArn

The ARN (Amazon resource name) of core network

#### CreatedAt

The creation time of core network

#### State

The state of core network

#### Segments

The segments within a core network.

#### Edges

The edges within a core network.
