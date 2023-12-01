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
   * @throws ClassCastException
   */
  Audience sender(Object obj) throws ClassCastException;
}
