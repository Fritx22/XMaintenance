/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
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
