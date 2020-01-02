package com.lushihao.model.aop;

import com.example.test.annotation.LogHandler;
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
     * 以自定义 @WebLog 注解为切点
     */
    @Pointcut("@annotation(com.example.test.annotation.LogHandler)")
    public void logHandler() {
    }

    @AfterThrowing(pointcut = "logHandler()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) throws Exception {
        getAspectLogDescription(joinPoint);
    }

    /**
     * 获取切面注解的描述
     */
    public void getAspectLogDescription(JoinPoint joinPoint)
            throws Exception {
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
