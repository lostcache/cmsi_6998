public class Table {

    private static final int NUM_CHOPSTICKS = 5;
    private static final int NUM_SEATS = 5;
    private final java.util.concurrent.locks.Lock[] chopsticks =
        new java.util.concurrent.locks.ReentrantLock[NUM_CHOPSTICKS];
    private final java.util.concurrent.ConcurrentLinkedQueue<
        Integer
    > emptySeats = new java.util.concurrent.ConcurrentLinkedQueue<>();
    private Restaurant restaurant = null;

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

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
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

    public void putDownChopsticks(Integer seatNo) {
        assert seatNo != -1;
        int left = seatNo % NUM_CHOPSTICKS;
        int right = (seatNo + 1) % NUM_CHOPSTICKS;

        chopsticks[left].unlock();
        chopsticks[right].unlock();
    }

    public synchronized Waiter getRandomAvailableWaiter() {
        return this.restaurant.getRandomAvailableWaiter();
    }

    public Philosopher getPhilosopher(int seatNo) {
        return this.restaurant.getPhilosopher(seatNo);
    }
}
