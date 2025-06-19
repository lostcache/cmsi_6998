public class Philosopher extends Thread {

    private String name;
    private double balance;
    private Table table;
    private int seatNo = -1;
    private volatile Order order = null;

    public Philosopher(Table table, String name) {
        this.name = name;
        this.table = table;
        this.balance = 15;
    }

    private void leaveTable() {
        assert this.seatNo != -1;

        this.table.releaseSeat(this.seatNo);
        this.seatNo = -1;
        Logger.log(
            "🚪 Philosopher " +
            this.name +
            " leaves the table with balance $" +
            String.format("%.2f", this.balance)
        );
    }

    private void leaveForInterval(long interval) {
        assert this.seatNo != -1;
        this.leaveTable();

        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int seatNo(int seatNo, Table table) {
        this.seatNo = seatNo;
        this.table = table;
        return seatNo;
    }

    private Order getOrderFromMenu() {
        assert (this.seatNo != -1);

        if (this.cookedOrder != null) Logger.log(
            "the order for " +
            this.name +
            "is supposed to be null but is " +
            this.cookedOrder.meal()
        );
        assert (this.cookedOrder == null);

        Meal meal = Meal.getRandomMeal();
        Order order = new Order(this.seatNo, meal, this);
        Logger.log("📝 " + this.name + " orders " + meal);
        return order;
    }

    private Pair<Integer, Integer> waitAcquireChopsticks() {
        Logger.log(
            "⏳ " +
            this.name +
            " waits to acquire chopsticks at seat " +
            this.seatNo
        );
        assert (this.seatNo != -1);

        Pair<Integer, Integer> chopsticks = null;
        while (chopsticks == null) {
            chopsticks = this.table.tryGetChopsticks(this.seatNo);
            if (chopsticks == null) {
                Thread.yield();
            }
        }

        Logger.log("🥢 " + this.name + " acquires chopsticks " + chopsticks);
        return chopsticks;
    }

    private void waitForPlacedOrder() {
        while (this.cookedOrder == null) {
            Thread.yield();
            continue;
        }
    }

    private void eat() {
        System.out.println("🍴 " + this.name + " eating " + cookedOrder.meal());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void putDownChopsticks() {
        assert this.seatNo != -1;
        this.table.putDownChopsticks(this.seatNo);
    }

    private void payForMeal() {
        this.balance -= this.cookedOrder.price();
        System.out.println(
            "💳 " +
            this.name +
            " paid $" +
            this.cookedOrder.price() +
            " (balance: $" +
            String.format("%.2f", this.balance) +
            ")"
        );
    }

    private boolean hasEnoughBalance() {
        if (this.balance < 0) {
            System.out.println(
                "💸 " +
                this.name +
                " is out of money! Final balance: $" +
                String.format("%.2f", this.balance)
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

    private void takeSeatAtTheTable() {
        assert this.seatNo == -1;
        if (this.cookedOrder != null) {
            Logger.log(
                "the order is supposed to be null " +
                this.cookedOrder.meal() +
                " for " +
                this.name
            );
        }
        assert this.cookedOrder == null;
        int seatNo = this.table.requestSeat();
        assert seatNo != -1;
        this.seatNo = seatNo;
        Logger.log(this.name + " takes seat " + seatNo);
    }

    private boolean tryToGiveOrderToRandomWaiter(Order order) {
        assert this.cookedOrder == null;
        assert this.seatNo != -1;

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 2000) {
            Waiter waiter = this.table.getRandomAvailableWaiter();
            if (waiter == null) {
                Thread.yield();
                continue;
            }

            Logger.log(
                "🤝 " +
                this.name +
                " tries to give order to waiter: " +
                waiter.name()
            );

            waiter.takeOrder(order);
            return true;
        }

        return false;
    }

    public void serveOrder(Order order) {
        this.order = order;
    }

    public String name() {
        return this.name;
    }

    @Override
    public void run() {
        while (
            this.hasEnoughBalance() && !Thread.currentThread().isInterrupted()
        ) {
            this.takeSeatAtTheTable();
            this.think((long) (Math.random() * 1000 + 500));

            Order order = this.getOrderFromMenu();
            boolean success = this.tryToGiveOrderToRandomWaiter(order);
            if (!success) {
                Logger.log(
                    this.name +
                    " failed to give order to waiter, leaving table temporarily"
                );
                this.leaveForInterval((long) (1000));
                continue;
            }

            success = this.waitForOrder(order);
            if (!success) {
                Logger.log(
                    "Order failed, was refunded, leaving with refund of $5"
                );
                this.leaveForInterval((long) (1000));
                continue;
            }
            Logger.log(this.name + " got order: " + order.meal());

            this.waitAcquireChopsticks();

            this.eat();
            this.putDownChopsticks();
            this.payForMeal();
            this.leaveForInterval((long) (Math.random() * 1000 + 500));
        }

        System.out.println("👋 " + this.name + " leaving for good.");
    }
}
