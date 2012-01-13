package edu.stanford.smi.protegex.widget.contains;

import java.lang.reflect.*;
import java.util.*;

import edu.stanford.smi.protege.model.*;

/**
 *  Description of the Class
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public class WidgetWrapperPool {
    private Project _project;
    private KnowledgeBase _kb;
    private Cls _cls;
    private int _maxSize;

    private WidgetWrapper[] _availableWidgetWrappers;
    private int _nextAvailableWidgetWrapper;
    private Class _formDescriptionClass;
    private Constructor _formDescriptionConstructor;

    private static HashMap _widgetWrapperPools = new HashMap();


    public WidgetWrapperPool(
        Project project,
        KnowledgeBase kb,
        Cls cls,
        int maxSize,
        int numberToAllocateInAdvance,
        Class formDescriptionClass) {
        _nextAvailableWidgetWrapper = -1;
        _project = project;
        _kb = kb;
        _cls = cls;
        _maxSize = maxSize;
        _availableWidgetWrappers = new WidgetWrapper[_maxSize];
        setFormDescriptionClass(formDescriptionClass);
        if (numberToAllocateInAdvance > 0) {
            allocateInAdvance((maxSize < numberToAllocateInAdvance) ? maxSize : numberToAllocateInAdvance);
        }
    }

    private void allocateInAdvance(int numberToAllocate) {
        Instance _tokenInstance = getTokenInstance();
        if (null == _tokenInstance) {
            return;
        }
        for (int counter = 0; counter < numberToAllocate; counter++) {
            returnWidgetWrapper(createWidgetWrapperForInstance(_tokenInstance));
        }
        return;
    }

    private WidgetWrapper createWidgetWrapperForInstance(Instance instance) {
        FormDescription formDescription = null;
        if (null != _formDescriptionConstructor) {
            Object[] argument = {instance};
            try {
                formDescription = (FormDescription) _formDescriptionConstructor.newInstance(argument);
            } catch (Exception ee) {
                System.err.println("Error in WidgetWrapperPool's createWidgetWrapperForInstance method");
                ee.printStackTrace();
                _formDescriptionConstructor = null;
                formDescription = null;
            }
        }
        return new WidgetWrapper(_project, instance, this, formDescription);
    }

    public void dispose() {
        for (int counter = 0; counter <= _nextAvailableWidgetWrapper; counter++) {
            //   _availableWidgetWrappers[counter].dispose();
        }
        _nextAvailableWidgetWrapper = -1;
    }

    public static WidgetWrapperPool getPoolForClass(
        Cls cls,
        Class formDescriptionClass,
        int maxSize,
        int numberToAllocateInAdvance) {
        HashMap clsLevel = (HashMap) _widgetWrapperPools.get(cls);
        if (null == clsLevel) {
            clsLevel = new HashMap();
            _widgetWrapperPools.put(cls, clsLevel);
        }

        WidgetWrapperPool widgetWrapperPool = (WidgetWrapperPool) clsLevel.get(formDescriptionClass);
        if (null == widgetWrapperPool) {
            widgetWrapperPool =
                new WidgetWrapperPool(
                    cls.getProject(),
                    cls.getKnowledgeBase(),
                    cls,
                    maxSize,
                    numberToAllocateInAdvance,
                    formDescriptionClass);
            clsLevel.put(formDescriptionClass, widgetWrapperPool);
        }
        return widgetWrapperPool;
    }

    private Instance getTokenInstance() {
        Collection instances = _cls.getInstances();
        if ((instances == null) || (instances.size() == 0)) {
            return null;
        }
        Iterator i = instances.iterator();
        return (Instance) i.next();
    }

    public WidgetWrapper getWidgetWrapperForInstance(Instance instance) {
        if (_nextAvailableWidgetWrapper < 0) {
            return createWidgetWrapperForInstance(instance);
        }
        WidgetWrapper returnValue = _availableWidgetWrappers[_nextAvailableWidgetWrapper];
        returnValue.setInstance(instance);
        _nextAvailableWidgetWrapper--;
        return returnValue;
    }

    public void returnWidgetWrapper(WidgetWrapper widgetWrapperToReturn) {
        if ((null != widgetWrapperToReturn) && (_nextAvailableWidgetWrapper < _maxSize - 1)) {
            _nextAvailableWidgetWrapper++;
            _availableWidgetWrappers[_nextAvailableWidgetWrapper] = widgetWrapperToReturn;
        } else {
            //   widgetWrapperToReturn.dispose();
        }
        return;
    }

    private void setFormDescriptionClass(Class formDescriptionClass) {
        _formDescriptionClass = formDescriptionClass;
        if (null == _formDescriptionClass) {
            return;
        }
        Class[] constructorArguments = {Instance.class};
        try {
            _formDescriptionConstructor = _formDescriptionClass.getConstructor(constructorArguments);
        } catch (Exception ee) {
            System.err.println("Error in WidgetWrapperPool's setFormDescriptionClass method");
            ee.printStackTrace();
            _formDescriptionClass = null;
        }
    }
}
