package com.sprit.routing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodeInfo {
    private final Object controller;
    private final Method action;

    public MethodeInfo(Object controller, Method action) {
        this.controller = controller;
        this.action = action;
    }

    public Object getController() {
        return controller;
    }

    public Method getAction() {
        return action;
    }

    public void invoke(HttpServletRequest req, HttpServletResponse resp)
            throws InvocationTargetException, IllegalAccessException {
        if (action.getParameterCount() == 2) {
            action.invoke(controller, req, resp);
        } else {
            action.invoke(controller);
        }
    }
}
