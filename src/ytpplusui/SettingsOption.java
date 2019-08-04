/* Copyright 2019 randompooper
 * This file is part of YTPPlusUI.
 *
 * YTPPlusUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * YTPPlusUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with YTPPlusUI.  If not, see <https://www.gnu.org/licenses/>.
 */
package ytpplusui;

public class SettingsOption {
    public SettingsOption() {}

    public SettingsOption(String id, String label, OptionType type, Value bind) {
        this(id, label, type, bind, true);
    }

    public SettingsOption(String id, String label, OptionType type, Value bind, boolean save) {
        this(id, label, type, bind, save, null);
    }

    public SettingsOption(String id, String label, OptionType type, Value bind, boolean save, Object[] extra) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.bind = bind;
        this.save = save;
        this.extra = extra;
    }

    public Object[] getExtra() { return extra; }
    public void setExtra(Object[] extra) { this.extra = extra; }

    public Value getBind() { return bind; }
    public void setBind(Value bind) { this.bind = bind; }

    public OptionType getType() { return type; }
    public void setType(OptionType type) { this.type = type; }

    public boolean saveable() { return save; }
    public void setSaveable(boolean save) { this.save = save; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public interface Value {
        public Object get();
        /* return null if setter doesn't indicate successfull operation
         * otherwise return true for success or false for fail
         */
        public Boolean set(Object value);
    }

    public enum OptionType {
        /* TextField + Button for file chooser */
        PathFile,
        /* TextField + Button for directory chooser */
        PathDir,
        CheckBox,
        /* String items for ComboBox are passed through extra */
        ComboBox,
        TextField,
        /* TextField with String conversion to number */
        DoubleNumberField,
        /* Same as above, but number is integer */
        IntegerNumberField
    }
    /* Extra parameters (only used for ComboBox) */
    private Object[] extra;
    private Value bind;
    private OptionType type;
    /* Save/load value to/from Preferences */
    private boolean save;
    /* id: element id and (if enabled) key for Preferences
     * label: visible description to option
     */
    private String id, label;
}
