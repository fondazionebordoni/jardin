package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.JungleRecords;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class Jungle extends Dialog {

  private ListGrid grid;

  public Jungle(ResultsetImproved resultset, String xml) {
    this.setMaximizable(true);
    this.setHeading("Jungle (Jardin warehousing): " + resultset.getAlias());
    this.setWidth(500);
    this.setHeight(500);
    this.setLayout(new FitLayout());

    ToolBar toolBar = new ToolBar();
    toolBar.add(formulaButton());
    toolBar.add(summaryButton());
    this.setTopComponent(toolBar);

    this.grid = getGrid(resultset);
    this.grid.setDataSource(new JungleRecords(resultset, xml));
    this.grid.setAutoFetchData(true);
    // this.grid.resizeTo(this.getWidth(), this.getHeight());

    this.add(this.grid);

    Listener<WindowEvent> listener = new Listener<WindowEvent>() {
      public void handleEvent(WindowEvent be) {
        grid.resizeTo(be.getWidth() - 14, be.getHeight());
      }
    };

    this.addListener(Events.Resize, listener);
    this.getButtonBar().removeAll();
  }

  private Button formulaButton() {
    SelectionListener l = new SelectionListener() {
      @Override
      public void componentSelected(ComponentEvent ce) {
        grid.addFormulaField();
      }
    };

    return new Button("Formula", IconHelper.createStyle("icon-formula"), l);
  }

  private Button summaryButton() {
    SelectionListener l = new SelectionListener() {
      @Override
      public void componentSelected(ComponentEvent ce) {
        grid.addSummaryField();
      }
    };

    return new Button("Summary", IconHelper.createStyle("icon-summary"), l);
  }

  private ListGrid getGrid(ResultsetImproved resultset) {
    ListGrid grid = new ListGrid();
    ListGridField[] list = new ListGridField[resultset.getFields().size()];

    int i = 0;
    for (ResultsetField field : resultset.getFields()) {
      list[i++] = new ListGridField(field.getName(), field.getAlias());
    }

    grid.setFields(list);
    return grid;
  }
}
