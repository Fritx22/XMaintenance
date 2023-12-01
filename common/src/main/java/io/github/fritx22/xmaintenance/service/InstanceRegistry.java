package io.github.fritx22.xmaintenance.service;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * This registry goal is to map any subclass of {@link E} to one of its instances
 * @param <E> The abstract type
 */
public interface InstanceRegistry<E> {

  /**
   *
   * @param clazz
   * @return The current instance or null
   * @param <S>
   */
  @Nullable
  <S extends E> S getInstance(Class<S> clazz);

  /**
   * Map the subclass to one of its instances.
   * The current value is replaced.
   * @param clazz
   * @param instance
   * @param <S>
   */
  <S extends E> void registerInstance(Class<S> clazz, S instance);

  /**
   * Accept a Consumer for each map entry.
   * An unchecked cast is needed to get
   * the class instance, but it's guaranteed to be safe
   * because of the {@link #registerInstance(Class, Object) registerInstance} method.
   * @param action
   */
  void forEachEntry(BiConsumer<Class<? extends E>, E> action);

  /**
   * Accept a {@link java.util.function.Consumer} for each registry value.
   * @param action
   */
  void forEachValue(Consumer<E> action);

}
