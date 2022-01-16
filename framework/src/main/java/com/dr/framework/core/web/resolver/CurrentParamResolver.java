package com.dr.framework.core.web.resolver;

import com.dr.framework.common.exception.NeedLoginException;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.bo.ClientInfo;
import com.dr.framework.core.web.annotations.Current;
import com.dr.framework.core.web.interceptor.PersonInterceptor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;


/**
 * 为controller方法提供参数，参数是从request上下文中取出的。
 * 真正的实现是在{@link PersonInterceptor}中
 *
 * @author dr
 */
@Component
public class CurrentParamResolver implements HandlerMethodArgumentResolver {
    List<Class> supportClass = Arrays.asList(Person.class, Organise.class, ClientInfo.class);

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Class type = parameter.getParameterType();
        Current current = parameter.getParameterAnnotation(Current.class);
        if (type.equals(ClientInfo.class)) {
            return webRequest.getAttribute(ClientInfo.CLIENT_INFO_KEY, RequestAttributes.SCOPE_REQUEST);
        } else {
            String key = type.equals(Person.class) ? SecurityHolder.CURRENT_PERSON_KEY : SecurityHolder.CURRENT_ORGANISE_KEY;
            Object value = webRequest.getAttribute(key, RequestAttributes.SCOPE_REQUEST);
            if (current.required() && value == null) {
                throw new NeedLoginException("用户未登录！");
            }
            return value;
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Current.class) && supportClass.contains(parameter.getParameterType());
    }

}
