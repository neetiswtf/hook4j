# Hook4J - A small Java library for hooks

Welcome to the Hook4J repository. Hook4J - Is a small, and fast library used for hooks in Java.
Hook4J use Java-agents for manipulate with bytecode at runtime


# Example of usage

Hook4J is really easy for use. You can hook any method in 4 lines!

For first, compile and copy Hook4J-Agent to C:/ and name it Hook4J-Agent.jar

After, write something like this:

```java
public static void main(final String[] args){
	final Hook4J hook4J = Hook4J.getInstance(); // Get Hook4J instance
	hook4J.bootstrap(); // Initialize the library. (Call it before .flush() method)
	hook4J.addHook(new Hook(  
	  ReflectionUtils.getMethodFromCaller("coolMethod1337") //It's Hook4J util,  
	  HookType.APPEND,  
	  "System.out.println(\"Hello from Hook4J!\");"  
	)); // Add new hook. You can append new code, or replace method body
	hook4J.flush(); // Save all hooks.

	coolMethod1337(); // Call and see the result
}

public static void coolMethod1337(){
	System.out.println("hi");
}
```
Finally! You successfully hook coolMethod1337

## Types of hooks

You can use 2 types of hooks. APPEND and REPLACE

Append adds your code before method code will be executed;

Replace replaces method code to your
