package edu.stanford.smi.protegex.widget.scatterbox;

import edu.stanford.smi.protege.model.*;
import java.util.*;

/**
 *  Description of the Interface
 *
 * @author    William Grosso <grosso@smi.stanford.edu>
 */
public interface KBQueryUtils extends Constants {

    public Instance getDomainObject(Instance entry);

    // domain
    public Cls getDomainType(Cls functionCls);

    public Cls getDomainTypeFromEntryCls(Cls entryCls);

    public Collection getDomainValueSlots(Cls entryCls);

    // entry
    public Slot getEntriesSlot(Cls functionCls);

    public Cls getEntryCls(Cls functionCls);

    public Instance getOrCreateDomainObject(Instance entry);

    public Instance getOrCreateRangeObject(Instance entry);

    public Instance getRangeObject(Instance entry);

    // range
    public Cls getRangeType(Cls functionCls);

    public Cls getRangeTypeFromEntryCls(Cls entryCls);

    public Slot getRangeValueSlot(Cls entryCls);

    public Slot getRangeValueSlot(Instance entry);

    public Collection getRangeValueSlots(Cls entryCls);

    public Collection getRangeValueSlots(Instance entry);

    public void setDomainObject(Instance entry, Instance domainObject);

    public void setRangeObject(Instance entry, Instance rangeObject);
}
