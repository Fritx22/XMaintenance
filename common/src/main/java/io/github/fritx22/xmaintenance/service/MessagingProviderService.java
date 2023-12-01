/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.service;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;

public interface MessagingProviderService extends Service, AudienceProvider {

  /**
   * Tries to get an {@link net.kyori.adventure.audience.Audience} instance if the object
   * is a sender in the current platform, otherwise it throws
   * a {@link java.lang.ClassCastException}.
   * @param obj command sender object of the current platform
   * @return the corresponding Audience instance
   * @throws ClassCastException if the object or platform is invalid
   */
  Audience sender(Object obj) throws ClassCastException;
}
