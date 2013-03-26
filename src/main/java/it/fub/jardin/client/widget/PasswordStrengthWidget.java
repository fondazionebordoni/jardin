package it.fub.jardin.client.widget;

import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class PasswordStrengthWidget extends Composite {

  private static class PasswordStrength extends JavaScriptObject {

    protected PasswordStrength() {
    }

    public final native String getRating() /*-{
			return this.rating;
    }-*/;
  }

  private static final String RATING_URL =
      URL.encode("https://www.google.com/accounts/RatePassword?Passwd=");
  private static final String[] STRENGTH = { "molto debole", "debole", "accettabile",
      "forte" };

  public static void updateStrengthIndicator(TextField<String> fPassword,
      final Label indicator) {
    JsonpRequestBuilder request = new JsonpRequestBuilder();
    request.requestObject(RATING_URL + fPassword.getValue(),
        new AsyncCallback<PasswordStrength>() {

          @Override
          public void onSuccess(PasswordStrength result) {
//            indicator.setText(STRENGTH[Integer.parseInt(result.getRating()) - 1]
//                + result.getRating());
            indicator.setText("password " + STRENGTH[Integer.parseInt(result.getRating()) - 1]);
          }

          @Override
          public void onFailure(Throwable caught) {
            indicator.setText("error calculating strength");
          }
        });
  }

}
