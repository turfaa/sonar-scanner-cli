/*
 * Sonar Runner - API
 * Copyright (C) 2011 SonarSource
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
package org.sonar.runner.api;

import org.sonar.runner.impl.Constants;
import org.sonar.runner.impl.Logs;

import javax.annotation.Nullable;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

/**
 * @since 2.2
 */
public abstract class Runner<T extends Runner> {

  private final Properties properties = new Properties();

  protected Runner() {
    initProperties();
  }

  private void initProperties() {
    // default values
    properties.put(Constants.HOST_URL, "http://localhost:9000");
    properties.put(Constants.TASK, "scan");
    properties.put(Constants.RUNNER_APP, "SonarRunner");
    properties.put(Constants.RUNNER_APP_VERSION, RunnerVersion.version());
  }

  public Properties properties() {
    Properties clone = new Properties();
    clone.putAll(properties);
    return clone;
  }

  /**
   * Declare Sonar properties, for example sonar.projectKey=>foo.
   */
  public T addProperties(Properties p) {
    properties.putAll(p);
    return (T) this;
  }

  public T setProperty(String key, String value) {
    properties.setProperty(key, value);
    return (T) this;
  }

  public String property(String key, @Nullable String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  /**
   * User-agent used in the HTTP requests to the Sonar server
   */
  public T setApp(String app, String version) {
    setProperty(Constants.RUNNER_APP, app);
    setProperty(Constants.RUNNER_APP_VERSION, version);
    return (T) this;
  }

  public String app() {
    return property(Constants.RUNNER_APP, null);
  }

  public String appVersion() {
    return property(Constants.RUNNER_APP_VERSION, null);
  }

  public void execute() {
    initSourceEncoding();
    doExecute();
  }

  private void initSourceEncoding() {
    String sourceEncoding = property(Constants.SOURCE_ENCODING, null);
    boolean platformDependent = false;
    if (sourceEncoding == null || sourceEncoding.equals("")) {
      sourceEncoding = Charset.defaultCharset().name();
      platformDependent = true;
      setProperty(Constants.SOURCE_ENCODING, sourceEncoding);
    }
    Logs.info("Default locale: \"" + Locale.getDefault() + "\", source code encoding: \"" + sourceEncoding + "\""
      + (platformDependent ? " (analysis is platform dependent)" : ""));
  }

  protected abstract void doExecute();
}
