package com.coppel.aspects.customcomplements;

import com.coppel.utils.LogUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.coppel.constants.Estado.FALLO;
import static com.coppel.constants.Estado.FINALIZA_TRANSACCION;
import static com.coppel.constants.Estado.INICIA_TRANSACCION;

/**
 * Bitácora de inicio, fin y fallo de cada operación de los servicios con el LogUtil común.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ComplementsLoggingAspect {
    private final LogUtil log;

    @Around("execution(* com.coppel.services.customcomplements.impl.*ServiceImpl.*(..))")
    public Object logAroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getDeclaringType().getSimpleName()
            + "." + joinPoint.getSignature().getName();

        log.grabar(INICIA_TRANSACCION, methodName, "");

        try {
            Object result = joinPoint.proceed();
            log.grabar(FINALIZA_TRANSACCION, methodName, "");
            return result;
        } catch (Exception e) {
            String causaReal = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            log.grabar(FALLO, methodName, causaReal);
            throw e;
        }
    }
}
