package ru.neetis.hook4j;

import com.sun.tools.attach.VirtualMachine;
import ru.neetis.hook4j.api.Hook;
import ru.neetis.hook4j.exceptions.BootstrapException;
import ru.neetis.hook4j.exceptions.HookException;
import sun.jvmstat.monitor.MonitoredVmUtil;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.*;

public class Hook4J {
    private static final Set<Hook> hooks = new HashSet<>();

    private static final Hook4J instance = new Hook4J();

    private static boolean isInitialized;

    private static VirtualMachine virtualMachine;

    private Hook4J(){}

    public static Hook4J getInstance(){
        return instance;
    }

    /*
        This method used for initialize Hook4J
     */
    public boolean bootstrap() throws IOException {
        try{
            final File file = new File("C:\\Hook4J-Agent.jar");

            /*
                Now, attach Hook4J-Agent to current JVM
             */
            final String pid = ManagementFactory.getRuntimeMXBean().getName().substring(0,  ManagementFactory.getRuntimeMXBean().getName().indexOf("@"));
            virtualMachine = VirtualMachine.attach(pid);
            virtualMachine.loadAgent(file.getAbsolutePath());

            isInitialized = true;
        }catch(final Exception e){
            e.printStackTrace();
        }
        return isInitialized;
    }

    public void addHook(final Hook hook){
        hooks.add(hook);
    }

    /*
        This method used for apply all hooks.
        Call it after bootstrap
     */
    public void flush() throws BootstrapException, HookException {
        if(!isInitialized) {
            /*
                If the Hook4J-agent not initialized, throw the BootstrapException
             */
            throw new BootstrapException("Hook4J not initialized");
        }

        /*
            Hook4J use reflection calls for flush hooks. So we need to call method from Hook4J-agent JAR:
         */
        try{
            final Class<?> clazz = Class.forName("ru.neetis.hook4j.agent.Hook4J");
            final Method method = clazz.getDeclaredMethod("flush", Set.class);
            method.invoke(null, hooks); /* Aaand, finally! */
        }catch(final Exception e) {
            e.printStackTrace();
            throw new HookException("Something went wrong while calling flush method");
        }

        /*
            If all hooks was successfully completed, then clean the tasklist
         */
        hooks.clear();
    }

    /*
        This method unloading Hook4J library.
     */
    public void shutdown() throws IOException {
        virtualMachine.detach();
        isInitialized = false;
    }
}
