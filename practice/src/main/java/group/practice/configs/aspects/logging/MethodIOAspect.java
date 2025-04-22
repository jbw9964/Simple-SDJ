package group.practice.configs.aspects.logging;

import static group.practice.configs.aspects.logging.Utils.*;

import java.util.*;
import lombok.extern.slf4j.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

@Slf4j
@Aspect
public class MethodIOAspect {

    @Pointcut("@within(group.practice.configs.aspects.logging.LogMethodIO) || @annotation(group.practice.configs.aspects.logging.LogMethodIO)")
    public void LogMethodCallPointcut() {
    }

    @Around("LogMethodCallPointcut()")
    public Object logMethodInOut(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {
        String simpleSignature = getSimpleSignature(joinPoint);

        logMethodCall(simpleSignature, joinPoint.getArgs());

        Object result = joinPoint.proceed();

        logMethodReturn(simpleSignature, result);

        return result;
    }

    private void logMethodCall(
            String simpleSignature,
            Object[] args
    ) {
        String logMessage = "<-- " +
                            simpleSignature + " < " +
                            Arrays.toString(args);

        log.info(logMessage);
    }

    private void logMethodReturn(
            String simpleSignature,
            Object result
    ) {
        String logMessage = "--> " +
                            simpleSignature + " > " +
                            result;

        log.info(logMessage);
    }
}
