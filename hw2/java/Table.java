public class Table {

    private static final int NUM_CHOPSTICKS = 5;
    private static final int NUM_SEATS = 5;
    private final java.util.concurrent.locks.Lock[] chopsticks =
        new java.util.concurrent.locks.ReentrantLock[NUM_CHOPSTICKS];
    private final java.util.Queue<Order> orderInQueue =
        new java.util.concurrent.ConcurrentLinkedQueue<>();
    private final java.util.Queue<Order> orderOutQueue =
        new java.util.concurrent.ConcurrentLinkedQueue<>();
    private final java.util.concurrent.ConcurrentLinkedQueue<
        Integer
    > emptySeats = new java.util.concurrent.ConcurrentLinkedQueue<>();

    public Table() {
        for (int i = 0; i < chopsticks.length; i++) {
            chopsticks[i] = new java.util.concurrent.locks.ReentrantLock();
        }

        for (int i = 0; i < NUM_SEATS; i++) {
            emptySeats.add(i);
        }
    }

    public int requestSeat() {
        return emptySeats.poll();
    }

    public void releaseSeat(int seatNo) {
        emptySeats.add(seatNo);
    }

    public void addToOrderOutQueue(Order order) {
        orderOutQueue.add(order);
    }

    public void addToOrderInQueue(Order order) {
        orderInQueue.add(order);
    }

    public Order getFromOrderInQueue() {
        return orderInQueue.poll();
    }

    public Order getFromOrderOutQueue() {
        return orderOutQueue.poll();
    }

    public Pair<Integer, Integer> tryGetChopsticks(Integer seatNo) {
        int left = seatNo % NUM_CHOPSTICKS;
        int right = (seatNo + 1) % NUM_CHOPSTICKS;

        var leftChopStick = this.chopsticks[left].tryLock();

        if (!leftChopStick) {
            return null;
        }

        var rightChopStick = this.chopsticks[right].tryLock();

        if (!rightChopStick) {
            chopsticks[left].unlock();
            return null;
        }

        return new Pair<>(left, right);
    }

    public Order queryOrder(Integer seatNo) {
        Order order = null;
        java.util.Iterator<Order> iterator = orderOutQueue.iterator();
        while (iterator.hasNext()) {
            order = iterator.next();
            if (order.seatNo() == seatNo) {
                iterator.remove();
                return order;
            }
        }
        return null;
    }

    public void putDownChopsticks(Integer seatNo) {
        int left = seatNo % NUM_CHOPSTICKS;
        int right = (seatNo + 1) % NUM_CHOPSTICKS;

        chopsticks[left].unlock();
        chopsticks[right].unlock();
    }
}
