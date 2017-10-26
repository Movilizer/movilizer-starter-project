package com.movilizer.ps.login

import com.movilizer.maf.bo.mappings.container.MAFGenericDataContainer
import com.movilizer.maf.bo.mappings.container.MAFGenericDataContainerEntry
import com.movilizer.maf.bo.mappings.container.MAFGenericUploadDataContainer
import com.movilizer.maf.bo.mappings.container.MAFUploadDataContainer
import com.movilizer.maf.scripting.MAFEventContext
import com.movilizer.maf.scripting.access.MAFNotificationGateway
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import static org.junit.Assert.assertEquals
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

class LoginLibraryTest {
    private static final String CORRECT_USERNAME = 'movilizer'
    private static final String CORRECT_PASSWORD = 'secret'
    private static final String INCORRECT_USERNAME = 'mobilizer'

    @Mock
    MAFNotificationGateway notificationManager
    @Mock
    MAFEventContext context

    LoginLibrary loginLibrary

    List<MAFUploadDataContainer> onlineContainers

    @Before
    void setUp() {
        MockitoAnnotations.initMocks(this)

        onlineContainers = new ArrayList<>(1)

        when(context.getOnlineContainers()).thenReturn(onlineContainers)
        when(context.getNotificationManager()).thenReturn(notificationManager)

        loginLibrary = new LoginLibrary(context)
    }

    @Test
    void testRunScriptWithNoOnlineContainers() {
        loginLibrary.runScript()

        verify(context, times(0)).addOnlineContainerReply(anyString(), any(Hashtable.class))
    }

    @Test
    void testRunScriptWithOnlineContainersOnlyFirstContainerRuns() {
        MAFUploadDataContainer container = createLoginDataContainer(CORRECT_USERNAME, CORRECT_PASSWORD)
        onlineContainers.add(container)
        loginLibrary.runScript()

        verify(context, times(1)).addOnlineContainerReply(anyString(), any(Hashtable.class))

        verifyOnlineContainerReplies(LoginLibrary.LOGIN_CONTAINER_KEY, LoginLibrary.SUCCESS,
                LoginLibrary.SUCCESS_CODE, LoginLibrary.SUCCESS_MESSAGE)
    }

    @Test
    void testAuthenticateCorrectUser() throws Exception {
        Hashtable<String, Object> response = loginLibrary.authenticateUser(CORRECT_USERNAME, CORRECT_PASSWORD)

        assertResponse(response, LoginLibrary.SUCCESS, LoginLibrary.SUCCESS_CODE, LoginLibrary.SUCCESS_MESSAGE)
    }

    @Test
    void testAuthenticateIncorrectUser() throws Exception {
        Hashtable<String, Object> response = loginLibrary.authenticateUser(INCORRECT_USERNAME, CORRECT_PASSWORD)

        assertResponse(response, LoginLibrary.FAILURE, LoginLibrary.FAILURE_CODE, LoginLibrary.FAILURE_MESSAGE)
    }

    private MAFUploadDataContainer createLoginDataContainer(String username, String password) {
        MAFGenericDataContainerEntry userEntry = new MAFGenericDataContainerEntry()
        userEntry.setName(LoginLibrary.FIELD_USERNAME)
        userEntry.setValstr(username)

        MAFGenericDataContainerEntry passwordEntry = new MAFGenericDataContainerEntry()
        passwordEntry.setName(LoginLibrary.FIELD_PASSWORD)
        passwordEntry.setValstr(password)

        MAFGenericDataContainer dataContainer = new MAFGenericDataContainer()
        dataContainer.addEntry(userEntry)
        dataContainer.addEntry(passwordEntry)

        MAFGenericUploadDataContainer uploadDataContainer = new MAFGenericUploadDataContainer()
        uploadDataContainer.setData(dataContainer)
        MAFUploadDataContainer container = new MAFUploadDataContainer()
        container.setData(uploadDataContainer)
        container.setKey(LoginLibrary.LOGIN_CONTAINER_KEY)
        container
    }

    private void verifyOnlineContainerReplies(String containerId, String success, String code, String message) {
        ArgumentCaptor<String> containerIdArgument = ArgumentCaptor.forClass(String.class)
        ArgumentCaptor<Hashtable> onlineContainersArgument = ArgumentCaptor.forClass(Hashtable.class)

        verify(context).addOnlineContainerReply(containerIdArgument.capture(), onlineContainersArgument.capture())

        assertEquals containerId, containerIdArgument.getValue()

        Hashtable<String, Object> response = onlineContainersArgument.getValue()

        assertResponse(response, success, code, message)
    }

    private void assertResponse(Hashtable<String, Object> response, String success, String code, String message) {
        assertEquals success, response.get(LoginLibrary.RESPONSE_FIELD_SUCCESS)
        assertEquals code, response.get(LoginLibrary.RESPONSE_FIELD_CODE)
        assertEquals message, response.get(LoginLibrary.RESPONSE_FIELD_MESSAGE)
    }

}