package group.practice.configs.aspects.logging;

import org.aspectj.lang.*;

public abstract class Utils {

    public static String getSimpleSignature(JoinPoint joinPoint) {
        String targetName = joinPoint.getTarget()
                .getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        return targetName + "." + methodName;
    }
}
