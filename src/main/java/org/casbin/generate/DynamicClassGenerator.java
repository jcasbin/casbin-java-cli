package org.casbin.generate;


import org.casbin.jcasbin.util.function.CustomFunction;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.*;


import static org.casbin.util.Util.*;

public class DynamicClassGenerator {

    public static CustomFunction generateClass(String methodName, String methodCodes) {
        String className = "CustomFunc";
        int argsNum = getArgsNum(methodCodes);
        StringBuilder sb = new StringBuilder();

        String codeSnippetOne = "import com.googlecode.aviator.runtime.type.AviatorBoolean;\n" +
                "import com.googlecode.aviator.runtime.type.AviatorObject;\n" +
                "import org.casbin.jcasbin.util.function.CustomFunction;\n" +
                "import java.util.Map;\n" +
                "import java.util.regex.Pattern;" +
                "\n" +
                "    public class " + className + " extends CustomFunction {\n" +
                "        @Override\n" +
                "        public AviatorObject call(Map<String, Object> env, ";

        sb.append(codeSnippetOne);
        for (int i = 0; i < argsNum; i++) {
            if(i == argsNum - 1) {
                sb.append("AviatorObject arg").append(i + 1);
            } else {
                sb.append("AviatorObject arg").append(i + 1).append(",");
            }
        }
        sb.append(") {");

        for (int i = 0; i < argsNum; i++) {
            sb.append("String obj").append(i + 1).append("=getStringValue(arg").append(i + 1).append(", env);\n");
        }

        StringBuilder args = new StringBuilder();
        for (int i = 0; i < argsNum; i++) {
            if(i == argsNum - 1) {
                args.append("obj").append(i + 1);
            } else {
                args.append("obj").append(i + 1).append(",");
            }

        }
        sb.append("return AviatorBoolean.valueOf(").append(methodName).append("(").append(args).append("));}\n");
        sb.append("@Override\n" + "public String getName() {\n" + "   return \"").append(methodName).append("\";\n").append("}\n");

        sb.append("public static final String getStringValue(final AviatorObject arg,\n" +
                "                                                  final Map<String, Object> env) {\n" +
                "            String result = null;\n" +
                "            final Object value = arg.getValue(env);\n" +
                "            if (value instanceof Character) {\n" +
                "                result = value.toString();\n" +
                "            } else {\n" +
                "                result = (String) value;\n" +
                "            }\n" +
                "            return result;\n" +
                "        }\n");

        sb.append(methodCodes);
        sb.append("}");
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            InMemoryJavaFileManager customFileManager = new InMemoryJavaFileManager(fileManager);

            JavaFileObject sourceFile = new JavaSourceFromString(className, sb.toString());
            Iterable<? extends JavaFileObject> compilationUnits = Collections.singletonList(sourceFile);

            JavaCompiler.CompilationTask task = compiler.getTask(null, customFileManager, diagnostics, null, null, compilationUnits);
            boolean success = task.call();
            if(success) {
                byte[] classBytes = customFileManager.getClassBytes(className);
                if(classBytes != null) {
                    CustomClassLoader loader = new CustomClassLoader();
                    Class<?> loadedClass = loader.defineClass(className, classBytes);
                    CustomFunction customFunction = (CustomFunction) loadedClass.getDeclaredConstructor().newInstance();
                    return customFunction;
                }
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static class InMemoryJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final Map<String, ByteArrayOutputStream> classBytesMap = new HashMap<>();

        InMemoryJavaFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            classBytesMap.put(className, outputStream);
            return new SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind) {
                @Override
                public OutputStream openOutputStream() {
                    return outputStream;
                }
            };
        }

        public byte[] getClassBytes(String className) {
            ByteArrayOutputStream outputStream = classBytesMap.get(className);
            return outputStream != null ? outputStream.toByteArray() : null;
        }
    }
}


