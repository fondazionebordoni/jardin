/**
 * 
 */

package it.fub.jardin.client.model;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 * 
 */
public class User implements IsSerializable {

  private static final long serialVersionUID = 1L;

  private int uid, gid, login, status;
  private Credentials credentials;
  private String name, surname, group, email, office, telephone, last;
  private List<ResultsetImproved> resultsets;
  private List<Message> messages;
  private List<EventTypeSerializable> events;

  @SuppressWarnings("unused")
  private User() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  /**
   * @param uid
   *          User ID
   * @param gid
   *          Group ID
   * @param credentials
   *          Access credential (username, password)
   * @param name
   *          First name
   * @param surname
   *          Last name
   * @param group
   *          Group name
   * @param email
   *          Email
   * @param office
   *          Office identification
   * @param telephone
   *          Phone number
   * @param status
   *          User status
   * @param login
   *          Login counter
   * @param last
   *          Last login time
   * @param resultsets
   *          Resultset list
   * @param messages
   *          Message messages' list
   */
  public User(int uid, int gid, Credentials credentials, String name,
      String surname, String group, String email, String office,
      String telephone, int status, int login, String last,
      List<ResultsetImproved> resultsets, List<Message> messages) {
    super();
    this.uid = uid;
    this.gid = gid;
    this.credentials = credentials;
    this.name = name;
    this.surname = surname;
    this.group = group;
    this.email = email;
    this.office = office;
    this.telephone = telephone;
    this.status = status;
    this.login = login;
    this.last = last;
    this.resultsets = resultsets;
    this.messages = messages;
  }
  
  public void addEvent(EventTypeSerializable event) {
    this.events.add(event);
  }

  public void cleanEvents() {
    this.events = new ArrayList<EventTypeSerializable>();
  }
  
  public Credentials getCredentials() {
    return credentials;
  }

  public String getEmail() {
    return email;
  }

  public List<EventTypeSerializable> getEvents() {
    return events;
  }
  
  public String getFullName() {
    return getName() + " " + getSurname();
  }

  public int getGid() {
    return gid;
  }

  public String getGroup() {
    return group;
  }

  public String getLast() {
    return last;
  }

  public int getLogin() {
    return login;
  }

  public String getName() {
    return name;
  }

  public String getOffice() {
    return office;
  }

  public String getPassword() {
    return getCredentials().getPassword();
  }

  public ResultsetImproved getResultsetFromId(int resultsetId) {
    ResultsetImproved res = null;
    for (ResultsetImproved r : this.getResultsets()) {
      if (r.getId() == resultsetId) {
        res = r;
      }
    }
    return res;
  }

  public List<ResultsetImproved> getResultsets() {
    return resultsets;
  }

  public int getStatus() {
    return status;
  }

  public String getSurname() {
    return surname;
  }

  public String getTelephone() {
    return telephone;
  }

  public int getUid() {
    return uid;
  }

  public String getUsername() {
    return getCredentials().getUsername();
  }

  public List<Message> getWarnings() {
    return messages;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOffice(String office) {
    this.office = office;
  }

  public void setPassword(String password) {
    getCredentials().setPassword(password);
  }

  /**
   * Aggiorna le preferenze dell'utente
   */
  public void setResultsetHeaderPreferencesNoDefault(final Integer resultsetId,
      ArrayList<Integer> headerFields, String value) {

    final MessageBox waitbox =
        MessageBox.wait("Attendere",
            "Salvataggio preferenze vista in corso...", "");

    /* Create the service proxy class */
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
      public void onFailure(Throwable caught) {
        waitbox.close();
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(Boolean result) {
        waitbox.close();
        if (result) {
          // TODO Aggiornare solamente le preferenze del resultset in questione
          Info.display("Informazione", "Vista salvata per l'utente");
          Dispatcher.forwardEvent(EventList.GetGridViews, resultsetId);
        } else {
          Dispatcher.forwardEvent(EventList.Error,
              "Impossibile salvare la vista");
        }
      }
    };

    /* Make the call */
    service.setUserResultsetHeaderPreferencesNoDefault(getUid(), resultsetId,
        headerFields, value, callback);
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public void updateUserProperties() {
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
      public void onFailure(Throwable caught) {
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(Integer result) {
        Info.display("Informazione", "Salvate preferenze per l'utente "
            + getUsername());
      }
    };

    service.updateUserProperties(this, callback);
  }

}
