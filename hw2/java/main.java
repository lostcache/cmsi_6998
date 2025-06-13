class Main {

    private static final void runSim() {
        Philosopher[] philosophers = new Philosopher[5];
        Table table = new Table();

        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher(table, "Philosopher " + (i + 1));
        }

        for (Philosopher philosopher : philosophers) {
            philosopher.start();
        }
        // try {
        //     for (Philosopher philosopher : philosophers) {
        //         philosopher.join();
        //     }
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        // }
    }

    public static void main(String[] args) {
        runSim();
    }
}

class Philosopher extends Thread {

    private int seatNo;
    private double money;
    private Table table;
    public String name;

    public Philosopher(Table table, String name) {
        this.money = 200.0;
        this.seatNo = -1;
        this.table = table;
        this.name = name;
    }

    private int waitForSeat(Table table) {
        var seat = -1;
        while (seat == -1) {
            seat = table.tryToAcquireSeat();
            if (seat != -1) {
                this.seatNo = seat;
            } else {
                Thread.yield();
            }
        }

        return seat;
    }

    private Pair<Integer, Integer> waitForForks(int seatNo) {
        Pair<Integer, Integer> forkPair = null;
        while (forkPair == null) {
            forkPair = table.tryToPickUpFork(seatNo);
            if (forkPair != null) {
                return forkPair;
            }
            Thread.yield();
        }

        return forkPair;
    }

    private void eatMeal() {
        Pair<Integer, Integer> forkPair = this.waitForForks(this.seatNo);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        table.putDownForks(forkPair);
    }

    private void payBill(Order order) {
        this.money -= order.totalPrice();
    }

    private boolean outOfMoney() {
        return this.money < 0;
    }

    private Order orderMeal() {
        return this.table.orderMeal(this.seatNo);
    }

    private void leaveTable() {
        this.table.leaveTable(this.seatNo);
    }

    @Override
    public void run() {
        while (!outOfMoney()) {
            this.waitForSeat(this.table);
            Order order = this.orderMeal();
            this.eatMeal();
            this.payBill(order);
            this.leaveTable();
        }
    }
}

record Pair<A, B>(A first, B second) {
    public A first() {
        return first;
    }

    public B second() {
        return second;
    }
}

class Table {

    private static final int NUM_SEATS = 5;
    private static final int NUM_FORKS = 5;
    private final String[] waiters;
    private final java.util.Queue<Float> orderQueue;
    private final java.util.concurrent.locks.Lock[] seats;
    private final java.util.Queue<Integer> emptySeats;

    public Table() {
        this.orderQueue = new java.util.LinkedList<Float>();
        this.waiters = new String[] { "Waiter A", "Waiter B" };
        this.seats = new java.util.concurrent.locks.ReentrantLock[NUM_SEATS];
        for (int i = 0; i < NUM_SEATS; i++) {
            seats[i] = new java.util.concurrent.locks.ReentrantLock();
        }
        this.emptySeats = new java.util.LinkedList<Integer>();
        for (int i = 0; i < NUM_SEATS; i++) {
            emptySeats.add(i);
        }
    }

    public Order orderMeal(int seatNo) {
        var meal = Meal.values()[new java.util.Random()
                .nextInt(Meal.values().length)];
        return new Order(seatNo, meal);
    }

    public int tryToAcquireSeat() {
        synchronized (emptySeats) {
            var seat = emptySeats.poll();
            if (seat != null) {
                return seat;
            }
        }
        return -1;
    }

    public void putDownForks(Pair<Integer, Integer> forkPair) {
        seats[forkPair.first()].unlock();
        seats[forkPair.second()].unlock();
    }

    public Pair<Integer, Integer> tryToPickUpFork(int seatNo) {
        var leftFork = seats[seatNo].tryLock();
        var rightFork = seats[(seatNo + 1) % NUM_FORKS].tryLock();
        if (leftFork && rightFork) {
            return new Pair<>(seatNo, seatNo + 1);
        }
        return null;
    }

    public void leaveTable(int seatNo) {
        synchronized (emptySeats) {
            emptySeats.add(seatNo);
        }
    }
}

class Kitchen {

    private final String[] chefs;
    private final java.util.Queue<String> orderQueue;
    private final java.util.Queue<String> readyOrders;

    public Kitchen() {
        this.orderQueue = new java.util.LinkedList<String>();
        this.readyOrders = new java.util.LinkedList<String>();
        this.chefs = new String[] { "Chef A", "Chef B", "Chef C" };
    }
}

record Order(int seatNo, Meal meal) {
    public double totalPrice() {
        return meal.price();
    }
}

enum Meal {
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
}

class Logger {

    public static void log(String message) {
        System.out.println(message);
    }
}
