package com.xc.ai.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Slf4j
@Aspect
public class ControllerLogAspect {

    /**
     * AOP切面
     *   *表示返回任意类型,void,也可以是其它类型的参数
     *   com.xc.ai.controller表示指定包名,要切的class类的所在包
     *   .. 可以匹配到当前包和子包中的类
     *   * 匹配当前包以及子包下的class类
     *   . 无意义
     *   * 匹配任意方法名
     *   (..)匹配任意参数
     */
    @Around("execution(* com.xc.ai.controller..*.*(..))")
    public Object timesLog(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object proceed = joinPoint.proceed();
        stopWatch.stop();
        String point = joinPoint.getTarget().getClass().getName()
                + "."
                + joinPoint.getSignature().getName();
        long takeTime = stopWatch.getTotalTimeMillis();
        if (takeTime > 3000) {
            log.error("{} 耗时偏长{}毫秒", point, takeTime);
        } else if (takeTime > 2000) {
            log.warn("{} 耗时中等{}毫秒", point, takeTime);
        } else {
            log.info("{} 耗时{}毫秒", point, takeTime);
        }

        return proceed;
    }
}
