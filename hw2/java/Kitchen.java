public class Kitchen {

    private java.util.Queue<Order> kitchenInQueue =
        new java.util.concurrent.ConcurrentLinkedQueue<>();
    private java.util.Queue<Order> kitchenOutQueue =
        new java.util.concurrent.ConcurrentLinkedQueue<>();

    public Kitchen() {}

    public void addToKitchenInQueue(Order order) {
        kitchenInQueue.add(order);
    }

    public void addToKitchenOutQueue(Order order) {
        kitchenOutQueue.add(order);
    }

    public Order getFromKitchenInQueue() {
        return kitchenInQueue.poll();
    }

    public Order getFromKitchenOutQueue() {
        return kitchenOutQueue.poll();
    }
}
