public enum Meal {
    Paella(1, 13.25),
    Wu_Hsiang_Chi(2, 10.00),
    Bogrács_Gulyás(3, 11.25),
    Spanokopita(4, 6.50),
    Moui_Nagden(5, 12.95),
    Sambal_Goreng_Udang(6, 14.95);

    private final int id;
    private final double price;

    private Meal(int id, double price) {
        this.id = id;
        this.price = price;
    }

    public int id() {
        return id;
    }

    public double price() {
        return price;
    }

    public static Meal getRandomMeal() {
        Meal[] meals = Meal.values();
        int randomIndex = (int) (Math.random() * meals.length);
        return meals[randomIndex];
    }
}
