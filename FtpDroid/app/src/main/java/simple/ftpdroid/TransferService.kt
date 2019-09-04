package simple.ftpdroid

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.io.File

class TransferService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        download()
    }

    private fun download(){
        if(Global.ftp == null){
            println("ftp为空，无法下载")
            Global.isServiceRunning = false
            stopSelf()
            return
        }
        doAsync {
            while (Global.transferList.isNotEmpty()){
                val bean = Global.transferList.first()
                if(Global.ftp == null){
                    toast("下载失败：ftp已关闭")
                    println("下载失败：ftp已关闭")
                    Global.isServiceRunning = false
                    stopSelf()
                    return@doAsync
                }

                if(bean.isDownload){
                    println("======开始下载：${bean.remotePath},${bean.name},${bean.localPath}")
                    Global.transferList.first().hasBegan = true
                    Global.ftp!!.download(bean.remotePath, bean.name, bean.localPath)
                    Global.transferList.removeAt(0)
                    continue
                }else{
                    println("======开始上传 ： ${bean.localPath},${bean.name}, to ${bean.remotePath}")
                    Global.ftp!!.uploading(File("${bean.localPath}${bean.name}"), bean.remotePath)
                    println("上传完成======${bean.name} $")
                    toast("上传${bean.name}完成")
                    Global.transferList.removeAt(0)
                    continue
                }
            }

            toast("所有下载完成")
            println("======所有任务下载完成")
            Global.isServiceRunning = false
            stopSelf()
        }
    }
}
