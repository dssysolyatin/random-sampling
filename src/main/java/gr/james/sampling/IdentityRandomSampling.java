package gr.james.sampling;

import java.util.*;

/**
 * A {@link RandomSampling} decorator that doesn't permit duplicate items.
 * <p>
 * Example:
 * <pre><code>
 * IdentityRandomSampling&lt;Integer, WatermanSampling&lt;Integer&gt;&gt; irs =
 *     new IdentityRandomSampling&lt;&gt;(new WatermanSampling&lt;&gt;(10, new Random()));
 * </code></pre>
 *
 * @param <T>  the element type
 * @param <RS> the {@link RandomSampling} implementation type
 */
@Deprecated
class IdentityRandomSampling<T, RS extends RandomSampling<T>> implements RandomSampling<T> {
    private final RS source;
    private final Set<T> set;
    private final Set<T> unmodifiableSample;

    /**
     * Decorates {@code source} as an {@link IdentityRandomSampling}.
     * <p>
     * The caller must ensure that {@code source} will not be accessed directly after this point.
     *
     * @param source the source {@link RandomSampling} implementation
     * @throws NullPointerException     if {@code source} is {@code null}
     * @throws IllegalArgumentException if {@code source} already had some items fed
     */
    IdentityRandomSampling(RS source) {
        if (source == null) {
            throw new NullPointerException();
        }
        if (source.sample().size() != 0) {
            throw new IllegalArgumentException();
        }
        this.source = source;
        this.set = new HashSet<>();
        this.unmodifiableSample = new AbstractSet<T>() {
            final Collection<T> sample = source.sample();

            @Override
            public Iterator<T> iterator() {
                return sample.iterator();
            }

            @Override
            public int size() {
                return sample.size();
            }
        };
    }

    @Override
    public boolean feed(T item) {
        if (item == null) {
            throw new NullPointerException();
        }
        if (!set.add(item)) {
            throw new UnsupportedOperationException();
        }
        return source.feed(item);
    }

    @Override
    public boolean feed(Iterator<T> items) {
        return source.feed(items);
    }

    @Override
    public boolean feed(Iterable<T> items) {
        return source.feed(items);
    }

    @Override
    public int sampleSize() {
        return source.sampleSize();
    }

    @Override
    public long streamSize() {
        return source.streamSize();
    }

    @Override
    public Set<T> sample() {
        assert source.sample().stream().distinct().count() == source.sample().stream().distinct().count();
        return this.unmodifiableSample;
    }
}
