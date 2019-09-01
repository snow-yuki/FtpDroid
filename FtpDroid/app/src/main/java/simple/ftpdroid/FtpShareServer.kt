package simple.ftpdroid

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.WritePermission

class FtpShareServer : Service() {

    private var homeDir = ""
    private var ftpServer : FtpServer? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        homeDir = Environment.getExternalStorageDirectory().absolutePath

        stopFtp()
        startFtp()
        println("成功启动ftp服务器======")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopFtp()
        println("关闭ftp服务器成功")
    }

    private fun startFtp(){
        val ftpFactory = FtpServerFactory()
        val baseUser = BaseUser()
        baseUser.name = "admin"
        baseUser.password = "123456"
        baseUser.homeDirectory = homeDir
        val authorities = ArrayList<Authority>()
        authorities.add(WritePermission())
        baseUser.authorities = authorities
        ftpFactory.userManager.save(baseUser)

        val listenerFactory = ListenerFactory()
        listenerFactory.port = Global.port
        ftpFactory.addListener("default",listenerFactory.createListener())

        ftpServer = ftpFactory.createServer()
        ftpServer!!.start()
    }

    private fun stopFtp(){
        if(ftpServer != null){
            ftpServer!!.stop()
            ftpServer = null
        }
    }
}
