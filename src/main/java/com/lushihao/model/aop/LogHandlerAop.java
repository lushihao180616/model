package com.lushihao.model.aop;

import com.lushihao.model.annotation.LogHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogHandlerAop {
    /**
     * 以自定义注解为切点
     */
    @Pointcut("@annotation(com.lushihao.model.annotation.LogHandler)")
    public void logHandler() {
    }

    /**
     * 抛异常调用方法
     *
     * @param joinPoint
     * @param e
     * @throws Exception
     */
    @AfterThrowing(pointcut = "logHandler()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    System.out.println(method.getAnnotation(LogHandler.class).description());
                    break;
                }
            }
        }
    }
}
