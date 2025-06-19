public class Waiter extends Thread {

    private final String name;
    private Kitchen kitchen;
    private volatile boolean running = true;
    private volatile Order carryingOrder = null;

    public Waiter(Table table, Kitchen kitchen, String name) {
        this.name = name;
        this.kitchen = kitchen;
    }

    public synchronized void takeOrder(Order newOrder) {
        assert newOrder != null;
        assert this.carryingOrder == null;

        this.carryingOrder = newOrder;
        Logger.log("📋 Waiter " + this.name + " took order " + newOrder.meal());
    }

    public boolean tryRelayOrderToRandomChef() {
        Logger.log("🔍 Waiter " + this.name + " looking for available chef");
        Chef chef = kitchen.getRandomAvailableChef();
        if (chef == null) {
            Logger.log("❌ Waiter " + this.name + " found no available chefs");
            return false;
        }

        Logger.log(
            "🤝 Waiter " + this.name + " hands off order to chef " + chef.name()
        );
        chef.assignOrder(this.carryingOrder);
        return true;
    }

    public void serveOrderAtTable() {
        assert this.carryingOrder != null;
        Philosopher philosopher = this.carryingOrder.orderedBy();
        assert philosopher != null;
        while (philosopher.isBusy()) Thread.yield();
        Logger.log(
            "🍽️ Waiter " +
            this.name +
            " served order " +
            this.carryingOrder.meal() +
            " to philosopher " +
            philosopher.name()
        );
        philosopher.serveOrder(this.carryingOrder);
    }

    public void waitForOrderFromPhilosophers() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 2000) {
            if (this.carryingOrder != null) {
                return;
            }
            Thread.yield();
        }
    }

    public boolean isBusy() {
        return this.carryingOrder != null;
    }

    public void markOrderAsRefund() {
        this.carryingOrder.markAsRefund();
    }

    private Order checkForCookedOrderAtKitchenCounter() {
        return this.kitchen.getCompletedOrders();
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }

    public String name() {
        return this.name;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            this.waitForOrderFromPhilosophers();

            if (this.carryingOrder != null) {
                Logger.log(
                    "📨 " +
                    this.name +
                    " receives order for " +
                    this.carryingOrder.meal()
                );

                boolean orderAssigned = this.tryRelayOrderToRandomChef();
                if (!orderAssigned) {
                    this.markOrderAsRefund();
                    this.serveOrderAtTable();
                }
                this.carryingOrder = null;
            }

            this.carryingOrder = this.checkForCookedOrderAtKitchenCounter();

            if (this.carryingOrder != null) {
                Logger.log(
                    "🍽️ Waiter " +
                    this.name +
                    " picked up order " +
                    this.carryingOrder.meal() +
                    " from kitchen counter"
                );
                this.serveOrderAtTable();
            }
            this.carryingOrder = null;
        }

        Logger.log("🏠 " + this.name + " leaving for the day");
    }
}
