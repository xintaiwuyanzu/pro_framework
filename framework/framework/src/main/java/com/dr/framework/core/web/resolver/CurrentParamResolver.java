package com.dr.framework.core.web.resolver;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.core.web.annotations.Current;
import com.dr.framework.sys.controller.LoginController;
import com.dr.framework.sys.entity.Organise;
import com.dr.framework.sys.entity.Person;
import com.dr.framework.sys.service.SysOrganisePersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author dr
 */
@Component
public class CurrentParamResolver implements HandlerMethodArgumentResolver {
    List<Class> supportClass = Arrays.asList(Person.class, Organise.class);
    @Autowired
    LoginController loginController;
    @Autowired
    SysOrganisePersonService sysOrganisePersonService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Current.class) && supportClass.contains(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class type = parameter.getParameterType();
        Current current = parameter.getParameterAnnotation(Current.class);
        ResultEntity<Person> resultEntity = loginController.personInfo(webRequest.getNativeRequest(HttpServletRequest.class));
        if (current.required()) {
            Assert.isTrue(resultEntity.isSuccess(), resultEntity.getMessage());
        }
        Person person = resultEntity.getData();
        if (type.equals(Person.class)) {
            return person;
        } else if (type.equals(Organise.class)) {
            if (person != null) {
                return sysOrganisePersonService.getPersonDefaultOrganise(person.getId());
            }
        }
        return null;
    }
}
