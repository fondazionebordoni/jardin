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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jardin.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.fub.jardin.client.widget;

import com.extjs.gxt.ui.client.widget.form.Field;

public class ExtendedMultiFieldPattern<F extends Field<D>, D extends CharSequence>
    extends ExtendedMultiField<F, D> {

  private final int valueLength;
  private boolean isDBK = false;

  public ExtendedMultiFieldPattern() {
    super();
    this.valueLength = 2;
  }

  public ExtendedMultiFieldPattern(final int value) {
    super();
    this.valueLength = value;
  }

  public ExtendedMultiFieldPattern(final int value, final boolean isdbk) {
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
    if (this.isDBK) {
      String DbKPattern = new String("");
      int erpLimit = ((String) pattern).indexOf("$");

      Integer erp = Integer.valueOf((String) pattern.subSequence(0, erpLimit));
      String newPattern =
          (String) pattern.subSequence(erpLimit + 1, pattern.length());

      pattern = (D) newPattern;

      if (this.getAll().size() != pattern.length() / (3 * this.valueLength)) {
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
            (String) DbKPattern.subSequence(i * this.valueLength, (i + 1)
                * this.valueLength);
        tf.setRawValue(((Object) (erp - (Integer.valueOf((val.trim())) + 30))).toString());
      }
    } else {
      if (this.getAll().size() != pattern.length() / this.valueLength) {
        this.get(0).setValue(pattern);
        return;
      }
      for (int i = 0; i < this.getAll().size(); i++) {
        F tf = this.get(i);
        tf.setValue((D) pattern.subSequence(i * this.valueLength, (i + 1)
            * this.valueLength));
      }
    }
  }

}
