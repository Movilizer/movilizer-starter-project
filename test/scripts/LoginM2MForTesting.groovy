import com.movilizer.maf.scripting.MAFEventContext
import com.movilizer.ps.login.LoginLibrary
import com.movilizer.maf.bo.mappings.container.MAFUploadDataContainer
import com.movilizer.maf.scripting.access.MAFNotificationGateway

final start = System.currentTimeMillis()

MAFUploadDataContainer dataContainer = mafContext.getUploadContainer()
String key = dataContainer.getKey()
MAFNotificationGateway notificationManager = mafContext.getNotificationManager()

MAFEventContext mafContext // @RemoveAtDeployment

LoginLibrary loginLibrary = new LoginLibrary(mafContext)

notificationManager.writeDebug("Upload container received $key for Login library e2e test", null)
boolean isSuccess = true
Hashtable<String, Object> data = new Hashtable<String, Object>()
try {
    String username = dataContainer.getData().getObject('payload').getObject(LoginLibrary.FIELD_USERNAME)
    String password = dataContainer.getData().getObject('payload').getObject(LoginLibrary.FIELD_PASSWORD)
    data = loginLibrary.authenticateUser(username, password)
} catch (Exception e) {
    isSuccess = false
    notificationManager.writeError("Exception raised when executing the test", e)
}

final executionTime = System.currentTimeMillis() - start

// Log result to monitoring
if (isSuccess){
    notificationManager.writeInfo("Datacontainer successfully processed in ${executionTime} ms", dataContainer)
} else {
    notificationManager.writeError("Error while processing datacontainer (time spent: ${executionTime} ms)", dataContainer)
}

final Hashtable<String, Object> payload = new Hashtable<>()
payload.put("isSuccess", isSuccess)
payload.put("data", data)
mafContext.addOnlineContainerReply(
        /*String containerKey*/ key,
        /*Hashtable<?, ?> dataContainer*/ payload
)
notificationManager.writeDebug("Sending reply datacontainer for test", payload)
