package gr.james.sampling;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.function.Supplier;

@RunWith(Parameterized.class)
public class RandomSamplingTests {

    private static final Random RANDOM = new Random();

    private static final int STREAM = 20;
    private static final int SAMPLE = 10;
    private static final int REPS = 1000000;

    private final Supplier<RandomSampling<Integer>> impl;

    public RandomSamplingTests(Supplier<RandomSampling<Integer>> impl) {
        this.impl = impl;
    }

    @Parameterized.Parameters()
    public static Collection<Supplier<RandomSampling<Integer>>> implementations() {
        final Collection<Supplier<RandomSampling<Integer>>> implementations = new ArrayList<>();
        implementations.add(() -> new WatermanSampling<>(SAMPLE, RANDOM));
        implementations.add(() -> new VitterXSampling<>(SAMPLE, RANDOM));
        implementations.add(() -> new VitterZSampling<>(SAMPLE, RANDOM));
        implementations.add(() -> new EfraimidisSampling<>(SAMPLE, RANDOM));
        return implementations;
    }

    @Test
    public void correctness() {
        final int[] d = new int[STREAM];

        for (int reps = 0; reps < REPS; reps++) {
            final RandomSampling<Integer> alg = impl.get();

            if (alg instanceof UnweightedRandomSampling) {
                for (int i = 0; i < STREAM; i++) {
                    ((UnweightedRandomSampling<Integer>) alg).feed(i);
                }
            } else if (alg instanceof WeightedRandomSampling) {
                for (int i = 0; i < STREAM; i++) {
                    ((WeightedRandomSampling<Integer>) alg).feed(i, 1.0);
                }
            }

            for (int s : alg.sample()) {
                d[s]++;
            }
        }

        for (int c : d) {
            final double expected = (double) REPS * SAMPLE / STREAM;
            final double actual = (double) c;
            Assert.assertEquals("RandomSamplingTests.correctness", 1, actual / expected, 1e-2);
        }
    }

    @Test
    public void performance() {
        final RandomSampling<Integer> alg = impl.get();
        if (alg instanceof UnweightedRandomSampling) {
            for (int i = 0; i < 100000000; i++) {
                ((UnweightedRandomSampling<Integer>) alg).feed(i);
            }
        } else if (alg instanceof WeightedRandomSampling) {
            for (int i = 0; i < 100000000; i++) {
                ((WeightedRandomSampling<Integer>) alg).feed(i, 1.0);
            }
        }
    }

}
