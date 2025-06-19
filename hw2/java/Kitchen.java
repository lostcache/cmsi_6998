public class Kitchen {

    private Restaurant restaurant = null;
    private java.util.Queue<Order> kitchenOutQueue =
        new java.util.concurrent.ConcurrentLinkedQueue<>();

    public Kitchen() {}

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void addToKitchenOutQueue(Order order) {
        kitchenOutQueue.add(order);
    }

    public Order getCompletedOrders() {
        return kitchenOutQueue.poll();
    }

    public Chef getRandomAvailableChef() {
        return this.restaurant.getRandomAvailableChef();
    }
}
