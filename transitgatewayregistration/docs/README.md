# AWS::NetworkManager::TransitGatewayRegistration

The AWS::NetworkManager::TransitGatewayRegistration type registers a transit gateway in your global network. The transit gateway can be in any AWS Region, but it must be owned by the same AWS account that owns the global network. You cannot register a transit gateway in more than one global network.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::NetworkManager::TransitGatewayRegistration",
    "Properties" : {
        "<a href="#globalnetworkid" title="GlobalNetworkId">GlobalNetworkId</a>" : <i>String</i>,
        "<a href="#transitgatewayarn" title="TransitGatewayArn">TransitGatewayArn</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::NetworkManager::TransitGatewayRegistration
Properties:
    <a href="#globalnetworkid" title="GlobalNetworkId">GlobalNetworkId</a>: <i>String</i>
    <a href="#transitgatewayarn" title="TransitGatewayArn">TransitGatewayArn</a>: <i>String</i>
</pre>

## Properties

#### GlobalNetworkId

The ID of the global network.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TransitGatewayArn

The Amazon Resource Name (ARN) of the transit gateway.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)
