package it.fub.jardin.client.model;

public enum Tool {
  MODIFY("MODIFY"),
  EXPORT("EXPORT"),
  IMPORT("IMPORT"),
  PREFERENCE("PREFERENCE"),
  ANALISYS("ANALISYS"),
  ALL("ALL");

  private final String specTool;

  Tool(String tool) {
    this.specTool = tool;
  }

  @Override
  public String toString() {
    return this.specTool;
  }

}