package com.lushihao.model.property.util;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

@Component
public class LSHPropertyUtils implements EmbeddedValueResolverAware {

    /**
     * 获取文件数据
     */
    private static StringValueResolver stringValueResolver;

    /**
     * 设置文件数据
     *
     * @param stringValueResolver
     */
    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }

    /**
     * 获取文件数据
     *
     * @return
     */
    public static StringValueResolver getStringValueResolver() {
        return stringValueResolver;
    }

    /**
     * 动态获取配置文件中的值
     *
     * @param name
     * @return
     */
    public static String getPropertiesValue(String name) {
        try {
            name = "${" + name + "}";
            return stringValueResolver.resolveStringValue(name);
        } catch (Exception e) {
            // 获取失败则返回null
            return null;
        }
    }
}