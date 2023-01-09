package org.apache.skyline.admin.web.config.mvc.method.annotation;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.apache.skyline.admin.commons.exception.SkylineAdminErrorCode;
import org.bravo.gaia.commons.base.HttpResult;
import org.bravo.gaia.commons.enums.SystemErrorCodeEnum;
import org.bravo.gaia.commons.errorcode.ErrorCode;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.log.GaiaLogger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hejianbing
 * @version @Id: SkylineAdminExceptionHandler.java, v 0.1 2023年01月09日 10:56 hejianbing Exp $
 */

@ResponseBody
@ControllerAdvice
public class SkylineAdminExceptionHandler {


    @ExceptionHandler({
            IllegalArgumentException.class,
            NullPointerException.class,
            DuplicateKeyException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            PlatformException.class
    })
    protected HttpResult handleException(final Exception exception) {
        GaiaLogger.getGlobalErrorLogger().error(exception.getMessage());

        if (exception instanceof IllegalArgumentException) {
            return HttpResult.fail(SystemErrorCodeEnum.PARAM_ILLEGAL.getCode(),exception.getMessage());
        }

        if (exception instanceof NullPointerException) {
            return HttpResult.fail(SystemErrorCodeEnum.SYSTEM_404.getCode(),exception.getMessage());
        }

        if (exception instanceof DuplicateKeyException) {
            return HttpResult.fail(SkylineAdminErrorCode.UNIQUE_INDEX_CONFLICT_ERROR.getCode(),exception.getMessage());
        }

        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException )exception;
            BindingResult bindingResult = ex.getBindingResult();
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(f -> f.getField().concat(": ").concat(Optional.ofNullable(f.getDefaultMessage()).orElse("")))
                    .collect(Collectors.joining("; "));

            return HttpResult.fail(SystemErrorCodeEnum.PARAM_ILLEGAL.getCode(),errorMsg);

        }

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException )exception;

            String message = ex.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath().toString().concat(": ").concat(v.getMessage()))
                    .collect(Collectors.joining("; "));;

            return HttpResult.fail(SystemErrorCodeEnum.PARAM_ILLEGAL.getCode(),message);
        }

        PlatformException ex = (PlatformException) exception;

        ErrorCode errorCode = ex.getErrorContext().getCurrentErrorCode();

        return HttpResult.fail(errorCode,ex.getMessage());


    }
}