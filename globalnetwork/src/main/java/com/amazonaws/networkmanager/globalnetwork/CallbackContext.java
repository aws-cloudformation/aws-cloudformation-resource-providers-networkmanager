package com.amazonaws.networkmanager.globalnetwork;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@JsonPOJOBuilder(withPrefix = "")
public class CallbackContext {
    @JsonPOJOBuilder(withPrefix = "")
    public static class CallbackContextBuilder {
    }
}
