/**
 * 
 */
package it.fub.jardin.client.widget;

import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * @author gpantanetti
 * 
 */
public class ExtendedMultiFieldPattern<F extends Field<D>, D extends CharSequence>
    extends ExtendedMultiField<F, D> {

  private int valueLength;
  private boolean isDBK = false;

  public ExtendedMultiFieldPattern() {
    super();
    this.valueLength = 2;
  }

  public ExtendedMultiFieldPattern(int value) {
    super();
    this.valueLength = value;
  }

  public ExtendedMultiFieldPattern(int value, boolean isdbk) {
    super();
    this.valueLength = value;
    this.isDBK = isdbk;
  }

  @Override
  public void setValue(D pattern) {
    // TODO sostituire gli spazi con 0 in pattern

    if (pattern == null) {
      for (F tf : this.getAll()) {
        tf.setValue(null);
      }
      return;
    }

    // Fai il parsing del valore del campo
    if (isDBK) {
      String DbKPattern = new String("");
      int erpLimit = ((String) pattern).indexOf("$");

      Integer erp = Integer.valueOf((String) pattern.subSequence(0, erpLimit));
      String newPattern =
          (String) pattern.subSequence(erpLimit + 1, pattern.length());

      pattern = (D) newPattern;

      if (this.getAll().size() != pattern.length() / (3 * valueLength)) {
        this.get(0).setValue(pattern);
        return;
      }

      int j = 0;
      while (j < pattern.length()) {
        DbKPattern = DbKPattern + (String) pattern.subSequence(j, (j + 2));
        for (int x = 0; x < 6; x++) {
          j++;
        }
      }

      for (int i = 0; i < this.getAll().size(); i++) {
        F tf = this.get(i);
        String val =
            (String) DbKPattern.subSequence(i * valueLength, (i + 1)
                * valueLength);
        tf.setRawValue(((Object) (erp - (Integer.valueOf((val.trim())) + 30))).toString());
      }
    } else {
      if (this.getAll().size() != pattern.length() / valueLength) {
        this.get(0).setValue(pattern);
        return;
      }
      for (int i = 0; i < this.getAll().size(); i++) {
        F tf = this.get(i);
        tf.setValue((D) pattern.subSequence(i * valueLength, (i + 1)
            * valueLength));
      }
    }
  }

}
