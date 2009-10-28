package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum MessageType implements IsSerializable {

  ALL("ALL"), GROUP("GROUP"), USER("USER");

  private final String type;

  MessageType(String type) {
    this.type = type;
  }

  public String type() {
    return type;
  }

  @Override
  public String toString() {
    return type;
  }

}
