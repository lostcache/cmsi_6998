class Main {

    private static final int MIN_INT = 2;
    private static final int MAX_INT = 100;
    private static final int MAX_THREADS = 50;
    private static final Thread[] threadHandles = new Thread[MAX_THREADS];

    @SuppressWarnings("unchecked")
    private static final java.util.Queue<Integer>[] threadQueues =
        new java.util.Queue[MAX_THREADS];

    public static void main(String[] args) {
        threadQueues[0] = new java.util.LinkedList<Integer>();
        threadHandles[0] = new PrimeThread(0, 2, threadQueues, threadHandles);
        threadHandles[0].start();

        for (int i = MIN_INT; i < MAX_INT; i++) {
            threadQueues[0].add(i);
        }

        threadQueues[0].add(-1);
    }
}

class PrimeThread extends Thread {

    private int index;
    private int divisor;
    private java.util.Queue<Integer>[] threadQueues;
    private Thread[] threadHandles;

    public PrimeThread(
        int index,
        int divisor,
        java.util.Queue<Integer>[] threadQueues,
        Thread[] threadHandles
    ) {
        this.index = index;
        this.divisor = divisor;
        this.threadQueues = threadQueues;
        this.threadHandles = threadHandles;
        System.out.println(divisor);
    }

    @Override
    public void run() {
        var queue = threadQueues[index];
        while (true) {
            Integer num;

            synchronized (queue) {
                num = queue.poll();
            }

            if (num == null) {
                Thread.yield();
                continue;
            }

            if (num == -1) {
                if (
                    index + 1 < threadHandles.length &&
                    threadQueues[index + 1] != null
                ) {
                    threadQueues[index + 1].add(num);
                }
                break;
            }

            if (num % divisor != 0) {
                if (index + 1 >= threadHandles.length) {
                    continue;
                }

                synchronized (threadHandles) {
                    if (threadHandles[index + 1] == null) {
                        threadQueues[index + 1] = new java.util.LinkedList<
                            Integer
                        >();
                        threadHandles[index + 1] = new PrimeThread(
                            index + 1,
                            num,
                            threadQueues,
                            threadHandles
                        );
                        threadHandles[index + 1].start();
                    }
                }

                synchronized (threadQueues[index + 1]) {
                    threadQueues[index + 1].add(num);
                }
            }
        }
    }
}
