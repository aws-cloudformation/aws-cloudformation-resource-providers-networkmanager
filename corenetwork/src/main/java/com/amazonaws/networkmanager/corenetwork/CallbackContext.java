package com.amazonaws.networkmanager.corenetwork;

import software.amazon.cloudformation.proxy.StdCallbackContext;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode(callSuper = true)
public class CallbackContext extends StdCallbackContext {
    public int attempts = -1;
    public int latestVersionId = Integer.MIN_VALUE;
    public int updatePolicyVersionId = Integer.MIN_VALUE;
    public boolean isPolicyUpdated = false;
}
