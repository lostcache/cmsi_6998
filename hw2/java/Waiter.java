public class Waiter extends Thread {

    private final String name;
    private Table table;
    private Kitchen kitchen;
    private volatile boolean running = true;
    private volatile Order carryingOrder = null;

    public Waiter(Table table, Kitchen kitchen, String name) {
        this.name = name;
        this.table = table;
        this.kitchen = kitchen;
    }

    public synchronized boolean takeOrder(Order newOrder) {
        assert newOrder != null;
        assert this.carryingOrder == null;

        this.carryingOrder = newOrder;
        Logger.log("Waiter " + this.name + " took order " + newOrder.meal());
        return this.carryingOrder != null;
    }

    public boolean tryRelayOrderToRandomChef() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 1000) {
            Chef chef = kitchen.getRandomChef();
            if (chef == null) {
                Thread.yield();
                continue;
            }

            chef.assignOrder(this.carryingOrder);
            return true;
        }

        Logger.log(
            "Waiter " +
            this.name +
            "cound not assign order: " +
            this.carryingOrder.meal() +
            " to any chef"
        );
        return false;
    }

    public void serveOrderAtTable() {
        assert this.carryingOrder != null;
        Philosopher philosopher = table.getPhilosopher(
            this.carryingOrder.seatNo()
        );
        assert philosopher != null;
        philosopher.serveOrder(this.carryingOrder);
        Logger.log(
            "Waiter " +
            this.name +
            " served order " +
            this.carryingOrder.meal() +
            " to philosopher " +
            philosopher.name()
        );
    }

    public void waitForOrder() {
        long startTime = System.currentTimeMillis();
        while (startTime + 2000 < System.currentTimeMillis()) {
            if (this.carryingOrder == null) {
                Thread.yield();
            }
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
            this.waitForOrder();

            if (this.carryingOrder != null) {
                Logger.log(
                    this.name +
                    " receives order for " +
                    this.carryingOrder.meal()
                );

                boolean success = this.tryRelayOrderToRandomChef();
                if (!success) {
                    this.markOrderAsRefund();
                    this.serveOrderAtTable();
                }
                this.carryingOrder = null;
            }

            this.carryingOrder = this.checkForCookedOrderAtKitchenCounter();

            if (this.carryingOrder != null) this.serveOrderAtTable();
        }

        Logger.log(this.name + "leaving for the day");
    }
}
