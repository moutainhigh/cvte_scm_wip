package com.cvte.scm.demo.boot.aspect;

import com.cvte.csb.toolkit.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 17:03
 */
@Aspect
@Component
public class TraceAspect {
    @Autowired
    private Tracer tracer;

    @Pointcut("execution(public * com.cvte.csb.web.handler.GlobalExceptionHandler.*(..))")
    public void tracePointcut(){}

    @Before("tracePointcut()")
    public void doBefore(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        for(Object o:args) {
            if (o instanceof RuntimeException) {
                traceStackInfo((Exception) o);
            }
        }
    }

    /**
     * 记录堆栈信息上报zipkin
     * @param ex
     */
    private void traceStackInfo(Exception ex) {
        if (ObjectUtils.isNotNull(tracer)) {
            Span span = tracer.getCurrentSpan();
            StackTraceElement[] stackTraceElements = ex.getStackTrace();
            StringBuffer sb = new StringBuffer();
            sb.append(ex.toString());
            for (StackTraceElement traceElement : stackTraceElements) {
                sb.append("\n" + traceElement.toString());
            }
            span.logEvent("ERROR: " + ex.getMessage());
            tracer.addTag("error", sb.toString());
        }
    }
}
