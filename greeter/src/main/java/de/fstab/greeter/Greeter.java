package de.fstab.greeter;

public class Greeter {

    // We have 20 names. 10 fit into the cache, the rest must be loaded from the database.
    private final Cache cache = new Cache(10);
    private final Database db = new Database();

    public String sayHello(String name) throws GreetingNotFoundException {
        String greeting = cache.get(name);
        if (greeting == null) {
            greeting = db.loadGreeting(name);
            cache.put(name, greeting);
        }
        return greeting;
    }
}
