public class Order {

    private boolean refunded = false;
    private int seatNo;
    private Meal meal;
    private String orderedBy;

    public Order(int seatNo, Meal meal, String orderedBy) {
        this.seatNo = seatNo;
        this.meal = meal;
        this.orderedBy = orderedBy;
    }

    public double price() {
        return meal.price();
    }

    public int seatNo() {
        return this.seatNo;
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

    public String orderedBy() {
        return this.orderedBy;
    }
}
