package org.elasticsearch.plugin.analysis.arirang;

import org.elasticsearch.common.settings.SecureString;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Setting.Property;
import org.elasticsearch.common.settings.Settings;

public final class ArirangSettings {

  static final Setting<String> MARS_NAME_SETTING = Setting.simpleString("mars.name", Property.NodeScope);

  public String getMarsName() {
    return marsName;
  }

  final String marsName;

  protected ArirangSettings(String marsName) {
    this.marsName = marsName;
  }

  static ArirangSettings getSettings(Settings settings) {
    return new ArirangSettings(
        MARS_NAME_SETTING.get(settings)
    );
  }

}
