package com.amazonaws.networkmanager.corenetwork.workflow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.networkmanager.model.AccessDeniedException;
import software.amazon.awssdk.services.networkmanager.model.ConflictException;
import software.amazon.awssdk.services.networkmanager.model.InternalServerException;
import software.amazon.awssdk.services.networkmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.networkmanager.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.networkmanager.model.ThrottlingException;
import software.amazon.awssdk.services.networkmanager.model.ValidationException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ExceptionMapperTest {

    @Test
    public void handleRequest_AccessDenied() {
        assertThat(ExceptionMapper.mapToHandlerErrorCode(
                AccessDeniedException.builder().message("access denied")
                        .build())).isEqualTo(HandlerErrorCode.AccessDenied);
    }

    @Test
    public void handleRequest_ConflictException() {
        assertThat(ExceptionMapper.mapToHandlerErrorCode(
                ConflictException.builder().message("conflict")
                        .build())).isEqualTo(HandlerErrorCode.ResourceConflict);
    }

    @Test
    public void handleRequest_ServiceQuotaExceededException() {
        assertThat(ExceptionMapper.mapToHandlerErrorCode(
                ServiceQuotaExceededException.builder().message("limit exceed")
                        .build())).isEqualTo(HandlerErrorCode.ServiceLimitExceeded);
    }

    @Test
    public void handleRequest_ThrottlingException() {
        assertThat(ExceptionMapper.mapToHandlerErrorCode(
                ThrottlingException.builder().message("throttling")
                        .build())).isEqualTo(HandlerErrorCode.Throttling);
    }

    @Test
    public void handleRequest_ValidationException() {
        assertThat(ExceptionMapper.mapToHandlerErrorCode(
                ValidationException.builder().message("invalid")
                        .build())).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequest_InternalServerException() {
        assertThat(ExceptionMapper.mapToHandlerErrorCode(
                InternalServerException.builder().message("server error")
                        .build())).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }

    @Test
    public void handleRequest_ResourceNotFoundException() {
        assertThat(ExceptionMapper.mapToHandlerErrorCode(
                ResourceNotFoundException.builder().message("not found")
                        .build())).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_OtherException() {
        assertThat(ExceptionMapper.mapToHandlerErrorCode(
                new RuntimeException("Runtime exception"))).isEqualTo(HandlerErrorCode.InternalFailure);
    }
}
