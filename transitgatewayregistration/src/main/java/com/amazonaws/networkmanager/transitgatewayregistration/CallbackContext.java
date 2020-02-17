package com.amazonaws.networkmanager.transitgatewayregistration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CallbackContext {
    private boolean actionStarted;
    private int remainingRetryCount;
}
