package gr.james.sampling;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a weighted reservoir sampling algorithm.
 * <p>
 * A weighted reservoir sampling algorithm randomly chooses a sample of {@code k} items from a list {@code S} containing
 * {@code n} items, where {@code n} may be unknown. Additionally, a weight is assigned to each item of the stream. The
 * interpretation of the weight may be different for each algorithm. The contract, however, is that a higher value
 * suggests a higher probability for an item to be included in the sample. Implementations may define certain
 * restrictions on the values of weight. These restrictions must be mentioned in the documentation and an
 * {@link IllegalWeightException} must be thrown to indicate such violations.
 * <p>
 * Classes that implement this interface have a static method with signature
 * <pre><code>
 * public static &lt;E&gt; WeightedRandomSamplingCollector&lt;E&gt; weightedCollector(int sampleSize, Random random)
 * </code></pre>
 * that returns a {@link WeightedRandomSamplingCollector} to use with the Java 8 stream API.
 * <p>
 * Classes that implement this interface have constant space complexity in respect to the stream size.
 *
 * @param <T> the item type
 * @author Giorgos Stamatelatos
 * @see <a href="https://en.wikipedia.org/wiki/Reservoir_sampling">Reservoir sampling @ Wikipedia</a>
 * @see RandomSampling
 */
public interface WeightedRandomSampling<T> extends RandomSampling<T> {
    /**
     * Feed an item along with its weight from the stream to the algorithm.
     *
     * @param item   the item to feed to the algorithm
     * @param weight the weight assigned to this item
     * @return this instance
     * @throws NullPointerException    if {@code item} is {@code null}
     * @throws IllegalWeightException  if {@code weight} is incompatible with the algorithm
     * @throws StreamOverflowException if the internal state of the algorithm has overflown
     */
    WeightedRandomSampling<T> feed(T item, double weight);

    /**
     * Feed an {@link Iterator} of items of type {@code T} along with their weights to the algorithm.
     * <p>
     * This method is equivalent to invoking the method {@link #feed(Object, double)} for each item in {@code items}
     * along with its respective entry in {@code weights}.
     *
     * @param items   the items to feed to the algorithm
     * @param weights the weights assigned to the {@code items}
     * @return this instance
     * @throws NullPointerException     if {@code items} is {@code null} or {@code weights} is {@code null} or any item
     *                                  in {@code items} is {@code null} or any weight in {@code weights} is
     *                                  {@code null}
     * @throws IllegalArgumentException if {@code items} and {@code weights} are not of same size
     * @throws IllegalWeightException   if any of the weights in {@code weights} is incompatible with the algorithm
     * @throws StreamOverflowException  if any subsequent calls to {@link #feed(Object, double)} causes
     *                                  {@code StreamOverflowException}
     */
    default WeightedRandomSampling<T> feed(Iterator<T> items, Iterator<Double> weights) {
        while (items.hasNext() && weights.hasNext()) {
            feed(items.next(), weights.next());
        }
        if (items.hasNext() || weights.hasNext()) {
            throw new IllegalArgumentException("Items and weights size mismatch");
        }
        return this;
    }

    /**
     * Feed an {@link Map} of items of type {@code T} along with their weights to the algorithm.
     * <p>
     * This method is equivalent to invoking the method {@link #feed(Object, double)} for each entry in {@code items}.
     *
     * @param items the items to feed to the algorithm
     * @return this instance
     * @throws NullPointerException    if {@code items} is {@code null} or any key or value in {@code items} is
     *                                 {@code null}
     * @throws IllegalWeightException  if any of the weights in the values of {@code items} is incompatible with the
     *                                 algorithm
     * @throws StreamOverflowException if any subsequent calls to {@link #feed(Object, double)} causes
     *                                 {@code StreamOverflowException}
     */
    default WeightedRandomSampling<T> feed(Map<T, Double> items) {
        for (Map.Entry<T, Double> e : items.entrySet()) {
            feed(e.getKey(), e.getValue());
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method uses the value {@code 1.0} as weight and is equivalent to
     * <pre><code>
     * feed(item, 1.0);
     * </code></pre>
     *
     * @param item {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException    {@inheritDoc}
     * @throws StreamOverflowException {@inheritDoc}
     */
    @Override
    default WeightedRandomSampling<T> feed(T item) {
        feed(item, 1.0);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method uses the value {@code 1.0} as weight.
     *
     * @param items {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException    {@inheritDoc}
     * @throws StreamOverflowException {@inheritDoc}
     */
    @Override
    default WeightedRandomSampling<T> feed(Iterator<T> items) {
        while (items.hasNext()) {
            feed(items.next());
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method uses the value {@code 1.0} as weight.
     *
     * @param items {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException    {@inheritDoc}
     * @throws StreamOverflowException {@inheritDoc}
     */
    @Override
    default WeightedRandomSampling<T> feed(Iterable<T> items) {
        for (T item : items) {
            feed(item);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    int sampleSize();

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    long streamSize();

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    Collection<T> sample();
}
