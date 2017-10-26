package com.movilizer.ps.utils

import com.movilizer.maf.bo.mappings.container.MAFGenericDataContainerEntry
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertEquals

class DataContainerWalkerTest {


    @Test
    void testWalkEmptyDataContainer() throws Exception {
        def container = new MAFGenericDataContainerEntry()
        container.setName('test')
        def walkedPath = walkDatacontainer(container)
        assertEquals "(null <- test:null)", walkedPath
    }

    @Test
    void testWalkSingleStringValueEntry() throws Exception {
        def container = new MAFGenericDataContainerEntry()
        container.setName('test')
        container.setValstr('testValue')
        def walkedPath = walkDatacontainer(container)
        assertEquals "(null <- test:testValue)", walkedPath
    }

    @Test
    void testWalkParentSingleChildStringValueEntry() throws Exception {
        def child = new MAFGenericDataContainerEntry()
        child.setName('child')
        child.setValstr('childValue')
        def container = new MAFGenericDataContainerEntry()
        container.setName('parent')
        container.addEntry(child)
        def walkedPath = walkDatacontainer(container)
        assertEquals "(null <- parent:L[1])(parent <- child:childValue)", walkedPath
    }


    @Ignore //TODO: implement this test
    @Test
    void testWalkParentMultipleChildrenStringValueEntries() throws Exception {
        def container = new MAFGenericDataContainerEntry()
        def walkedPath = "{"
        walkedPath += "}"
        assertEquals "{ }", walkedPath
    }

    @Ignore //TODO: implement this test
    @Test
    void testWalkGrandParentSingleParentSingleChildStringValueEntry() throws Exception {
        def container = new MAFGenericDataContainerEntry()
        def walkedPath = "{"
        walkedPath += "}"
        assertEquals "{ }", walkedPath
    }

    @Ignore //TODO: implement this test
    @Test
    void testWalkGrandParentMultipleMultipleParentMultipleChildrenStringValueEntries() throws Exception {
        def container = new MAFGenericDataContainerEntry()
        def walkedPath = "{"
        walkedPath += "}"
        assertEquals "{ }", walkedPath
    }

    private static String walkDatacontainer(MAFGenericDataContainerEntry container) {
        def walkedPath = ""
        DataContainerWalker.walk(container, { MAFGenericDataContainerEntry entry, MAFGenericDataContainerEntry parent ->
            String value
            if (entry.entryList && !entry.entryList.empty) {
                value = "L[${entry.entryList.size()}]"
            } else if (entry.valb64) {
                value = "b[${entry.valb64.length}]"
            } else {
                value = entry?.valstr
            }
            walkedPath += "(${parent?.name} <- ${entry.name}:${value})"
        })
        return walkedPath
    }
}
