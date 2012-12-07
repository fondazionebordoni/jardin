/*
 * Copyright (c) 2010 Jardin Development Group <jardin.project@gmail.com>.
 * 
 * Jardin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Jardin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jardin. If not, see <http://www.gnu.org/licenses/>.
 */

package it.fub.jardin.client.model;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable {

  private static final long serialVersionUID = 1L;

  private int uid, gid, login, status;
  private Credentials credentials;
  private String name, surname, group, email, office, telephone, last;
  private List<ResultsetImproved> resultsets;
  private List<Message> messages;
  private List<EventType> events;

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
  public User(final int uid, final int gid, final Credentials credentials,
    final String name, final String surname, final String group,
    final String email, final String office, final String telephone,
    final int status, final int login, final String last,
    final List<ResultsetImproved> resultsets, final List<Message> messages) {
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
    this.events = new ArrayList<EventType>();
  }

  public void addEvent(final EventType event) {
    this.events.add(event);
  }

  public void cleanEvents() {
    this.events = new ArrayList<EventType>();
  }

  public Credentials getCredentials() {
    return this.credentials;
  }

  public String getEmail() {
    return this.email;
  }

  public List<EventType> getEvents() {
    return this.events;
  }

  public String getFullName() {
    return this.getName() + " " + this.getSurname();
  }

  public int getGid() {
    return this.gid;
  }

  public String getGroup() {
    return this.group;
  }

  public String getLast() {
    return this.last;
  }

  public int getLogin() {
    return this.login;
  }

  public List<Message> getMessages() {
    return this.messages;
  }

  public String getName() {
    return this.name;
  }

  public String getOffice() {
    return this.office;
  }

  public String getPassword() {
    return this.getCredentials().getPassword();
  }

  public ResultsetImproved getResultsetFromId(final int resultsetId) {
    ResultsetImproved res = null;
    for (ResultsetImproved r : this.getResultsets()) {
      if (r.getId() == resultsetId) {
        res = r;
      }
    }
    return res;
  }

  public List<ResultsetImproved> getResultsets() {
    return this.resultsets;
  }

  public int getStatus() {
    return this.status;
  }

  public String getSurname() {
    return this.surname;
  }

  public String getTelephone() {
    return this.telephone;
  }

  public int getUid() {
    return this.uid;
  }

  public String getUsername() {
    return this.getCredentials().getUsername();
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public void setMessages(final List<Message> messages) {
    this.messages = messages;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setOffice(final String office) {
    this.office = office;
  }

  public void setPassword(final String password) {
    this.getCredentials().setPassword(password);
  }

  /**
   * Aggiorna le preferenze dell'utente
   */
  public void setResultsetHeaderPreferencesNoDefault(final Integer resultsetId,
    final ArrayList<Integer> headerFields, final String value) {

    final MessageBox waitbox =
      MessageBox.wait("Attendere", "Salvataggio preferenze vista in corso...",
        "");

    /* Create the service proxy class */
    final ManagerServiceAsync service =
      (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
      public void onFailure(final Throwable caught) {
        waitbox.close();
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(final Boolean result) {
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
    service.setUserResultsetHeaderPreferencesNoDefault(this.getUid(),
      resultsetId, headerFields, value, callback);
  }

  public void setSurname(final String surname) {
    this.surname = surname;
  }

  public void setTelephone(final String telephone) {
    this.telephone = telephone;
  }

  public void updateUserProperties() {
    final ManagerServiceAsync service =
      (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<Void> callback = new AsyncCallback<Void>() {
      public void onFailure(final Throwable caught) {
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      @Override
      public void onSuccess(Void result) {
        Info.display("Informazione", "Salvate preferenze per l'utente "
          + User.this.getUsername());
      }
    };
    service.updateUserProperties(this, callback);
  }
}
