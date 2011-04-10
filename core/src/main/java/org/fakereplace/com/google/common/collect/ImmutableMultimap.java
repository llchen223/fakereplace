/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fakereplace.com.google.common.collect;

import org.fakereplace.com.google.common.annotations.GwtCompatible;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.fakereplace.com.google.common.base.Preconditions.checkNotNull;


/**
 * An immutable {@link Multimap}. Does not permit null keys or values.
 * <p/>
 * <p>Unlike {@link Multimaps#unmodifiableMultimap(Multimap)}, which is
 * a <i>view</i> of a separate multimap which can still change, an instance of
 * {@code ImmutableMultimap} contains its own data and will <i>never</i>
 * change. {@code ImmutableMultimap} is convenient for
 * {@code public static final} multimaps ("constant multimaps") and also lets
 * you easily make a "defensive copy" of a multimap provided to your class by
 * a caller.
 * <p/>
 * <p><b>Note</b>: Although this class is not final, it cannot be subclassed as
 * it has no public or protected constructors. Thus, instances of this class
 * are guaranteed to be immutable.
 *
 * @author Jared Levy
 */
@GwtCompatible
public abstract class ImmutableMultimap<K, V>
        implements Multimap<K, V>, Serializable {

    /**
     * Returns an empty multimap.
     */
    public static <K, V> ImmutableMultimap<K, V> of() {
        return ImmutableListMultimap.of();
    }

    /**
     * Returns an immutable multimap containing a single entry.
     */
    public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1) {
        return ImmutableListMultimap.of(k1, v1);
    }

    /**
     * Returns an immutable multimap containing the given entries, in order.
     */
    public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2) {
        return ImmutableListMultimap.of(k1, v1, k2, v2);
    }

    /**
     * Returns an immutable multimap containing the given entries, in order.
     */
    public static <K, V> ImmutableMultimap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3) {
        return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3);
    }

    /**
     * Returns an immutable multimap containing the given entries, in order.
     */
    public static <K, V> ImmutableMultimap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    /**
     * Returns an immutable multimap containing the given entries, in order.
     */
    public static <K, V> ImmutableMultimap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    // looking for of() with > 5 entries? Use the builder instead.

    /**
     * Returns a new builder. The generated builder is equivalent to the builder
     * created by the {@link Builder} constructor.
     */
    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }

    /**
     * Multimap for {@link ImmutableMultimap.Builder} that maintains key and
     * value orderings, allows duplicate values, and performs better than
     * {@link LinkedListMultimap}.
     */
    private static class BuilderMultimap<K, V> extends AbstractMultimap<K, V> {
        BuilderMultimap() {
            super(new LinkedHashMap<K, Collection<V>>());
        }

        @Override
        Collection<V> createCollection() {
            return Lists.newArrayList();
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * A builder for creating immutable multimap instances, especially
     * {@code public static final} multimaps ("constant multimaps"). Example:
     * <pre>   {@code
     * <p/>
     *   static final Multimap<String, Integer> STRING_TO_INTEGER_MULTIMAP =
     *       new ImmutableMultimap.Builder<String, Integer>()
     *           .put("one", 1)
     *           .putAll("several", 1, 2, 3)
     *           .putAll("many", 1, 2, 3, 4, 5)
     *           .build();}</pre>
     * <p/>
     * <p>Builder instances can be reused - it is safe to call {@link #build}
     * multiple times to build multiple multimaps in series. Each multimap
     * contains the key-value mappings in the previously created multimaps.
     */
    public static class Builder<K, V> {
        private final Multimap<K, V> builderMultimap = new BuilderMultimap<K, V>();

        /**
         * Creates a new builder. The returned builder is equivalent to the builder
         * generated by {@link ImmutableMultimap#builder}.
         */
        public Builder() {
        }

        /**
         * Adds a key-value mapping to the built multimap.
         */
        public Builder<K, V> put(K key, V value) {
            builderMultimap.put(checkNotNull(key), checkNotNull(value));
            return this;
        }

        /**
         * Stores a collection of values with the same key in the built multimap.
         *
         * @throws NullPointerException if {@code key}, {@code values}, or any
         *                              element in {@code values} is null. The builder is left in an invalid
         *                              state.
         */
        public Builder<K, V> putAll(K key, Iterable<? extends V> values) {
            Collection<V> valueList = builderMultimap.get(checkNotNull(key));
            for (V value : values) {
                valueList.add(checkNotNull(value));
            }
            return this;
        }

        /**
         * Stores an array of values with the same key in the built multimap.
         *
         * @throws NullPointerException if the key or any value is null. The builder
         *                              is left in an invalid state.
         */
        public Builder<K, V> putAll(K key, V... values) {
            return putAll(key, Arrays.asList(values));
        }

        /**
         * Stores another multimap's entries in the built multimap. The generated
         * multimap's key and value orderings correspond to the iteration ordering
         * of the {@code multimap.asMap()} view, with new keys and values following
         * any existing keys and values.
         *
         * @throws NullPointerException if any key or value in {@code multimap} is
         *                              null. The builder is left in an invalid state.
         */
        public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap) {
            for (Map.Entry<? extends K, ? extends Collection<? extends V>> entry
                    : multimap.asMap().entrySet()) {
                putAll(entry.getKey(), entry.getValue());
            }
            return this;
        }

        /**
         * Returns a newly-created immutable multimap.
         */
        public ImmutableMultimap<K, V> build() {
            return copyOf(builderMultimap);
        }
    }

    /**
     * Returns an immutable multimap containing the same mappings as
     * {@code multimap}. The generated multimap's key and value orderings
     * correspond to the iteration ordering of the {@code multimap.asMap()} view.
     * <p/>
     * <p><b>Note:</b> Despite what the method name suggests, if
     * {@code multimap} is an {@code ImmutableMultimap}, no copy will actually be
     * performed, and the given multimap itself will be returned.
     *
     * @throws NullPointerException if any key or value in {@code multimap} is
     *                              null
     */
    public static <K, V> ImmutableMultimap<K, V> copyOf(
            Multimap<? extends K, ? extends V> multimap) {
        if (multimap instanceof ImmutableMultimap) {
            @SuppressWarnings("unchecked") // safe since multimap is not writable
                    ImmutableMultimap<K, V> kvMultimap
                    = (ImmutableMultimap<K, V>) multimap;
            return kvMultimap;
        } else {
            return ImmutableListMultimap.copyOf(multimap);
        }
    }

    final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
    final transient int size;

    // These constants allow the deserialization code to set final fields. This
    // holder class makes sure they are not initialized unless an instance is
    // deserialized.
    static class FieldSettersHolder {
        // Eclipse doesn't like the raw ImmutableMultimap
        @SuppressWarnings("unchecked")
        static final Serialization.FieldSetter<ImmutableMultimap>
                MAP_FIELD_SETTER = Serialization.getFieldSetter(
                ImmutableMultimap.class, "map");
        // Eclipse doesn't like the raw ImmutableMultimap
        @SuppressWarnings("unchecked")
        static final Serialization.FieldSetter<ImmutableMultimap>
                SIZE_FIELD_SETTER = Serialization.getFieldSetter(
                ImmutableMultimap.class, "size");
    }

    ImmutableMultimap(ImmutableMap<K, ? extends ImmutableCollection<V>> map,
                      int size) {
        this.map = map;
        this.size = size;
    }

    // mutators (not supported)

    /**
     * Guaranteed to throw an exception and leave the multimap unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    public ImmutableCollection<V> removeAll(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the multimap unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    public ImmutableCollection<V> replaceValues(K key,
                                                Iterable<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the multimap unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an immutable collection of the values for the given key.  If no
     * mappings in the multimap have the provided key, an empty immutable
     * collection is returned. The values are in the same order as the parameters
     * used to build this multimap.
     */
    public abstract ImmutableCollection<V> get(K key);

    /**
     * Guaranteed to throw an exception and leave the multimap unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    public boolean put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the multimap unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    public boolean putAll(K key, Iterable<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the multimap unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the multimap unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    // accessors

    public boolean containsEntry(Object key, Object value) {
        Collection<V> values = map.get(key);
        return values != null && values.contains(value);
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        for (Collection<V> valueCollection : map.values()) {
            if (valueCollection.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Multimap) {
            Multimap<?, ?> that = (Multimap<?, ?>) object;
            return this.map.equals(that.asMap());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    // views

    /**
     * Returns an immutable set of the distinct keys in this multimap. These keys
     * are ordered according to when they first appeared during the construction
     * of this multimap.
     */
    public ImmutableSet<K> keySet() {
        return map.keySet();
    }

    /**
     * Returns an immutable map that associates each key with its corresponding
     * values in the multimap.
     */
    @SuppressWarnings("unchecked") // a widening cast
    public ImmutableMap<K, Collection<V>> asMap() {
        return (ImmutableMap) map;
    }

    private transient ImmutableCollection<Map.Entry<K, V>> entries;

    /**
     * Returns an immutable collection of all key-value pairs in the multimap. Its
     * iterator traverses the values for the first key, the values for the second
     * key, and so on.
     */
    public ImmutableCollection<Map.Entry<K, V>> entries() {
        ImmutableCollection<Map.Entry<K, V>> result = entries;
        return (result == null)
                ? (entries = new EntryCollection<K, V>(this)) : result;
    }

    private static class EntryCollection<K, V>
            extends ImmutableCollection<Map.Entry<K, V>> {
        final ImmutableMultimap<K, V> multimap;

        EntryCollection(ImmutableMultimap<K, V> multimap) {
            this.multimap = multimap;
        }

        @Override
        public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
            final Iterator<? extends Map.Entry<K, ? extends ImmutableCollection<V>>>
                    mapIterator = this.multimap.map.entrySet().iterator();

            return new UnmodifiableIterator<Map.Entry<K, V>>() {
                K key;
                Iterator<V> valueIterator;

                public boolean hasNext() {
                    return (key != null && valueIterator.hasNext())
                            || mapIterator.hasNext();
                }

                public Map.Entry<K, V> next() {
                    if (key == null || !valueIterator.hasNext()) {
                        Map.Entry<K, ? extends ImmutableCollection<V>> entry
                                = mapIterator.next();
                        key = entry.getKey();
                        valueIterator = entry.getValue().iterator();
                    }
                    return Maps.immutableEntry(key, valueIterator.next());
                }
            };
        }

        public int size() {
            return multimap.size();
        }

        @Override
        public boolean contains(Object object) {
            if (object instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
                return multimap.containsEntry(entry.getKey(), entry.getValue());
            }
            return false;
        }

        private static final long serialVersionUID = 0;
    }

    private transient ImmutableMultiset<K> keys;

    /**
     * Returns a collection, which may contain duplicates, of all keys. The number
     * of times a key appears in the returned multiset equals the number of
     * mappings the key has in the multimap. Duplicate keys appear consecutively
     * in the multiset's iteration order.
     */
    public ImmutableMultiset<K> keys() {
        ImmutableMultiset<K> result = keys;
        return (result == null) ? (keys = createKeys()) : result;
    }

    private ImmutableMultiset<K> createKeys() {
        ImmutableMultiset.Builder<K> builder = ImmutableMultiset.builder();
        for (Map.Entry<K, ? extends ImmutableCollection<V>> entry
                : map.entrySet()) {
            builder.addCopies(entry.getKey(), entry.getValue().size());
        }
        return builder.build();
    }

    private transient ImmutableCollection<V> values;

    /**
     * Returns an immutable collection of the values in this multimap. Its
     * iterator traverses the values for the first key, the values for the second
     * key, and so on.
     */
    public ImmutableCollection<V> values() {
        ImmutableCollection<V> result = values;
        return (result == null) ? (values = new Values<V>(this)) : result;
    }

    private static class Values<V> extends ImmutableCollection<V> {
        final Multimap<?, V> multimap;

        Values(Multimap<?, V> multimap) {
            this.multimap = multimap;
        }

        @Override
        public UnmodifiableIterator<V> iterator() {
            final Iterator<? extends Map.Entry<?, V>> entryIterator
                    = multimap.entries().iterator();
            return new UnmodifiableIterator<V>() {
                public boolean hasNext() {
                    return entryIterator.hasNext();
                }

                public V next() {
                    return entryIterator.next().getValue();
                }
            };
        }

        public int size() {
            return multimap.size();
        }

        private static final long serialVersionUID = 0;
    }

    private static final long serialVersionUID = 0;
}
