/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.streams.kstream;

import org.apache.kafka.common.annotation.InterfaceStability;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;

/**
 * {@link KGroupedTable} is an abstraction of a <i>grouped changelog stream</i> from a primary-keyed table,
 * usually on a different grouping key than the original primary key.
 * <p>
 * It is an intermediate representation after a re-grouping of a {@link KTable} before an aggregation is applied
 * to the new partitions resulting in a new {@link KTable}.
 *
 * @param <K> Type of primary keys
 * @param <V> Type of value changes
 */
@InterfaceStability.Unstable
public interface KGroupedTable<K, V> {

    /**
     * Combine updating values of this stream by the selected key into a new instance of {@link KTable}.
     * The resulting {@link KTable} will be materialized in a local state
     * store with the given store name. Also a changelog topic named "${applicationId}-${storeName}-changelog"
     * will be automatically created in Kafka for failure recovery, where "applicationID"
     * is specified by the user in {@link org.apache.kafka.streams.StreamsConfig}.
     *
     * @param adder         the instance of {@link Reducer} for addition
     * @param subtractor    the instance of {@link Reducer} for subtraction
     * @param storeName     the name of the underlying {@link KTable} state store
     * @return a {@link KTable} with the same key and value types as this {@link KGroupedTable},
     *         containing aggregated values for each key
     */
    KTable<K, V> reduce(Reducer<V> adder,
                        Reducer<V> subtractor,
                        String storeName);

    /**
     * Combine updating values of this stream by the selected key into a new instance of {@link KTable}.
     * The resulting {@link KTable} will be materialized in a state
     * store provided by the {@link StateStoreSupplier}.
     *
     * @param adder         the instance of {@link Reducer} for addition
     * @param subtractor    the instance of {@link Reducer} for subtraction
     * @param storeSupplier user defined state store supplier {@link StateStoreSupplier}
     * @return a {@link KTable} with the same key and value types as this {@link KGroupedTable},
     *         containing aggregated values for each key
     */
    KTable<K, V> reduce(Reducer<V> adder,
                        Reducer<V> subtractor,
                        final StateStoreSupplier<KeyValueStore> storeSupplier);

    /**
     * Aggregate updating values of this stream by the selected key into a new instance of {@link KTable}.
     * The resulting {@link KTable} will be materialized in a local state
     * store with the given store name. Also a changelog topic named "${applicationId}-${storeName}-changelog"
     * will be automatically created in Kafka for failure recovery, where "applicationID"
     * is specified by the user in {@link org.apache.kafka.streams.StreamsConfig}.
     *
     * @param initializer   the instance of {@link Initializer}
     * @param adder         the instance of {@link Aggregator} for addition
     * @param subtractor    the instance of {@link Aggregator} for subtraction
     * @param aggValueSerde value serdes for materializing the aggregated table,
     *                      if not specified the default serdes defined in the configs will be used
     * @param storeName     the name of the underlying {@link KTable} state store
     * @param <T>           the value type of the aggregated {@link KTable}
     * @return a {@link KTable} with same key and aggregated value type {@code T},
     *         containing aggregated values for each key
     */
    <T> KTable<K, T> aggregate(Initializer<T> initializer,
                               Aggregator<K, V, T> adder,
                               Aggregator<K, V, T> subtractor,
                               Serde<T> aggValueSerde,
                               String storeName);

    /**
     * Aggregate updating values of this stream by the selected key into a new instance of {@link KTable}
     * using default serializers and deserializers.
     * The resulting {@link KTable} will be materialized in a local state
     * store with the given store name. Also a changelog topic named "${applicationId}-${storeName}-changelog"
     * will be automatically created in Kafka for failure recovery, where "applicationID"
     * is specified by the user in {@link org.apache.kafka.streams.StreamsConfig}.
     *
     * @param initializer   the instance of {@link Initializer}
     * @param adder         the instance of {@link Aggregator} for addition
     * @param subtractor   the instance of {@link Aggregator} for subtraction
     * @param storeName     the name of the underlying {@link KTable} state store
     * @param <T>           the value type of the aggregated {@link KTable}
     * @return a {@link KTable} with same key and aggregated value type {@code T},
     *         containing aggregated values for each key
     */
    <T> KTable<K, T> aggregate(Initializer<T> initializer,
                               Aggregator<K, V, T> adder,
                               Aggregator<K, V, T> subtractor,
                               String storeName);

    /**
     * Aggregate updating values of this stream by the selected key into a new instance of {@link KTable}
     * using default serializers and deserializers.
     * The resulting {@link KTable} will be materialized in a state
     * store provided by the {@link StateStoreSupplier}.
     *
     * @param initializer   the instance of {@link Initializer}
     * @param adder         the instance of {@link Aggregator} for addition
     * @param subtractor    the instance of {@link Aggregator} for subtraction
     * @param storeSupplier user defined state store supplier {@link StateStoreSupplier}
     * @param <T>           the value type of the aggregated {@link KTable}
     * @return a {@link KTable} with same key and aggregated value type {@code T},
     *         containing aggregated values for each key
     */
    <T> KTable<K, T> aggregate(Initializer<T> initializer,
                               Aggregator<K, V, T> adder,
                               Aggregator<K, V, T> subtractor,
                               final StateStoreSupplier<KeyValueStore> storeSupplier);

    /**
     * Count number of records of this stream by the selected key into a new instance of {@link KTable}.
     * The resulting {@link KTable} will be materialized in a local state
     * store with the given store name. Also a changelog topic named "${applicationId}-${storeName}-changelog"
     * will be automatically created in Kafka for failure recovery, where "applicationID"
     * is specified by the user in {@link org.apache.kafka.streams.StreamsConfig}.
     *
     * @param storeName     the name of the underlying {@link KTable} state store
     * @return a {@link KTable} with same key and {@link Long} value type as this {@link KGroupedTable},
     *         containing the number of values for each key
     */
    KTable<K, Long> count(String storeName);

    /**
     * Count number of records of this stream by the selected key into a new instance of {@link KTable}.
     * The resulting {@link KTable} will be materialized in a state
     * store provided by the {@link StateStoreSupplier}.
     *
     * @param storeSupplier user defined state store supplier {@link StateStoreSupplier}
     * @return a {@link KTable} with same key and {@link Long} value type as this {@link KGroupedTable},
     * containing the number of values for each key
     */
    KTable<K, Long> count(final StateStoreSupplier<KeyValueStore> storeSupplier);

}
