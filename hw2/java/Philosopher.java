public class Philosopher extends Thread {

    private int seatNo;
    private Table table;
    private double balance;

    public Philosopher(Table table) {
        this.table = table;
        this.balance = 15;
        this.sitAtTable();
    }

    private void leaveTable() {
        assert this.seatNo != -1;

        this.table.releaseSeat(this.seatNo);
        this.seatNo = -1;
    }

    private void leaveTableAndJoinAfterInterval(long interval) {
        this.leaveTable();

        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        this.sitAtTable();
    }

    private void sitAtTable() {
        this.seatNo = this.table.requestSeat();
    }

    public int seatNo(int seatNo, Table table) {
        this.seatNo = seatNo;
        this.table = table;
        return seatNo;
    }

    private Order orderMeal(int seatNo) {
        assert (seatNo != -1);

        Meal meal = Meal.getRandomMeal();
        Order order = new Order(seatNo, meal);
        System.out.println(
            "📝 " + Thread.currentThread().getName() + " orders " + meal
        );
        return order;
    }

    private Pair<Integer, Integer> tryToAcquireChopsticks() {
        assert (this.seatNo != -1);

        Pair<Integer, Integer> chopsticks = null;
        while (chopsticks == null) {
            chopsticks = this.table.tryGetChopsticks(this.seatNo);
            if (chopsticks == null) {
                Thread.yield();
            }
        }
        return chopsticks;
    }

    private Order waitForCookedMeal(Order order) {
        Order cooked = null;
        while (cooked == null) {
            cooked = this.table.queryOrder(this.seatNo);
            if (cooked == null) {
                Thread.yield();
            }
        }
        return cooked;
    }

    private void eat(Order order) {
        System.out.println(
            "🍴 " + Thread.currentThread().getName() + " eating " + order.meal()
        );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void putDownChopsticks() {
        this.table.putDownChopsticks(this.seatNo);
    }

    private void payForMeal(Order order) {
        this.balance -= order.price();
        System.out.println(
            "💳 " +
            Thread.currentThread().getName() +
            " paid $" +
            order.price() +
            " (balance: $" +
            String.format("%.2f", balance) +
            ")"
        );
    }

    private boolean hasEnoughBalance() {
        if (this.balance < 0) {
            System.out.println(
                "💸 " +
                Thread.currentThread().getName() +
                " is out of money! Final balance: $" +
                String.format("%.2f", balance)
            );
            return false;
        }

        return true;
    }

    private void think(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (this.hasEnoughBalance()) {
            this.think((long) (Math.random() * 1000 + 500));
            Order order = this.orderMeal(this.seatNo);
            this.table.addToOrderInQueue(order);
            Order cooked = this.waitForCookedMeal(order);
            this.tryToAcquireChopsticks();
            this.eat(cooked);
            this.putDownChopsticks();
            this.payForMeal(cooked);
            this.leaveTableAndJoinAfterInterval(
                    (long) (Math.random() * 1000 + 500)
                );
        }

        System.out.println(
            "👋 " + Thread.currentThread().getName() + " leaving"
        );
    }
}
