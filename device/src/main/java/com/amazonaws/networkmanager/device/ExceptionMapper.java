package com.amazonaws.networkmanager.device;


import software.amazon.awssdk.services.networkmanager.model.AccessDeniedException;
import software.amazon.awssdk.services.networkmanager.model.ConflictException;
import software.amazon.awssdk.services.networkmanager.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.networkmanager.model.ThrottlingException;
import software.amazon.awssdk.services.networkmanager.model.ValidationException;
import software.amazon.awssdk.services.networkmanager.model.InternalServerException;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public final class ExceptionMapper {
    private ExceptionMapper() {
    }

    /**
     * Translates the Network Manager's client exception to a Cfn Handler Error Code.
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
