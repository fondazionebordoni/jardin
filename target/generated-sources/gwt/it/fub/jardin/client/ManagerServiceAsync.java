package it.fub.jardin.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface ManagerServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void massiveUpdate( it.fub.jardin.client.model.MassiveUpdateObject muo, AsyncCallback<java.lang.Integer> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void changePassword( it.fub.jardin.client.model.Credentials credentials, AsyncCallback<it.fub.jardin.client.model.User> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getResultsetImproved( int resultsetId, int gid, AsyncCallback<it.fub.jardin.client.model.ResultsetImproved> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getResultsetPlus( int resultsetId, int gid, AsyncCallback<it.fub.jardin.client.model.ResultsetPlus> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void createReport( java.lang.String file, it.fub.jardin.client.model.Template template, com.extjs.gxt.ui.client.data.PagingLoadConfig config, java.util.List<com.extjs.gxt.ui.client.data.BaseModelData> selectedRows, java.util.List<java.lang.String> columns, it.fub.jardin.client.model.SearchParams searchParams, char fs, char ts, AsyncCallback<java.lang.String> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getEvents( AsyncCallback<java.util.List<com.extjs.gxt.ui.client.event.EventType>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getGridViews( java.lang.Integer userId, java.lang.Integer resultsetId, AsyncCallback<it.fub.jardin.client.model.HeaderPreferenceList> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getHeaderUserPreference( java.lang.Integer id, java.lang.Integer userPreferenceHeaderId, AsyncCallback<java.util.List<java.lang.Integer>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getRecords( com.extjs.gxt.ui.client.data.PagingLoadConfig config, it.fub.jardin.client.model.SearchParams searchParams, AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<com.extjs.gxt.ui.client.data.BaseModelData>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getReGroupings( int resultSetId, AsyncCallback<java.util.List<com.extjs.gxt.ui.client.data.BaseModelData>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getServerTime( AsyncCallback<java.lang.String> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getSimpleUser( it.fub.jardin.client.model.Credentials credentials, AsyncCallback<it.fub.jardin.client.model.User> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getUser( it.fub.jardin.client.model.Credentials credentials, AsyncCallback<it.fub.jardin.client.model.User> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getUserMessages( java.lang.Integer userId, AsyncCallback<java.util.List<it.fub.jardin.client.model.Message>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getValuesOfAField( int resultsetId, java.lang.String fieldId, AsyncCallback<java.util.List<com.extjs.gxt.ui.client.data.BaseModelData>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getValuesOfAFieldFromTableName( java.lang.String table, java.lang.String field, AsyncCallback<java.util.List<com.extjs.gxt.ui.client.data.BaseModelData>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getValuesOfFields( java.lang.Integer resultsetId, AsyncCallback<it.fub.jardin.client.model.FieldsMatrix> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getValuesOfForeignKeys( java.lang.Integer resultsetId, AsyncCallback<it.fub.jardin.client.model.FieldsMatrix> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void removeObjects( java.lang.Integer resultset, java.util.List<com.extjs.gxt.ui.client.data.BaseModelData> selectedRows, AsyncCallback<java.lang.Integer> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void sendMessage( it.fub.jardin.client.model.Message message, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void setObjects( java.lang.Integer resultsetId, java.util.List<com.extjs.gxt.ui.client.data.BaseModelData> newItemList, AsyncCallback<java.lang.Integer> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void updateObjects( java.lang.Integer resultsetId, java.util.List<com.extjs.gxt.ui.client.data.BaseModelData> newItemList, java.lang.String condition, AsyncCallback<java.lang.Integer> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void setUserResultsetHeaderPreferencesNoDefault( java.lang.Integer userid, java.lang.Integer resultsetId, java.util.ArrayList<java.lang.Integer> listfields, java.lang.String value, AsyncCallback<java.lang.Boolean> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void updateUserProperties( it.fub.jardin.client.model.User user, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getPopUpDetailEntry( com.extjs.gxt.ui.client.data.BaseModelData data, AsyncCallback<java.util.ArrayList<com.extjs.gxt.ui.client.data.BaseModelData>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getPlugins( int gid, int rsid, AsyncCallback<java.util.List<it.fub.jardin.client.model.Plugin>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void getUserResultsetList( int uid, AsyncCallback<java.util.List<it.fub.jardin.client.model.Resultset>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see it.fub.jardin.client.ManagerService
     */
    void testServerPresence( AsyncCallback<java.lang.String> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static ManagerServiceAsync instance;

        public static final ManagerServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (ManagerServiceAsync) GWT.create( ManagerService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "ManagerService" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
