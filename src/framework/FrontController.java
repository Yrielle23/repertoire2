package framework;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import framework.annotation.ApiController;

@WebServlet("/*")
public class FrontController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        handleRequest(req, res, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        handleRequest(req, res, "POST");
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse res, String method)
            throws ServletException, IOException {

        String url = req.getRequestURI().substring(req.getContextPath().length());
        if (url.isEmpty()) {
            url = "/";
        }

        System.out.println("[FrontController] " + method + " " + url);

        Map<String, Mapping> routes = ("GET".equals(method))
            ? (Map<String, Mapping>) getServletContext().getAttribute("routesGet")
            : (Map<String, Mapping>) getServletContext().getAttribute("routesPost");

        if (routes == null || !routes.containsKey(url)) {
            res.setContentType("application/json; charset=UTF-8");
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("{\"status\":\"error\",\"message\":\"Route not found: " + method + " " + url + "\"}");
            return;
        }

        try {
            Mapping mapping = routes.get(url);
            Class<?> controllerClass = Class.forName(mapping.getClassName());

            if (!controllerClass.isAnnotationPresent(ApiController.class)) {
                res.setContentType("application/json; charset=UTF-8");
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("{\"status\":\"error\",\"message\":\"Controller non autorisé: "
                        + controllerClass.getName() + " n'est pas annoté @ApiController\"}");
                return;
            }

            Object controller = controllerClass.getDeclaredConstructor().newInstance();
            Method actionMethod = controllerClass.getDeclaredMethod(mapping.getMethodName());
            Object[] args = bindArguments(actionMethod, req);

            if (actionMethod.getParameterCount() > 0) {
                actionMethod.setAccessible(true);
                Object result = actionMethod.invoke(controller, args);
                res.setContentType("application/json; charset=UTF-8");
                res.getWriter().write((String) result);
            } else {
                Object result = actionMethod.invoke(controller);
                res.setContentType("application/json; charset=UTF-8");
                res.getWriter().write((String) result);
            }

        } catch (Exception e) {
            System.err.println("[FrontController] Error: " + e.getMessage());
            e.printStackTrace();
            res.setContentType("application/json; charset=UTF-8");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private Object[] bindArguments(Method method, HttpServletRequest req) {
        Parameter[] parameters = method.getParameters();
        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            values[i] = bindParameter(req, parameter);
        }

        return values;
    }

    private Object bindParameter(HttpServletRequest req, Parameter parameter) {
        Class<?> parameterType = parameter.getType();

        if (isSimpleType(parameterType)) {
            String parameterName = parameter.getName();
            String requestValue = req.getParameter(parameterName);
            return convertValue(requestValue, parameterType);
        }

        return bindObject(parameterType, req);
    }

    private Object bindObject(Class<?> parameterType, HttpServletRequest req) {
        try {
            Object objectInstance = parameterType.getDeclaredConstructor().newInstance();

            for (Field field : getAllFields(parameterType)) {
                field.setAccessible(true);
                String requestValue = req.getParameter(field.getName());

                if (requestValue == null) {
                    if (field.getType().isPrimitive()) {
                        field.set(objectInstance, getPrimitiveDefaultValue(field.getType()));
                    } else {
                        field.set(objectInstance, null);
                    }
                    continue;
                }

                field.set(objectInstance, convertValue(requestValue, field.getType()));
            }

            return objectInstance;
        } catch (Exception e) {
            throw new RuntimeException("Impossible de binder l'objet " + parameterType.getSimpleName(), e);
        }
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;

        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                fields.add(field);
            }
            current = current.getSuperclass();
        }

        return fields;
    }

    private boolean isSimpleType(Class<?> parameterType) {
        return parameterType.isPrimitive()
                || parameterType == String.class
                || parameterType == Integer.class || parameterType == int.class
                || parameterType == Long.class || parameterType == long.class
                || parameterType == Double.class || parameterType == double.class
                || parameterType == Float.class || parameterType == float.class
                || parameterType == Boolean.class || parameterType == boolean.class
                || parameterType == Character.class || parameterType == char.class
                || parameterType == Short.class || parameterType == short.class
                || parameterType == Byte.class || parameterType == byte.class;
    }

    private Object getPrimitiveDefaultValue(Class<?> primitiveType) {
        if (primitiveType == boolean.class) {
            return false;
        }
        if (primitiveType == byte.class) {
            return (byte) 0;
        }
        if (primitiveType == short.class) {
            return (short) 0;
        }
        if (primitiveType == char.class) {
            return '\u0000';
        }
        if (primitiveType == int.class) {
            return 0;
        }
        if (primitiveType == long.class) {
            return 0L;
        }
        if (primitiveType == float.class) {
            return 0.0f;
        }
        if (primitiveType == double.class) {
            return 0.0d;
        }
        return null;
    }

    private Object convertValue(String requestValue, Class<?> parameterType) {
        if (requestValue == null) {
            if (parameterType == String.class) {
                return null;
            }
            if (parameterType == Integer.class || parameterType == int.class) {
                return (parameterType == int.class) ? 0 : null;
            }
            if (parameterType == Long.class || parameterType == long.class) {
                return (parameterType == long.class) ? 0L : null;
            }
            if (parameterType == Double.class || parameterType == double.class) {
                return (parameterType == double.class) ? 0.0d : null;
            }
            if (parameterType == Float.class || parameterType == float.class) {
                return (parameterType == float.class) ? 0.0f : null;
            }
            if (parameterType == Boolean.class || parameterType == boolean.class) {
                return (parameterType == boolean.class) ? false : null;
            }
            if (parameterType == Character.class || parameterType == char.class) {
                return (parameterType == char.class) ? '\u0000' : null;
            }
            if (parameterType == Short.class || parameterType == short.class) {
                return (parameterType == short.class) ? (short) 0 : null;
            }
            if (parameterType == Byte.class || parameterType == byte.class) {
                return (parameterType == byte.class) ? (byte) 0 : null;
            }
            return null;
        }

        if (parameterType == String.class) {
            return requestValue;
        }
        if (parameterType == Integer.class || parameterType == int.class) {
            return Integer.valueOf(requestValue);
        }
        if (parameterType == Long.class || parameterType == long.class) {
            return Long.valueOf(requestValue);
        }
        if (parameterType == Double.class || parameterType == double.class) {
            return Double.valueOf(requestValue);
        }
        if (parameterType == Float.class || parameterType == float.class) {
            return Float.valueOf(requestValue);
        }
        if (parameterType == Boolean.class || parameterType == boolean.class) {
            return Boolean.valueOf(requestValue);
        }
        if (parameterType == Character.class || parameterType == char.class) {
            return requestValue.charAt(0);
        }
        if (parameterType == Short.class || parameterType == short.class) {
            return Short.valueOf(requestValue);
        }
        if (parameterType == Byte.class || parameterType == byte.class) {
            return Byte.valueOf(requestValue);
        }

        return requestValue;
    }
}