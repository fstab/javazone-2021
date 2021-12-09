package de.fstab.greeter;

import java.util.Random;
import java.util.Set;

public class Database {

    private final Set<String> data = Set.of(
            "liam",
            "olivia",
            "noah",
            "emma",
            "oliver",
            "ava",
            "elijah",
            "charlotte",
            "william",
            "sophia",
            "james",
            "amelia",
            "benjamin",
            "isabella",
            "lucas",
            "mia",
            "henry",
            "evelyn",
            "alexander",
            "harper"
    );

    private final Random random = new Random(0);

    public String loadGreeting(String name) throws GreetingNotFoundException {
        try {
            // Reading from this database takes about 200ms.
            Thread.sleep((long) Math.abs(190.0 + 10 * random.nextGaussian()));
            if (data.contains(name)) {
                return "Hello, " + name.substring(0, 1).toUpperCase() + name.substring(1) + "!\n";
            } else {
                throw new GreetingNotFoundException(name);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
