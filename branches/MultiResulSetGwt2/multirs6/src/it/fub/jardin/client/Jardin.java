package it.fub.jardin.client;

import it.fub.jardin.client.mvc.JardinController;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Jardin implements EntryPoint {

  public static final String SERVICE = "service";

  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  public static final String SERVER_ERROR =
      "An error occurred while attempting to contact the server. "
          + "Please check your network connection and try again.";

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    ManagerServiceAsync service = GWT.create(ManagerService.class);
    ServiceDefTarget endpoint = (ServiceDefTarget) service;
    String moduleRelativeURL = GWT.getModuleBaseURL() + SERVICE;
    endpoint.setServiceEntryPoint(moduleRelativeURL);
    Registry.register(SERVICE, service);

    /*
     * Creazione Dispatcher. Il dispacher si prende cura di instanziare i
     * controller
     */
    Dispatcher dispatcher = Dispatcher.get();
    dispatcher.addController(new JardinController());

    GXT.hideLoadingPanel("loading");

    /*
     * Fire dell'evento Login. il dipatcher passa l'evento ai suoi controller
     * che l'agganciano se si sono registrati per gestirlo.
     */
    dispatcher.dispatch(EventList.Login);
  }
}
