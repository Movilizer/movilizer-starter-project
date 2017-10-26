package com.movilizer.ps.login

import com.movilizer.maf.bo.mappings.container.MAFUploadDataContainer
import com.movilizer.maf.scripting.MAFEventContext
import com.movilizer.maf.scripting.access.MAFNotificationGateway


/**
 * Simple example on how to deal with online containers in MAF. Not to be used as a production login example.
 */
class LoginLibrary {
    public static final LOGIN_CONTAINER_KEY = 'LOGIN'
    public static final FIELD_USERNAME = 'username'
    public static final FIELD_PASSWORD = 'password'
    public static final RESPONSE_FIELD_SUCCESS = 'success'
    public static final RESPONSE_FIELD_CODE = 'code'
    public static final RESPONSE_FIELD_MESSAGE = 'message'

    public static final SUCCESS = 'true'
    public static final SUCCESS_CODE = '200'
    public static final SUCCESS_MESSAGE = 'Successfully logged in'

    public static final FAILURE = 'false'
    public static final FAILURE_CODE = '400'
    public static final FAILURE_MESSAGE = 'Username or password incorrect'

    private MAFEventContext context
    private MAFNotificationGateway logger

    /**
     * Mandatory empty constructor for MAF
     */
    LoginLibrary(){}

    /**
     * Main constructor to be used in the scripts
     * @param context with all the needed MAF utils and information
     */
    LoginLibrary(MAFEventContext context){
        this.context = context
        this.logger = context.getNotificationManager()
    }

    /**
     * Main function to be run in the scripts side.
     *
     * Note: Running all script logic inside the library makes it easier to unit test all your code.
     */
    void runScript() {
        for (MAFUploadDataContainer container in context.getOnlineContainers()){
            if (LOGIN_CONTAINER_KEY == container.getKey()){
                processLoginContainer(container)
                break
            }
        }
    }

    void processLoginContainer(MAFUploadDataContainer container) {
        String username = container.getData().getObject(FIELD_USERNAME)
        String password = container.getData().getObject(FIELD_PASSWORD)
        Hashtable response = authenticateUser(username, password)
        context.addOnlineContainerReply(LOGIN_CONTAINER_KEY, response)
    }

    Hashtable<String, Object> authenticateUser(String username, String password) {
        Hashtable response = new Hashtable()
        if (username == 'movilizer' && password == 'secret') {
            response.put(RESPONSE_FIELD_SUCCESS, SUCCESS)
            response.put(RESPONSE_FIELD_CODE, SUCCESS_CODE)
            response.put(RESPONSE_FIELD_MESSAGE, SUCCESS_MESSAGE)
        } else {
            response.put(RESPONSE_FIELD_SUCCESS, FAILURE)
            response.put(RESPONSE_FIELD_CODE, FAILURE_CODE)
            response.put(RESPONSE_FIELD_MESSAGE, FAILURE_MESSAGE)
        }
        response
    }

}