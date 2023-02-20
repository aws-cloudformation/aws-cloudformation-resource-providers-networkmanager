package com.amazonaws.networkmanager.transitgatewaypeering;

import java.util.Map;
import java.util.stream.Collectors;

public class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-networkmanager-transitgatewaypeering.json");
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
