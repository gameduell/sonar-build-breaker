/*
 * Sonar Build Breaker Plugin
 * Copyright (C) 2009 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.buildbreaker;

import org.sonar.api.batch.BuildBreaker;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class ForbiddenConfigurationBreaker extends BuildBreaker {

  private static final Logger LOG = Loggers.get(ForbiddenConfigurationBreaker.class);

  private final Settings settings;

  public ForbiddenConfigurationBreaker(Settings settings) {
    this.settings = settings;
  }

  public void executeOn(Project project, SensorContext context) {
    String[] pairs = settings.getStringArray(BuildBreakerPlugin.FORBIDDEN_CONF_KEY);
    for (String pair : pairs) {
      String key = substringBefore(pair, "=");
      String value = substringAfter(pair, "=");
      if (equals(value, settings.getString(key))) {
        LOG.error(BuildBreakerPlugin.BUILD_BREAKER_LOG_STAMP + "Forbidden configuration: " + pair);
        fail("A forbidden configuration has been found on the project: " + pair);
      }
    }
  }

  boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }
  boolean equals(String str1, String str2) {
    return str1 == null?str2 == null:str1.equals(str2);
  }
  String substringBefore(String str, String separator) {
    if(!isEmpty(str) && separator != null) {
      if(separator.length() == 0) {
        return "";
      } else {
        int pos = str.indexOf(separator);
        return pos == -1?str:str.substring(0, pos);
      }
    } else {
      return str;
    }
  }
  String substringAfter(String str, String separator) {
    if(isEmpty(str)) {
      return str;
    } else if(separator == null) {
      return "";
    } else {
      int pos = str.indexOf(separator);
      return pos == -1?"":str.substring(pos + separator.length());
    }
  }
}
