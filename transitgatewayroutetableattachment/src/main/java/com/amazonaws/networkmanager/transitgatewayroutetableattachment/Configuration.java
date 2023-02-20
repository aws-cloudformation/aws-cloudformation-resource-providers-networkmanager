package com.amazonaws.networkmanager.transitgatewayroutetableattachment;

import java.util.Map;
import java.util.stream.Collectors;

public class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-networkmanager-transitgatewayroutetableattachment.json");
    }

    @Override
    public Map<String, String> resourceDefinedTags(final ResourceModel resourceModel) {
        if (resourceModel.getTags() == null) {
            return null;
        } else {
            return resourceModel.getTags().stream().collect(Collectors.toMap(tag -> tag.getKey(), tag -> tag.getValue()));
        }
    }
}
