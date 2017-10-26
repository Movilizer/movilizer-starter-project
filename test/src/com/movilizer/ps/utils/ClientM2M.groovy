package com.movilizer.ps.utils

import com.movilizer.maf.bo.mappings.container.MAFGenericDataContainerEntry
import com.movilizer.maf.bo.mappings.container.MAFGenericUploadDataContainer
import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

/**
 * Client for the M2M Movilizer interface.
 *
 * For more info go to https://devtools.movilizer.com/confluence/display/DEV241/MDS+-+M2M+Servlet
 */
class ClientM2M {

    private static DEBUG = false

    /**
     * Send datacontainer to the M2M synchronously
     *
     * @param mdsEndpoint
     * @param participantPassword
     * @param datacontainer
     * @return
     * @throws Exception
     */
    static Map sendDatacontainer(
            String mdsEndpoint,
            String participantPassword,
            MAFGenericUploadDataContainer datacontainer) throws Exception {

            assertMandatoryFieldsInDataContainer(datacontainer)

            Map data = convertGenericDatacontainerToMap(datacontainer)

            Map ret = null
            HTTPBuilder http = new HTTPBuilder(mdsEndpoint)

            http.request(Method.POST, ContentType.JSON) {
                uri.query = [
                        deviceAddress: datacontainer.getDeviceAddress(),
                        password     : participantPassword,
                        dataformat   : 'JSON',
                        data         : JsonOutput.toJson(data),
                ]
                headers.'User-Agent' = 'Professional Services Groovy M2M client'
                headers.'Content-Type' = 'application/x-www-form-urlencoded'
                headers.'Accept' = 'application/json'

                // response handler for a success response code
                response.success = { resp, json ->
                    ret = json
                    if (DEBUG) {
                        println "response status: ${resp.statusLine}"
                        println 'Headers: -----------'
                        resp.headers.each { h ->
                            println " ${h.name} : ${h.value}"
                        }
                        println 'Response data: -----'
                        println ret.toString()
                        println '--------------------'
                    }
                }

                response.failure = { resp ->
                    if (DEBUG) {
                        println "Request failed with status ${resp.status}"
                    }
                }
            }
            return ret
    }

    private static assertMandatoryFieldsInDataContainer(MAFGenericUploadDataContainer dataContainer) {
        assert dataContainer.getKey() != null
    }

    private static Map convertGenericDatacontainerToMap(MAFGenericUploadDataContainer dataContainer) {
        def out = [
                KEY           : dataContainer.getKey(),
                MOVELETKEY    : dataContainer.getMoveletKey(),
                MOVELETKEYEXT : dataContainer.getMoveletKeyExtension(),
                MOVELETVERSION: dataContainer.getMoveletVersion(),
                PARTICIPANTKEY: dataContainer.getParticipantKey(),
                TIMESTAMP     : dataContainer.getCreationTimestamp().getTime()
        ]

        for (MAFGenericDataContainerEntry entry : dataContainer.getData().getEntryList()) {
            out.putAll(recursiveConvertDataContainerEntryToMap(entry))
        }
        return out
    }

    private static Map recursiveConvertDataContainerEntryToMap(MAFGenericDataContainerEntry entry) {
        if (entry.getEntryList()) {
            def out = [:]
            def children = [:]
            for(MAFGenericDataContainerEntry iterEntry: entry.getEntryList()) {
                children.putAll(recursiveConvertDataContainerEntryToMap(iterEntry))
            }
            out.put(entry.getName(), children)
            return out
        } else if (entry.getValb64() && entry.getValb64().length > 0) {
            def out = [:]
            out.put(entry.getName(), entry.getValb64().encodeBase64().toString())
            return out
        } else {
            def out = [:]
            out.put(entry.getName(), entry.getValstr())
            return out
        }
    }

}
