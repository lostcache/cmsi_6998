class Main {

    public static void main(String[] args) {
        Kitchen kitchen = new Kitchen();
        Table table = new Table();

        Restaurant restaurant = new Restaurant(table, kitchen);

        restaurant.start();

        restaurant.shutdown();

        System.out.println("✅ Simulation completed");
    }
}
