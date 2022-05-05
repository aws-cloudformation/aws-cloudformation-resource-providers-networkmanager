package com.amazonaws.networkmanager.vpcattachment.workflow;

import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.networkmanager.model.AccessDeniedException;
import software.amazon.awssdk.services.networkmanager.model.ConflictException;
import software.amazon.awssdk.services.networkmanager.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.networkmanager.model.ThrottlingException;
import software.amazon.awssdk.services.networkmanager.model.ValidationException;
import software.amazon.awssdk.services.networkmanager.model.InternalServerException;

public class ExceptionMapper {
    public static HandlerErrorCode mapToHandlerErrorCode(Exception e) {
        if (e instanceof AccessDeniedException) {
            return HandlerErrorCode.AccessDenied;
        } else if (e instanceof ConflictException) {
            return HandlerErrorCode.ResourceConflict;
        } else if (e instanceof ServiceQuotaExceededException) {
            return HandlerErrorCode.ServiceLimitExceeded;
        } else if (e instanceof ThrottlingException) {
            return HandlerErrorCode.Throttling;
        } else if (e instanceof ValidationException) {
            return HandlerErrorCode.InvalidRequest;
        } else if (e instanceof InternalServerException) {
            return HandlerErrorCode.ServiceInternalError;
        } else if (e instanceof ResourceNotFoundException) {
            return HandlerErrorCode.NotFound;
        } else {
            return HandlerErrorCode.InternalFailure;
        }
    }
}
