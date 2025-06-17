public class Waiter extends Thread {

    private Restaurant restaurant;
    private Table table;
    private Kitchen kitchen;
    private volatile boolean running = true;

    public Waiter(Restaurant restaurant, Table table, Kitchen kitchen) {
        this.restaurant = restaurant;
        this.table = table;
        this.kitchen = kitchen;
        this.setName("🧑‍💼 Waiter");
    }

    public void relayOrderToKitchen(Order order) {
        this.kitchen.addToKitchenInQueue(order);
    }

    public void serveOrderAtTable(Order order) {
        this.table.addToOrderOutQueue(order);
    }

    public Order waitForOrderAtTable() {
        return this.table.getFromOrderInQueue();
    }

    public Order waitForOrderAtKitchen() {
        Order order = this.kitchen.getFromKitchenOutQueue();
        if (order == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            order = this.kitchen.getFromKitchenOutQueue();
        }

        return order;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            Order order = this.waitForOrderAtTable();
            if (order != null) {
                this.relayOrderToKitchen(order);
            }

            order = this.waitForOrderAtKitchen();
            if (order != null) {
                this.serveOrderAtTable(order);
            }
        }
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }
}
