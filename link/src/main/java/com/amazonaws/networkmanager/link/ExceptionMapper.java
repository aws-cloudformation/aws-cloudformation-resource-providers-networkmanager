package com.amazonaws.networkmanager.link;


import software.amazon.awssdk.services.networkmanager.model.*;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public final class ExceptionMapper {
    private ExceptionMapper() {
    }

    /**
     * Translates the NetworkManager's client exception to a CFN HandlerErrorCode.
     */
    public static HandlerErrorCode mapToHandlerErrorCode(final Exception exception) {
        if (exception instanceof AccessDeniedException) {
            return HandlerErrorCode.AccessDenied;
        } else if (exception instanceof ConflictException) {
            return HandlerErrorCode.ResourceConflict;
        } else if (exception instanceof ServiceQuotaExceededException) {
            return HandlerErrorCode.ServiceLimitExceeded;
        } else if (exception instanceof ThrottlingException) {
            return HandlerErrorCode.Throttling;
        } else if (exception instanceof ValidationException) {
            return HandlerErrorCode.InvalidRequest;
        } else if (exception instanceof InternalServerException) {
            return HandlerErrorCode.ServiceInternalError;
        } else if (exception instanceof ResourceNotFoundException) {
            return HandlerErrorCode.NotFound;
        } else {
            return HandlerErrorCode.InternalFailure;
        }
    }
}
