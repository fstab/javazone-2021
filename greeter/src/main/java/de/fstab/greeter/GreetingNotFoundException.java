package de.fstab.greeter;

public class GreetingNotFoundException extends Exception {
    public GreetingNotFoundException(String name) {
        super("don't know how to greet " + name);
    }
}
