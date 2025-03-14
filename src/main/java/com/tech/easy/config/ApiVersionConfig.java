package com.tech.easy.config;

import com.tech.easy.common.Version;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Configuration
public class ApiVersionConfig extends RequestMappingHandlerMapping {

    @Override
    protected RequestMappingInfo getMappingForMethod( Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) return null;

        Version classLevel = handlerType.getAnnotation(Version.class);
        Version methodLevel = method.getAnnotation(Version.class);

        if (classLevel == null && methodLevel == null) {
            // no version annotation
            return info;
        }

        int version = (methodLevel != null) ? methodLevel.value() : classLevel.value();
        String prefix = "/v" + version;

        // Handle case when patterns condition is null
        PatternsRequestCondition patterns = info.getPatternsCondition();
        PatternsRequestCondition pattern;
        if (patterns != null) {
            pattern = new PatternsRequestCondition(prefix).combine(patterns);
        } else {
            pattern = new PatternsRequestCondition(prefix);
        }

        return new RequestMappingInfo(
                pattern,
                info.getMethodsCondition(),
                info.getParamsCondition(),
                info.getHeadersCondition(),
                info.getConsumesCondition(),
                info.getProducesCondition(),
                info.getCustomCondition()
        );
    }
}