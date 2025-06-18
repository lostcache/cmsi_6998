public class Restaurant {

    private static final int NUM_CHEFS = 3;
    private static final int NUM_WAITERS = 2;
    private static final int NUM_PHILOSOPHERS = 5;

    private final Table table;
    private final Kitchen kitchen;
    private Chef[] chefs;
    private Waiter[] waiters;
    private Philosopher[] philosophers;
    private final String[] PHILOSOPHER_NAMES = {
        "Plato",
        "Socrates",
        "Aristotle",
        "Dostoevsky",
        "Marcus Aurelius",
    };

    private static final String[] WAITER_NAMES = { "Miria ", "Isaac" };

    private static final String[] CHEF_NAMES = { "Eren", "Mikasa", "Armin" };

    public Restaurant(Table table, Kitchen kitchen) {
        this.table = table;
        this.table.setRestaurant(this);
        this.kitchen = kitchen;
        this.kitchen.setRestaurant(this);

        this.chefs = new Chef[NUM_CHEFS];
        for (int i = 0; i < this.chefs.length; i++) {
            this.chefs[i] = new Chef(this.kitchen, CHEF_NAMES[i]);
        }

        this.waiters = new Waiter[NUM_WAITERS];
        for (int i = 0; i < this.waiters.length; i++) {
            this.waiters[i] = new Waiter(
                this.table,
                this.kitchen,
                WAITER_NAMES[i]
            );
        }

        this.philosophers = new Philosopher[NUM_PHILOSOPHERS];
        for (int i = 0; i < this.philosophers.length; i++) {
            this.philosophers[i] = new Philosopher(
                this.table,
                PHILOSOPHER_NAMES[i]
            );
        }
    }

    public void start() {
        for (Philosopher philosopher : this.philosophers) {
            philosopher.start();
        }

        for (Chef chef : this.chefs) {
            chef.start();
        }

        for (Waiter waiter : this.waiters) {
            waiter.start();
        }
    }

    public void shutdown() {
        for (Philosopher philosopher : this.philosophers) {
            try {
                philosopher.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Waiter waiter : this.waiters) {
            waiter.shutdown();
        }
        for (Waiter waiter : this.waiters) {
            try {
                waiter.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Chef chef : this.chefs) {
            chef.shutdown();
        }
        for (Chef chef : this.chefs) {
            try {
                chef.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Chef getRandomChef() {
        int randomIndex = (int) (Math.random() * this.chefs.length);
        Chef chef = this.chefs[randomIndex];
        if (chef.isBusy()) {
            return null;
        }
        return chef;
    }

    public synchronized Waiter getRandomWaiter() {
        int randomIndex = (int) (Math.random() * this.waiters.length);
        Waiter waiter = this.waiters[randomIndex];
        if (waiter.isBusy()) {
            return null;
        }
        return waiter;
    }

    public Philosopher getPhilosopher(int seatNo) {
        return this.philosophers[seatNo];
    }

    public String getPhilosopherName(int seatNo) {
        return this.philosophers[seatNo].name();
    }
}
