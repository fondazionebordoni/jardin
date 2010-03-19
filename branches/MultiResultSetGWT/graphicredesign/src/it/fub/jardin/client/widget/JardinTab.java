package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.ResultsetImproved;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class JardinTab extends Composite {

  private ResultsetImproved resultset;
  
  interface Binder extends UiBinder<Widget, JardinTab> {};
  private static final Binder binder = GWT.create(Binder.class);
  
  public JardinTab(ResultsetImproved resultset) {
    this.resultset = resultset;
    initWidget(binder.createAndBindUi(this));
  }
  
  @UiFactory
  SearchAreaAdvanced makeSearchAreaAdvanced() {
    return new SearchAreaAdvanced(this.resultset);
  }

  @UiFactory
  JardinGrid makeJardinGrid() {
    return new JardinGrid(null, null, null);
  }

  @UiFactory
  JardinDetail makeJardinDetail() {
    return new JardinDetail(this.resultset);
  }


  
}
