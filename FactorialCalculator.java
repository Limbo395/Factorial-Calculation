import java.util.concurrent.*;

public class FactorialCalculator {
    private static final ConcurrentHashMap<Integer, Long> factorialMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Future<Long>[] futures = new Future[5];

        for (int i = 0; i < 5; i++) {
            final int number = i + 1; // числа від 1 до 5
            futures[i] = executorService.submit(new Callable<Long>() {
                @Override
                public Long call() {
                    Long factorial = computeFactorial(number);
                    if (!Thread.currentThread().isInterrupted()) {
                        factorialMap.put(number, factorial);
                    }
                    return factorial;
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        for (int i = 0; i < futures.length; i++) {
            try {
                Long result = futures[i].get();
                if (futures[i].isCancelled()) {
                    System.out.println("Обчислення для " + (i + 1) + " скасоване.");
                } else {
                    System.out.println("Факторіал " + (i + 1) + ": " + result);
                }
            } catch (CancellationException e) {
                System.out.println("Обчислення для " + (i + 1) + " було скасоване.");
            }
        }

        System.out.println("Факторіали в мапі: " + factorialMap);
    }

    private static Long computeFactorial(int number) {
        long result = 1;
        for (int i = 1; i <= number; i++) {
            result *= i;
        }
        return result;
    }
}