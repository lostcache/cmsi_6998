class Main {

    public static void main(String[] args) {
        System.out.println("🍽️ Starting Dining Philosophers Simulation");

        RestaurantEnvironment env = initRestaurant();
        Philosopher[] philosophers = initPhilosophers(env.table);

        env.waiter.start();
        env.chef.start();

        for (Philosopher philosopher : philosophers) {
            philosopher.start();
        }

        try {
            for (Philosopher philosopher : philosophers) {
                philosopher.join();
            }

            System.out.println("🛑 Shutting down");
            env.waiter.shutdown();
            env.waiter.join();
            env.chef.shutdown();
            env.chef.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✅ Simulation completed");
    }

    private static class RestaurantEnvironment {

        public final Table table;
        public final Kitchen kitchen;
        public final Restaurant restaurant;
        public final Waiter waiter;
        public final Chef chef;

        public RestaurantEnvironment(
            Table table,
            Kitchen kitchen,
            Restaurant restaurant,
            Waiter waiter,
            Chef chef
        ) {
            this.table = table;
            this.kitchen = kitchen;
            this.restaurant = restaurant;
            this.waiter = waiter;
            this.chef = chef;
        }
    }

    private static RestaurantEnvironment initRestaurant() {
        Table table = new Table();
        Kitchen kitchen = new Kitchen();
        Restaurant restaurant = new Restaurant(table, kitchen);
        Waiter waiter = new Waiter(restaurant, table, kitchen);
        Chef chef = new Chef(kitchen);

        return new RestaurantEnvironment(
            table,
            kitchen,
            restaurant,
            waiter,
            chef
        );
    }

    private static Philosopher[] initPhilosophers(Table table) {
        Philosopher[] philosophers = new Philosopher[5];
        String[] names = {
            "Aristotle",
            "Socrates",
            "Plato",
            "Confucius",
            "Descartes",
        };

        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher(table);
            philosophers[i].setName(names[i]);
        }

        return philosophers;
    }
}
