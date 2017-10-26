package com.movilizer.ps.utils

import com.movilizer.maf.bo.mappings.container.MAFGenericDataContainerEntry


class DataContainerWalker {

    static void walk(List<MAFGenericDataContainerEntry> dataContainerEntries, Closure closure) {
        dataContainerEntries.each { entry -> walk(entry, closure) }
    }

    static void walk(MAFGenericDataContainerEntry dataContainerEntry, Closure closure) {
        Stack<MAFGenericDataContainerEntry[]> executionStack = new Stack<>()
        executionStack.push(createTuple(dataContainerEntry, null))
        while(!executionStack.isEmpty()) {
            def entryWithParent = executionStack.pop()
            def entry = entryWithParent[0]
            def parent = entryWithParent[1]
            if (entry.getEntryList() && !entry.getEntryList().isEmpty()) {
                entry.getEntryList().each { child -> executionStack.push(createTuple(child, entry)) }
            }
            closure.curry(entry, parent).run()
        }

    }

    private static MAFGenericDataContainerEntry[] createTuple(MAFGenericDataContainerEntry first, MAFGenericDataContainerEntry second) {
        MAFGenericDataContainerEntry[] tuple = new MAFGenericDataContainerEntry[2]
        tuple[0] = first
        tuple[1] = second
        return tuple
    }
}