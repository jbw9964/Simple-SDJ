package group.practice.configs;

import group.practice.configs.aspects.logging.*;
import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public MethodIOAspect loggingAspect() {
        return new MethodIOAspect();
    }

    @Bean
    public ExceptionAspect exceptionAspect() {
        return new ExceptionAspect();
    }
}
