package com.movilizer.ps.utils

import com.google.gson.*
import com.movilizer.maf.bo.mappings.container.MAFGenericDataContainerEntry

import java.lang.reflect.Type

class JsonUtils {

    static String toJSON(Object input) {
        Gson gson = (new GsonBuilder()).registerTypeAdapter(MAFGenericDataContainerEntry.class, new DataContainerAdapter()).create()
        return gson.toJson(input)
    }

    static class DataContainerAdapter implements JsonSerializer<MAFGenericDataContainerEntry> {
        JsonElement serialize(MAFGenericDataContainerEntry src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject outputObject = new JsonObject()
            if (src != null) {
                if (src.getValstr() != null) {
                    outputObject.addProperty(src.getName(), src.getValstr())
                } else if (src.getValb64() != null) {
                    outputObject.addProperty(src.getName(), src.getValb64().encodeBase64().toString())
                } else if (src.getEntryList() != null) {
                    Iterator childrenIter = src.getEntryList().iterator()

                    while(childrenIter.hasNext()) {
                        MAFGenericDataContainerEntry entry = (MAFGenericDataContainerEntry)childrenIter.next()
                        if (entry.getValstr() != null) {
                            outputObject.addProperty(entry.getName(), entry.getValstr())
                        } else if (entry.getValb64() != null) {
                            outputObject.addProperty(entry.getName(), entry.getValb64().encodeBase64().toString())
                        } else if (entry.getEntryList() != null) {
                            outputObject.add(entry.getName(), context.serialize(entry, MAFGenericDataContainerEntry.class))
                        }
                    }
                } else {
                    outputObject.addProperty(src.getName(), "null")
                }
            }
            return outputObject
        }
    }
}
