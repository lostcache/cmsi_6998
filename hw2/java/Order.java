public record Order(int seatNo, Meal meal) {
    public double price() {
        return meal.price();
    }

    public int seatNo() {
        return this.seatNo;
    }
}
