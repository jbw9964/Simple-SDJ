package group.practice.configs.aspects.logging;

import static group.practice.configs.aspects.logging.Utils.*;

import group.practice.exceptions.*;
import lombok.extern.slf4j.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

@Slf4j
@Aspect
public class ExceptionAspect {

    @Pointcut("@within(LogException) || @annotation(LogException)")
    public void LogExceptionPointcut() {
    }

    @AfterThrowing(value = "LogExceptionPointcut()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        String simpleSignature = getSimpleSignature(joinPoint);
        Object[] args = joinPoint.getArgs();

        if (ex instanceof CustomException) {
            log.warn(
                    "Custom exception has been raised at : {} - with args [{}]",
                    simpleSignature, args, ex
            );
        } else {
            log.error(
                    "Unexpected exception has been raised at : {} - with args [{}]",
                    simpleSignature, args, ex
            );
        }
    }
}
