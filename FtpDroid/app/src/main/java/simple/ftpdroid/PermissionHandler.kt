package simple.ftpdroid

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

class PermissionHandler {
    companion object {
        fun hasPermission(c:Context,permissionName:String):Boolean{
            if(ContextCompat.checkSelfPermission(c, permissionName) != PackageManager.PERMISSION_GRANTED){
                return false
            }
            return true
        }

        fun checkPermission(a:Activity,permissions:Array<String>,requestCode: Int):Boolean{
            val neededPermission = ArrayList<String>()
            permissions.filterNotTo(neededPermission) { hasPermission(a, it) }
            if(neededPermission.size > 0){
                requirePermission(a,neededPermission.toTypedArray(),requestCode)
                return false
            }
            return true
        }

        private fun requirePermission(a:Activity, permissions: Array<String>, requestCode:Int){
            ActivityCompat.requestPermissions(a,permissions,requestCode)
        }
    }
}