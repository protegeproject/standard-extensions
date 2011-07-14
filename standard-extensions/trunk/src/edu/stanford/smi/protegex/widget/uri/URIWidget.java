package edu.stanford.smi.protegex.widget.uri;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.*;

/**
 *  Description of the Class
 *
 * @author    Qi Li <liq@smi.stanford.edu>
 */

public class URIWidget extends AbstractSlotWidget {
    private static final long serialVersionUID = 5935821209965384422L;

    private final static String SLOT_NAME = "uri-value";

    private JTextField itsTextField;
    private Instance itsInstance;
    private String _currentValue;
    private boolean _valueHasChanged;

    private AllowableAction itsCheckAction;
    private AllowableAction itsInspectAction;
    private AllowableAction itsAddAction;
    private AllowableAction itsRemoveAction;

    private boolean isUri;

    private FrameListener itsInstanceListener =
        new FrameAdapter() {
            public void browserTextChanged(FrameEvent event) {
                setText();
            }
        }
    ;

    private class TextFieldDocumentListener extends DocumentChangedListener {
        public void stateChanged(ChangeEvent e) {
            _valueHasChanged = true;
        }
    }

    private class TextFieldFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            adjustValueIfReasonable();
        }

        public void focusGained(FocusEvent e) {
            recoverFrontColor();
        }
    }

    public void addButton(LabeledComponent c, Action action) {
        addButtonConfiguration(action);
        if (displayButton(action)) {
            c.addHeaderButton(action);
        }
    }

    private void adjustValueIfReasonable() {
        checkSyntax();
        if (_valueHasChanged) {
            valueChanged();
        }
        _valueHasChanged = false;
    }

    private void checkSyntax() {

        String URIstring = itsTextField.getText();
        if ((null == URIstring) || (URIstring.equals(""))) {
            _currentValue = null;
            slotIsCorrect();
            return;
        }

        String syntaxResponse = URIsyntax(URIstring);
        if (null != syntaxResponse) {
            itsTextField.setForeground(Color.red);
            itsTextField.setToolTipText(syntaxResponse + ". The slot currently has the value " + _currentValue);
        } else {
            _currentValue = URIstring;
            slotIsCorrect();
        }
    }

    private String checkURLWeb(String urlname) {
        URL url;
        HttpURLConnection connection = null;

        try {
            url = new URL(urlname);
        } catch (MalformedURLException e) {
            return new String("Invalid URL name!");
        }

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode > 299) {
                return new String(" Can not load the web ");
            }
        } catch (MalformedURLException ignored) {
            return new String("Invalid URL name!");
        } catch (IOException ignored) {
            return new String("Can not load the web");
        }
        return null;
    }

    public JTextField createTextField() {
        return ComponentFactory.createTextField();
    }

    public void dispose() {
        super.dispose();
        adjustValueIfReasonable();
        if (itsInstance != null) {
            itsInstance.removeFrameListener(itsInstanceListener);
        }
    }

    private Action getCheckURIAction() {

        itsCheckAction = new CheckURIAction("Check URI Syntax") {
            private static final long serialVersionUID = -7276717254833855910L;

            public void onCheckURI() {
                // Since lost focus happens, do nothing here
            }
        };
        return itsCheckAction;
    }

    private Action getInspectURIAction() {
        itsInspectAction = new InspectURIAction("Inspect URI Site") {
            private static final long serialVersionUID = -6336498276587786728L;

            public void onInspectURI() {
                String URIstring = itsTextField.getText();
                String response = checkURLWeb(URIstring);
                if (null != response) {
                    itsTextField.setForeground(Color.red);
                    itsTextField.setToolTipText(response + ". The slot currently has the value " + _currentValue);
                } else {
                    _currentValue = URIstring;
                    webIsCorrect();
                }
            }
        };
        return itsInspectAction;
    }

    private Action getRemoveInstanceAction() {
        itsRemoveAction = new RemoveAction("Remove Instance", this) {
            private static final long serialVersionUID = -1080506743274821865L;

            public void onRemove(Object o) {
                removeDisplayedInstance();
            }
        };
        return itsRemoveAction;
    }

    private Action getSelectInstanceAction() {
        itsAddAction = new AddAction("Add Instance") {
            private static final long serialVersionUID = -4002345467039679996L;

            public void onAdd() {
                Collection clses = getCls().getTemplateSlotAllowedClses(getSlot());
                Instance instance = DisplayUtilities.pickInstance(URIWidget.this, clses);
                // decide whether instance is a resource or a uri
                if (instance != null) {
                    setDisplayedInstance(instance);
                }
            }
        };
        return itsAddAction;
    }

    public Collection getSelection() {
        return CollectionUtilities.createCollection(itsInstance);
    }

    public JTextField getTextField() {
        return itsTextField;
    }

    public Collection getValues() {
        // Check whether itsInstance is from rdf:URI. If the slot does not exist,
        // we need create it.
        if (isUri) {
            if (itsInstance != null) {
                Slot slot = itsInstance.getKnowledgeBase().getSlot(SLOT_NAME);
                itsInstance.setOwnSlotValue(slot, itsTextField.getText());
            } else {
                String itsvalue = itsTextField.getText();
                if (itsvalue != null) {
                    itsvalue = itsvalue.trim();
                }
                if (itsvalue != null && itsvalue.length() > 0) {
                    KnowledgeBase ks = getKnowledgeBase();
                    Cls rdfuri = ks.getCls("URI");
                    Instance value = ks.createInstance(null, rdfuri);
                    Slot slot = getKnowledgeBase().getSlot(SLOT_NAME);
                    value.setOwnSlotValue(slot, itsvalue);

                    replaceInstance(value);

                }
            }
        } else {
            // resource instance
            // do nothing, just return the instance
        }

        return CollectionUtilities.createList(itsInstance);
    }

    private Action getViewInstanceAction() {
        return new ViewAction("View Instance", this) {
            private static final long serialVersionUID = -4064551812371783604L;

            public void onView(Object o) {
                showInstance((Instance) o);
            }
        };
    }

    public void initialize() {
        itsTextField = createTextField();

        if (isRuntime()) {

            itsTextField.getDocument().addDocumentListener(new TextFieldDocumentListener());
            itsTextField.addFocusListener(new TextFieldFocusListener());

        }

        LabeledComponent c = new LabeledComponent(getLabel(), itsTextField);
        addButton(c, getCheckURIAction());
        addButton(c, getInspectURIAction());
        addButton(c, getViewInstanceAction());
        addButton(c, getSelectInstanceAction());
        addButton(c, getRemoveInstanceAction());
        add(c);
        setPreferredColumns(2);
        setPreferredRows(1);
        isUri = true;
    }

    private String isResource(Instance instance) {

        Cls resourceCls = getKnowledgeBase().getCls("rdfs:Resource");
        if (resourceCls != null) {
            Collection subinstances = resourceCls.getInstances();
            Iterator i = subinstances.iterator();

            while (i.hasNext()) {
                Instance c = (Instance) i.next();
                if (instance.getName().equals(c.getName())) {

                    isUri = false;
                    return (String) instance.getName();
                }
            }

        }

        return null;
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        boolean isSuitable;
        if (cls == null || slot == null) {
            isSuitable = false;
        } else {
            boolean isInstance = cls.getTemplateSlotValueType(slot) == ValueType.INSTANCE;
            boolean isMultiple = cls.getTemplateSlotAllowsMultipleValues(slot);
            boolean isRdfURI = isInstance && rdf_uriCheck(cls, slot, facet);
            boolean isRdfResource = rdf_resourceCheck(cls, slot, facet);
            isSuitable = isInstance && !isMultiple && (isRdfURI || isRdfResource);
        }
        return isSuitable;
    }

    private String isURI(Instance instance) {

        Cls uriCls = getKnowledgeBase().getCls("URI");
        if (uriCls != null) {
            Collection subinstances = uriCls.getInstances();
            Iterator i = subinstances.iterator();
            Slot slot = getKnowledgeBase().getSlot(SLOT_NAME);
            while (i.hasNext()) {
                Instance c = (Instance) i.next();
                if (instance.getName().equals(c.getName())) {
                    isUri = true;
                    return (String) instance.getOwnSlotValue(slot);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        edu.stanford.smi.protege.Application.main(args);
    }

    private static boolean rdf_resourceCheck(Cls cls, Slot slot, Facet facet) {
        Cls uriCls = cls.getKnowledgeBase().getCls("rdfs:Resource");
        if (uriCls != null && (cls.hasSuperclass(uriCls) || (cls == uriCls))) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean rdf_uriCheck(Cls cls, Slot slot, Facet facet) {
        Collection col = cls.getTemplateSlotAllowedClses(slot);
        Iterator i = col.iterator();

        Cls uriCls = cls.getKnowledgeBase().getCls("URI");
        if (uriCls != null) {
            while (i.hasNext()) {
                Cls c = (Cls) i.next();
                if (c.hasSuperclass(uriCls) || (c.getName()).equals("URI")) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void recoverFrontColor() {
        itsTextField.setForeground(Color.black);
    }

    private void removeDisplayedInstance() {

        replaceInstance(null);
        setText();
        valueChanged();
    }

    private void replaceInstance(Instance instance) {

        if (itsInstance != null) {
            itsInstance.removeFrameListener(itsInstanceListener);
        }
        itsInstance = instance;
        if (itsInstance != null) {
            itsInstance.addFrameListener(itsInstanceListener);
        }
        notifySelectionListeners();
    }

    private void setDisplayedInstance(Instance instance) {
        replaceInstance(instance);
        setText();
        valueChanged();
    }

    public void setEditable(boolean b) {

        itsCheckAction.setAllowed(b);
        itsInspectAction.setAllowed(b);
        itsAddAction.setAllowed(b);
        itsRemoveAction.setAllowed(b);
        if (isUri) {
            itsTextField.setEditable(b);
        } else {
            itsTextField.setEnabled(false);
        }

    }

    private void setText() {
        String text;

        if (itsInstance == null) {
            text = "";
            isUri = true;
        } else {
            text = isResource(itsInstance);
            if (text == null) {
                text = isURI(itsInstance);
            }
        }
        _currentValue = text;
        _valueHasChanged = false;
        itsTextField.setText(text);

        checkSyntax();
        itsTextField.setEnabled(isUri);
    }

    public void setValues(Collection values) {

        Instance value;
        value = (Instance) CollectionUtilities.getFirstItem(values);

        replaceInstance(value);
        setText();
    }

    private void slotIsCorrect() {
        itsTextField.setToolTipText("The textfield is displaying the correct protocol");
        itsTextField.setForeground(Color.black);
    }

    private String URIsyntax(String urlname) {

        try {
            new URL(urlname);
        } catch (MalformedURLException e) {
            return new String("Invalid URL name!");
        }
        if (urlname.toLowerCase().startsWith("http")) {
            if (!urlname.toLowerCase().startsWith("http://")) {
                return new String("Invalid URL name!");
            }
        }
        return null;
    }

    private void webIsCorrect() {
        itsTextField.setToolTipText("The textfield is displaying the valid site");
        itsTextField.setForeground(Color.black);
    }
}
