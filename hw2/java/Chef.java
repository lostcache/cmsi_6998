public class Chef extends Thread {

    private Kitchen kitchen;
    private int completedOrders = 0;
    private volatile boolean running = true;

    public Chef(Kitchen kitchen) {
        this.kitchen = kitchen;
        this.setName("👨‍🍳 Chef");
    }

    private Order waitForOrder() {
        Order order = null;
        while (
            order == null && running && !Thread.currentThread().isInterrupted()
        ) {
            order = this.kitchen.getFromKitchenInQueue();

            if (order == null) {
                Thread.yield();
            }
        }

        return order;
    }

    private void cook(Order order) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        this.kitchen.addToKitchenOutQueue(order);
        this.completedOrders++;
        System.out.println(
            "🍳 Chef cooked " + order.meal() + " for seat " + order.seatNo()
        );
    }

    private void takeCoffeeBreakIfItsTime() {
        if (completedOrders % 4 == 0 && completedOrders > 0) {
            System.out.println("☕ Chef coffee break");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            Order order = waitForOrder();
            if (order != null) {
                cook(order);
                this.takeCoffeeBreakIfItsTime();
            }
        }
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }
}
