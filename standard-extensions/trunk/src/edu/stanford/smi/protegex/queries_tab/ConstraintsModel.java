package edu.stanford.smi.protegex.queries_tab;

import javax.swing.*;

public class ConstraintsModel extends DefaultComboBoxModel{
   private static final long serialVersionUID = 5312915838647751993L;
private static final String[] types = {"null","BOOLEAN","CLS", "FLOAT", "INSTANCE", "INTEGER", "STRING", "SYMBOL"};
   private static final String[] booleanConstraints = {"is" };
   private static final String[] clsConstraints = {"contains", "does not contain"};
   private static final String[] floatConstraints = {"is", "is greater than", "is less than"};
   private static final String[] instanceConstraints = {"contains", "does not contain"};
   private static final String[] integerConstraints = {"is", "is greater than", "is less than"};
   private static final String[] stringConstraints = {"contains", "does not contain", "is", "is not", "begins with", "ends with"};
   private static final String[] symbolConstraints = {"is", "is not"};
   private static final String[] nullConstraints = {""};

   private String type;

    public ConstraintsModel() {
        super();
        initialize();
    }

    private void addArray(String[] constraints) {
        for (int i = 0; i < constraints.length; i++) {
            addElement(constraints[i]);
        }
    }

    private void addConstraints(int index) {
        switch (index) {
            case 0 :
                //addArray(anyConstraints);
                addArray(nullConstraints);
                break;
            case 1 :
                addArray(booleanConstraints);
                break;
            case 2 :
                addArray(clsConstraints);
                break;
            case 3 :
                addArray(floatConstraints);
                break;
            case 4 :
                addArray(instanceConstraints);
                break;
            case 5 :
                addArray(integerConstraints);
                break;
            case 6 :
                addArray(stringConstraints);
                break;
            case 7 :
                addArray(symbolConstraints);
                break;
            default :
                //addArray(anyConstraints);
                addArray(nullConstraints);
                break;
        }
    }

    public static String[] getBooleanConstraints() {
        return booleanConstraints;
    }

    public static String[] getClsConstraints() {
        return clsConstraints;
    }

    public static String[] getFloatConstraints() {
        return floatConstraints;
    }

    public static String[] getInstanceConstraints() {
        return instanceConstraints;
    }

    public static String[] getIntegerConstraints() {
        return integerConstraints;
    }

    public static String[] getStringConstraints() {
        return stringConstraints;
    }

    public static String[] getSymbolConstraints() {
        return symbolConstraints;
    }

    public String getType() {
        return type;
    }

    public void initialize() {
        // setup the default type
        type = "INSTANCE";
        setUpComboBox(type);
    }

    public void setUpComboBox(String type) {
        this.type = type;
        if (getSize() > 0)
            removeAllElements();

        for (int i = 0; i < types.length; i++) {
            if (types[i].toLowerCase().equals(type.toLowerCase())) {
                addConstraints(i);
                break;
            }
        }
    }
}
