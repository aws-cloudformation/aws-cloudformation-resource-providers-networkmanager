# AWS::NetworkManager::CoreNetwork CoreNetworkEdge

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#edgelocation" title="EdgeLocation">EdgeLocation</a>" : <i>String</i>,
    "<a href="#asn" title="Asn">Asn</a>" : <i>Double</i>,
    "<a href="#insidecidrblocks" title="InsideCidrBlocks">InsideCidrBlocks</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#edgelocation" title="EdgeLocation">EdgeLocation</a>: <i>String</i>
<a href="#asn" title="Asn">Asn</a>: <i>Double</i>
<a href="#insidecidrblocks" title="InsideCidrBlocks">InsideCidrBlocks</a>: <i>
      - String</i>
</pre>

## Properties

#### EdgeLocation

The Region where a core network edge is located.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Asn

The ASN of a core network edge.

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InsideCidrBlocks

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
