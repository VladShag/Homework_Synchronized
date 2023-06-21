import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        Thread mapLog = new Thread(() -> {
            int maxValue = 0;
            int maxKey = 0;
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    if (Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey() != maxKey || Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getValue() != maxValue) {
                        System.out.println("На данный момент самое частое количество повторений: " +
                                Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey() + " встречается " +
                                Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getValue() + " раз");
                        maxKey = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
                        maxValue = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getValue();
                    }
                }
            }
        });
        mapLog.setPriority(Thread.MAX_PRIORITY);
        mapLog.start();
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            threads.add(new Thread(() -> {
                synchronized (sizeToFreq) {
                    String s = generateRoute("RLRFR", 100);
                    long resultCalc = s.chars().filter(c -> c == 'R').count();
                    int resultCalcInt = (int) resultCalc;
                    if (sizeToFreq.containsKey(resultCalcInt)) {
                        Integer quantity = sizeToFreq.get(resultCalcInt);
                        sizeToFreq.put((int) resultCalc, quantity + 1);
                    } else {
                        sizeToFreq.put((int) resultCalc, 1);
                    }
                    sizeToFreq.notify();
                }
            }));
            threads.get(i).start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        mapLog.interrupt();
        List<Map.Entry<Integer, Integer>> valuesList = new ArrayList<>(sizeToFreq.entrySet());
        valuesList.sort(Map.Entry.comparingByValue());
        System.out.println("Самое частое количество повторений - " + valuesList.get(valuesList.size() - 1).getKey() + " встречается " + valuesList.get(valuesList.size() - 1).getValue() + " раз " + " \nДругие значения: ");
        for (int i = valuesList.size() - 2; i >= 0; i--) {
            System.out.println(valuesList.get(i).getKey() + " встречается " + valuesList.get(i).getValue() + " раз");
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
