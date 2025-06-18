public class Chef extends Thread {

    private String name;
    private Kitchen kitchen;
    private int completedOrders = 0;
    private volatile Order job = null;
    private volatile boolean running = true;

    public Chef(Kitchen kitchen, String name) {
        this.kitchen = kitchen;
        this.name = name;
    }

    public synchronized boolean assignOrder(Order order) {
        if (this.job != null) {
            return false;
        }

        Logger.log("Order: " + order.meal() + " assigned to chef " + this.name);
        this.job = order;
        return true;
    }

    private void waitForOrder() {
        while (this.job == null) {
            Thread.yield();
        }
    }

    private void cook() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        System.out.println(
            "🍳 Chef: " +
            this.name +
            " cooked " +
            this.job.meal() +
            " for philosopher " +
            this.job.orderedBy()
        );
    }

    private void putOnKitchenCounterAndIncrementOrderCounter() {
        this.kitchen.addToKitchenOutQueue(this.job);
        this.job = null;
        this.completedOrders++;
    }

    private void takeCoffeeBreakIfItsTime() {
        if (this.completedOrders % 4 == 0 && this.completedOrders > 0) {
            System.out.println("☕ Chef coffee break");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public boolean isBusy() {
        return this.job != null;
    }

    public void shutdown() {
        this.running = false;
        this.interrupt();
    }

    public String name() {
        return this.name;
    }

    @Override
    public void run() {
        while (this.running && !Thread.currentThread().isInterrupted()) {
            this.waitForOrder();
            this.cook();
            this.putOnKitchenCounterAndIncrementOrderCounter();
            this.takeCoffeeBreakIfItsTime();
        }

        Logger.log(this.name + "leaving for the day");
    }
}
