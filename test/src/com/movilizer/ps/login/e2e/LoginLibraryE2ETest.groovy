package com.movilizer.ps.login.e2e

import com.movilizer.maf.bo.mappings.container.MAFGenericDataContainer
import com.movilizer.maf.bo.mappings.container.MAFGenericDataContainerEntry
import com.movilizer.maf.bo.mappings.container.MAFGenericUploadDataContainer
import com.movilizer.ps.login.LoginLibrary
import com.movilizer.ps.utils.ClientM2M
import groovy.json.JsonSlurper
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * Login library E2E test suite
 *
 * (remember to have the related M2M script deployed for this tests to work)
 */
class LoginLibraryE2ETest {

    static String mdsEndpoint
    static String deviceAddress
    static String participantPassword
    final static moveletKey = 'com.movilizer.ps.login.test'
    final static moveletKeyExtension = ''

    @BeforeClass
    static void setUp() {
        def json = new JsonSlurper()
        def config = json.parse(getClass().getResourceAsStream('/movilizer.json'))
        assert config instanceof Map
        mdsEndpoint = config.endpoint.m2m
        deviceAddress = config.participant.deviceAddress
        participantPassword = config.participant.password
    }

    @Test
    void testUserWithWrongCredentialsFailsToAuthenticate() throws Exception {
        MAFGenericDataContainer payload = createLoginRequestContainer('baduser',
                'its not gonna work')

        def response = performM2MRequest(payload)
        assertEquals LoginLibrary.FAILURE, extractLoginSuccess(response)
        assertEquals LoginLibrary.FAILURE_CODE, extractLoginCode(response)
        assertEquals LoginLibrary.FAILURE_MESSAGE, extractLoginMessage(response)
    }

    private static MAFGenericDataContainer createLoginRequestContainer(String username,
                                                                       String password) {
        MAFGenericDataContainerEntry usernameEntry = new MAFGenericDataContainerEntry()
        usernameEntry.setName(LoginLibrary.FIELD_USERNAME)
        usernameEntry.setValstr(username)

        MAFGenericDataContainerEntry passwordEntry = new MAFGenericDataContainerEntry()
        passwordEntry.setName(LoginLibrary.FIELD_PASSWORD)
        passwordEntry.setValstr(password)

        MAFGenericDataContainer payload = new MAFGenericUploadDataContainer()
        payload.addEntry(usernameEntry)
        payload.addEntry(passwordEntry)
        payload
    }

    private static Map performM2MRequest(MAFGenericDataContainer payload) {
        MAFGenericUploadDataContainer container = createRequestDataContainer(payload)
        Map response = ClientM2M.sendDatacontainer(mdsEndpoint, participantPassword, container)
        boolean isSuccess = extractIsSuccess(response)
        assertEquals true, isSuccess
        response
    }

    private static boolean extractIsSuccess(Map mafResponse) throws Exception {
        if(!mafResponse) return false
        mafResponse.ENTRIES[0].isSuccess == "true"
    }

    private static String extractLoginSuccess(Map mafResponse) throws Exception {
        mafResponse.ENTRIES.ENTRIES[LoginLibrary.RESPONSE_FIELD_SUCCESS][0]
    }

    private static String extractLoginCode(Map mafResponse) throws Exception {
        mafResponse.ENTRIES.ENTRIES[LoginLibrary.RESPONSE_FIELD_CODE][0]
    }

    private static String extractLoginMessage(Map mafResponse) throws Exception {
        mafResponse.ENTRIES.ENTRIES[LoginLibrary.RESPONSE_FIELD_MESSAGE][0]
    }

    private static MAFGenericUploadDataContainer createRequestDataContainer(
            MAFGenericDataContainer payload) {
        MAFGenericDataContainer wrappedPayload = new MAFGenericUploadDataContainer()
        MAFGenericDataContainerEntry payloadEntry = new MAFGenericDataContainerEntry()
        payloadEntry.setName("payload")
        payloadEntry.addAllEntry(payload.getEntryList())
        wrappedPayload.addEntry(payloadEntry)
        MAFGenericUploadDataContainer container = new MAFGenericUploadDataContainer()
        container.setKey(UUID.randomUUID().toString())
        container.setMoveletKey(moveletKey)
        container.setMoveletKeyExtension(moveletKeyExtension)
        container.setMoveletVersion(1L)
        container.setDeviceAddress(deviceAddress)
        container.setCreationTimestamp(new Date())
        container.setData(wrappedPayload)
        container
    }
}
