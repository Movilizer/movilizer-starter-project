import com.movilizer.maf.scripting.MAFEventContext
import com.movilizer.ps.login.LoginLibrary

MAFEventContext mafContext // @RemoveAtDeployment

LoginLibrary loginLibrary = new LoginLibrary(mafContext)
loginLibrary.runScript()