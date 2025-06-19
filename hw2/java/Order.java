public class Order {

    private boolean refunded = false;
    private Meal meal;
    private Philosopher orderedBy;

    public Order(int seatNo, Meal meal, Philosopher orderedBy) {
        this.meal = meal;
        this.orderedBy = orderedBy;
    }

    public double price() {
        return meal.price();
    }

    public void markAsRefund() {
        this.refunded = true;
    }

    public boolean isRefunded() {
        return this.refunded;
    }

    public Meal meal() {
        return this.meal;
    }

    public Philosopher orderedBy() {
        return this.orderedBy;
    }
}
