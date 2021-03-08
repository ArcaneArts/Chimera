package art.arcane.chimera.core.protocol.generation;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Progressive<T> {
    public double getProgress();

    public boolean isComplete();

    public void setListener(Consumer<Double> l);

    /**
     * Gets the value if there is one, if its not completed, null is returned
     *
     * @return the value or null if not complete
     */
    public T get();

    /**
     * Waits until the result is complete
     *
     * @return the value of this progressive
     */
    public T complete();

    public static <T> Progressive<T> of(Function<Consumer<Double>, T> function) {
        AtomicReference<Double> progress = new AtomicReference<>(0D);
        AtomicReference<Consumer<Double>> listenerReference = new AtomicReference<>();
        Consumer<Double> c = new Consumer<Double>() {
            @Override
            public void accept(Double d) {
                progress.set(d);

                if (listenerReference.get() != null) {
                    listenerReference.get().accept(d);
                }
            }
        };
        Future<T> future = ForkJoinPool.commonPool().submit(() -> {
            T v = function.apply(c);
            if (listenerReference.get() != null) {
                listenerReference.get().accept(1D);
            }

            return v;
        });
        Progressive<T> p = new Progressive<T>() {
            @Override
            public double getProgress() {
                return progress.get();
            }

            @Override
            public boolean isComplete() {
                return future.isDone();
            }

            @Override
            public void setListener(Consumer<Double> l) {
                listenerReference.set(l);
            }

            @Override
            public T get() {

                if (!isComplete()) {
                    return null;
                }

                return complete();
            }

            @Override
            public T complete() {
                try {
                    return future.get(1, TimeUnit.DAYS);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        return p;
    }
}
