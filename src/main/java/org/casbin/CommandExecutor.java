package org.casbin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.casbin.jcasbin.main.EnforceResult;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.resp.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class CommandExecutor {

    private NewEnforcer enforcer;

    private String inputMethodName;

    private String[]  inputVal;

    public CommandExecutor(NewEnforcer enforcer, String inputMethodName, String[] inputVal) {
        this.enforcer = enforcer;
        this.inputMethodName = inputMethodName;
        this.inputVal = inputVal;
    }

    /***
     * Converts a string input into a JSON formatted string.
     *
     * @param input The input string to be converted to JSON format. It should be enclosed in curly braces {}.
     * @return A JSON formatted string representing the key-value pairs from the input string.
     */
    public static String convertToJson(String input) {
        input = input.trim();
        // Handle the simple format {key: value}
        if (!input.contains("\"")) {
            input = input.substring(1, input.length() - 1).trim();
            StringBuilder jsonBuilder = new StringBuilder("{");
            String[] pairs = input.split(",");
            for (String pair : pairs) {
                pair = pair.trim();
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    jsonBuilder.append("\"").append(key).append("\":").append(value).append(",");
                }
            }
            if (jsonBuilder.length() > 1) {
                jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
            }
            jsonBuilder.append("}");
            return jsonBuilder.toString();
        }

        return input;
    }

    public String outputResult() throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        Class<? extends Enforcer> clazz = enforcer.getClass();
        Method[] methods = clazz.getMethods();

        ResponseBody responseBody = new ResponseBody(null, null);
        for (Method method : methods) {
            String methodName = method.getName();
            if(methodName.equals(inputMethodName)) {
                Type[] genericParameterTypes = method.getGenericParameterTypes();
                Object[] convertedParams = new Object[genericParameterTypes.length];
                Class<?> returnType = method.getReturnType();

                if(genericParameterTypes.length == 3 && genericParameterTypes[0] == String.class && genericParameterTypes[1].getTypeName().equals("java.util.List<java.lang.String>") && genericParameterTypes[2].getTypeName().equals("java.util.List<java.lang.String>")) {
                    convertedParams[0] = inputVal[0];
                    convertedParams[1] = Arrays.asList(inputVal[1].split(","));
                    convertedParams[2] = Arrays.asList(inputVal[2].split(","));
                } else if(genericParameterTypes.length == 2 && genericParameterTypes[0].getTypeName().equals("java.util.List<java.lang.String>") && genericParameterTypes[1].getTypeName().equals("java.util.List<java.lang.String>")) {
                    convertedParams[0] = Arrays.asList(inputVal[0].split(","));
                    convertedParams[1] = Arrays.asList(inputVal[1].split(","));
                } else {
                    for (int i = 0; i < genericParameterTypes.length; i++) {
                        if(genericParameterTypes[i] == int.class) {
                            convertedParams[i] = Integer.valueOf(inputVal[i]);
                        } else if(genericParameterTypes[i] == String.class) {
                            convertedParams[i] = inputVal[i];
                        } else if(genericParameterTypes[i] == Object[].class || genericParameterTypes[i] == String[].class) {
                            convertedParams[i] = Arrays.copyOfRange(inputVal, i, inputVal.length);
                        } else if (genericParameterTypes[i] == String[][].class) {
                            String[] arr = Arrays.copyOfRange(inputVal, i, inputVal.length);
                            String[][] res = new String[arr.length][];
                            for (int i1 = 0; i1 < res.length; i1++) {
                                res[i1] = arr[i1].split(",");
                            }
                            convertedParams[i] = res;
                        } else if (genericParameterTypes[i].getTypeName().equals("java.util.List<java.lang.String>")) {
                            String[] arr = Arrays.copyOfRange(inputVal, i, inputVal.length);
                            convertedParams[i] = Arrays.asList(arr);
                        } else if (genericParameterTypes[i].getTypeName().equals("java.util.List<java.util.List<java.lang.String>>")) {
                            List<List<String>> res = new ArrayList<>();
                            String[] arr = Arrays.copyOfRange(inputVal, i, inputVal.length);
                            for (String s : arr) {
                                List<String> ans = new ArrayList<>();
                                Collections.addAll(ans, s.split(","));
                                res.add(ans);
                            }
                            convertedParams[i] = res;
                        }
                    }
                }

                Object[] extraConvertedParams = new Object[inputVal.length];
                boolean hasJson = false;
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    for (int i = 0; i < inputVal.length; i++) {
                        if (inputVal[i].trim().startsWith("{")) {
                            Map<String, Object> objectMap = objectMapper.readValue(convertToJson(inputVal[i]), new TypeReference<Map<String, Object>>() {
                            });
                            extraConvertedParams[i] = objectMap;
                            hasJson = true;
                        } else {
                            extraConvertedParams[i] = inputVal[i];
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hasJson = false;
                }
                Object invoke;
                if(hasJson){
                    invoke = method.invoke(enforcer, (Object) extraConvertedParams);
                } else {
                    invoke = method.invoke(enforcer, convertedParams);
                }

                if(returnType == boolean.class) {
                    responseBody.setAllow((Boolean) invoke);
                } else if (returnType == List.class) {
                    responseBody.setExplain((ArrayList<?>) invoke);
                } else if (returnType == EnforceResult.class) {
                    responseBody.setAllow(((EnforceResult) invoke).isAllow());
                    responseBody.setExplain((ArrayList<?>) ((EnforceResult) invoke).getExplain());
                }
                enforcer.savePolicy();
                break;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(responseBody);
    }
}
