# AWS::NetworkManager::CoreNetwork CoreNetworkSegment

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#name" title="Name">Name</a>" : <i>String</i>,
    "<a href="#edgelocations" title="EdgeLocations">EdgeLocations</a>" : <i>[ String, ... ]</i>,
    "<a href="#sharedsegments" title="SharedSegments">SharedSegments</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#name" title="Name">Name</a>: <i>String</i>
<a href="#edgelocations" title="EdgeLocations">EdgeLocations</a>: <i>
      - String</i>
<a href="#sharedsegments" title="SharedSegments">SharedSegments</a>: <i>
      - String</i>
</pre>

## Properties

#### Name

Name of segment

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### EdgeLocations

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SharedSegments

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
