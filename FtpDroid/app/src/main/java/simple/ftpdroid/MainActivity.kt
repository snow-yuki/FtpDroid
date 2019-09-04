package simple.ftpdroid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import android.net.wifi.WifiManager

class MainActivity : AppCompatActivity() {

    private var SharePanelHeight = 0
    private var ConnectPanelHeight = 0

    private var isBeingServer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        SharePanelHeight = dip(116)
        ConnectPanelHeight = dip(216)

        initUI()
    }

    private fun requestPermission(){
        PermissionHandler.checkPermission(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
    }

    private fun initUI(){
        hostNameEditText.setText("192.168.8.192")
        userNameEditText.setText("admin")
        pwdEditText.setText("123456")

        funServer.setOnClickListener {
            expendServerPanel()
        }

        funConnect.setOnClickListener {
            if(isBeingServer){
                toast("已作为FTP服务器，若要连接其他手机请先关闭FTP服务器")
            }else{
                expendConnectPanel()
            }
        }

        connectBtn.setOnClickListener {
            startConnect()
        }

        createServerBtn.setOnClickListener {
            if(isBeingServer){
                stopFTPServer()
                createServerBtn.text = "Create Server"
                shareAddressText.text = "点击下面的按钮创建FTP服务器"
            }else{
                if(createServer()){
                    createServerBtn.text = "Stop Server"
                }
            }
        }
    }

    private fun startConnect(){
        Global.hostName = hostNameEditText.text.toString()
        Global.userName = userNameEditText.text.toString()
        Global.password = pwdEditText.text.toString()
        if(Global.hostName.isBlank()){
            println("地址不能为空")
            toast("地址不能为空")
            return
        }
        if(Global.userName.isBlank()){
            println("用户名不能为空")
            toast("用户名不能为空")
            return
        }
        if(Global.password.isBlank()){
            println("密码不能为空")
            toast("密码不能为空")
            return
        }
        startActivity<FtpListActivity>()
    }

    private fun createServer(): Boolean {
        val serverIP = getIp()
        if(serverIP.isBlank()){
            toast("创建服务失败：无法获取ip地址")
            return false
        }
        shareAddressText.text = "请访问ftp://$serverIP:${Global.port}"
        startService(Intent(this,FtpShareServer::class.java))
        isBeingServer = true
        return true
    }

    private fun expendServerPanel(){
        detailFunShare.layoutParams.height = 0
        detailFunCon.visibility = View.GONE
        detailFunShare.visibility = View.VISIBLE
        funShareNameText.setTextColor(ContextCompat.getColor(this,R.color.accentPink))
        funConNameText.setTextColor(ContextCompat.getColor(this,R.color.black))
        val animaion = object : Animation(){
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                when(interpolatedTime){
                    1f -> {
                        detailFunShare.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    }
                    else -> detailFunShare.layoutParams.height = (SharePanelHeight*interpolatedTime).toInt()
                }
                detailFunShare.requestLayout()
            }

            override fun willChangeBounds() = true
        }
        animaion.duration = 500L
        detailFunShare.startAnimation(animaion)
    }

    private fun expendConnectPanel(){
        detailFunCon.layoutParams.height = 0
        detailFunShare.visibility = View.GONE
        detailFunCon.visibility = View.VISIBLE
        funShareNameText.setTextColor(ContextCompat.getColor(this,R.color.black))
        funConNameText.setTextColor(ContextCompat.getColor(this,R.color.accentPink))
        val animation = object : Animation(){
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                when(interpolatedTime){
                    1f -> {
                        detailFunCon.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    }
                    else -> detailFunCon.layoutParams.height = (ConnectPanelHeight*interpolatedTime).toInt()
                }
                detailFunCon.requestLayout()
            }

            override fun willChangeBounds() = true
        }
        animation.duration = 500L
        detailFunCon.startAnimation(animation)
    }

    private fun stopFTPServer(){
        stopService(Intent(this,FtpShareServer::class.java))
        isBeingServer = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopFTPServer()
    }

    fun getIp(): String {
        //获取wifi服务
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }
        val wifiInfo = wifiManager.connectionInfo
        val ipAddress = wifiInfo.ipAddress
        return intToIp(ipAddress)
    }

    private fun intToIp(i: Int): String {

        return (i and 0xFF).toString() + "." +
                (i shr 8 and 0xFF) + "." +
                (i shr 16 and 0xFF) + "." +
                (i shr 24 and 0xFF)
    }
}
