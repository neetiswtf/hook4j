package ru.neetis.hook4j.agent;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import ru.neetis.hook4j.api.Hook;

import java.io.IOException;
import java.lang.instrument.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Hook4J {
    private static Instrumentation instrumentation;

    public static void agentmain(final String args, final Instrumentation inst) {
        instrumentation = inst;
    }

    public static void flush(final Set<Hook> hooks) throws UnmodifiableClassException {
        /*
            We need to define our transformer for edit classes at runtime
         */
        instrumentation.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            for(final Hook hook : hooks){
                if(hook.getClazz().equals(classBeingRedefined)){
                    try {
                        final CtClass ctClass = ClassPool.getDefault().get(className);
                        for(final CtMethod ctMethod : ctClass.getMethods()){
                            if(ctMethod.getName().equals(hook.getMethod().getName())){
                                switch (hook.getHookType()){
                                    case APPEND:
                                        ctMethod.insertBefore(hook.getSource());
                                        ctClass.detach();
                                        break;
                                    case REPLACE:
                                        ctMethod.instrument(new ExprEditor(){
                                            @Override
                                            public void edit(MethodCall m) throws CannotCompileException {
                                                m.replace(hook.getSource());
                                            }
                                        });
                                        break;
                                }
                                classfileBuffer = ctClass.toBytecode(); // Compile new class and save it to buffer
                            }
                        }
                    } catch (NotFoundException | CannotCompileException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return classfileBuffer;
        }, true);
        /*
            And now, we need to collect all classes that user want to hook
         */
        final List<Class<?>> classes = new ArrayList<>();
        hooks.forEach(hook -> {
            classes.add(hook.getClazz());
        });
        instrumentation.retransformClasses(classes.toArray(new Class[0])); // Aaaand, finally!
    }
}
