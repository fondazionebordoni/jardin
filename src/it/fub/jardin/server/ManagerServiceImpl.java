/**
 * 
 */
package it.fub.jardin.server;

import it.fub.jardin.client.DbException;
import it.fub.jardin.client.ManagerService;
import it.fub.jardin.client.UserException;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.model.Warning;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mysql.jdbc.Statement;

/**
 * @author gpantanetti
 * 
 */
public class ManagerServiceImpl extends RemoteServiceServlet implements
    ManagerService {

  private static final long serialVersionUID = 1L;
  private User user;
  private DbUtils dbUtils;

  public ManagerServiceImpl() {
    super();
    this.dbUtils = new DbUtils();
  }

  public String createReport(String file, Template template,
      PagingLoadConfig config, List<String> columns, SearchParams searchParams)
      throws UserException {
    if (searchParams == null) {
      throw new UserException("Effettuare prima una ricerca");
    }

    List<BaseModelData> records = dbUtils.getObjects(config, searchParams);

    String xsl =
        this.getServletContext().getRealPath(
            Template.TEMPLATE_DIR + template.getXsl());

    /* Gestione del template di default */
    if (template.getInfo().compareTo(Template.DEFAULT.getInfo()) == 0) {
      ResultsetImproved resultset =
          user.getResultsetFromId(searchParams.getResultsetId());
      try {
        FileUtils.prepareDefaultTemplate(resultset, xsl);
      } catch (IOException e) {
        Log.error("Impossibile ottenere il template di default", e);
        throw new UserException("Impossibile ottenere il template di default");
      }
    }

    if (template != null) {
      String context = this.getServletContext().getRealPath("/");
      String realpath = this.getServletContext().getRealPath(file);
      String result =
          FileUtils.createReport(realpath, xsl, template, records, columns);
      Log.debug("File esportato: " + result);
      Log.debug("Servlet context path: " + context);
      return result.substring(context.length());
    } else {
      throw new UserException("Impossibile leggere il template");
    }
  }

  public List<Warning> getGroupWarnings(Integer groupId) throws DbException {
    return dbUtils.getGroupWarnigns(groupId);
  }

  public PagingLoadResult<BaseModelData> getRecords(PagingLoadConfig config,
      SearchParams searchParams) throws DbException {
    List<BaseModelData> records = dbUtils.getObjects(config, searchParams);
    int recordSize = dbUtils.countObjects(searchParams);

    if (config != null) {
      return new BasePagingLoadResult<BaseModelData>(records,
          config.getOffset(), recordSize);
    } else {
      return new BasePagingLoadResult<BaseModelData>(records);
    }
  }

  public List<BaseModelData> getReGroupings(int resultSetId) {
    return dbUtils.getReGroupings(resultSetId);
  }

  public String getServerTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
    return formatter.format(new Date());
  }

  public User getUser(Credentials credentials) throws UserException {
    this.user = dbUtils.getUser(credentials);
    Log.info("[" + this.user.getUsername() + "] LOGIN");
    return user;
  }

  public List<Warning> getUserWarnings(Integer userId) throws DbException {
    return dbUtils.getUserWarnigns(userId);
  }

  public List<BaseModelData> getValuesOfAField(int resultsetId, String fieldId)
      throws DbException {
    return dbUtils.getValuesOfAField(resultsetId, fieldId);
  }

  public FieldsMatrix getValuesOfFields(Integer resultsetId) throws DbException {
    return dbUtils.getValuesOfFields(resultsetId);
  }

  public FieldsMatrix getValuesOfForeignKeys(Integer resultsetId)
      throws DbException {
    return dbUtils.getValuesOfForeignKeys(resultsetId);
  }

  public Integer removeObjects(Integer resultset,
      List<BaseModelData> selectedRows) throws DbException {
    Log.info("[" + this.user.getUsername() + "] Removing records...");
    return dbUtils.removeObjects(resultset, selectedRows);
  }

  public Integer setObjects(Integer resultsetId, List<BaseModelData> newItemList)
      throws DbException {
    Log.info("[" + this.user.getUsername() + "] Setting records...");
    // recupero dei vecchi parametri
    // e passaggio a notifyCanges
    List<BaseModelData> newItemListTest = newItemList;
    int success = dbUtils.setObjects(resultsetId, newItemList);
    if (success > 0) {
      try {
        dbUtils.notifyChanges(resultsetId, newItemListTest);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return success;
  }

  public void updateUserProperties(User user) throws DbException {
    dbUtils.updateUserProperties(user);
  }

  public boolean setUserResultsetHeaderPreferencesNoDefault(Integer userid,
      Integer resultsetId, ArrayList<Integer> listfields, String value)
      throws DbException {
    return dbUtils.setUserResultsetHeaderPreferencesNoDefault(userid,
        resultsetId, listfields, value);
  }

  public HeaderPreferenceList getGridViews(Integer userId, Integer resultsetId)
      throws DbException {
    return dbUtils.getHeaderUserPreferenceList(userId, resultsetId);
  }

  public List<Integer> getHeaderUserPreference(Integer idUser,
      Integer userPreferenceHeaderId) throws DbException {
    return dbUtils.getHeaderUserPreference(idUser, userPreferenceHeaderId);
  }

  public List<BaseModelData> getValuesOfAFieldFromTableName(String table,
      String field) throws DbException {

    return dbUtils.getValuesOfAFieldFromTableName(table, field);
  }

}
