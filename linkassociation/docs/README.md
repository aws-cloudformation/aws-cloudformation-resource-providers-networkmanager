# AWS::NetworkManager::LinkAssociation

The AWS::NetworkManager::LinkAssociation type associates a link to a device. The device and link must be in the same global network and the same site.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::LinkAssociation",
    "Properties" : {
        "<a href="#globalnetworkid" title="GlobalNetworkId">GlobalNetworkId</a>" : <i>String</i>,
        "<a href="#deviceid" title="DeviceId">DeviceId</a>" : <i>String</i>,
        "<a href="#linkid" title="LinkId">LinkId</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::LinkAssociation
Properties:
    <a href="#globalnetworkid" title="GlobalNetworkId">GlobalNetworkId</a>: <i>String</i>
    <a href="#deviceid" title="DeviceId">DeviceId</a>: <i>String</i>
    <a href="#linkid" title="LinkId">LinkId</a>: <i>String</i>
</pre>

## Properties

#### GlobalNetworkId

The ID of the global network.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### DeviceId

The ID of the device

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### LinkId

The ID of the link

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)
