package simple.ftpdroid

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.jetbrains.anko.toast
import java.io.File

class TransferService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if(Global.ftp == null){
            stopSelf()
            return
        }

        while (Global.transferList.isNotEmpty()){
            val bean = Global.transferList.first()
            if(Global.ftp == null){
                toast("下载失败：ftp已关闭")
                println("下载失败：ftp已关闭")
                return
            }

            if(bean.isDownload){
                Global.ftp!!.download(bean.remotePath, bean.name, bean.localPath)
                Global.transferList.removeAt(0)
                continue
            }else{
                Global.ftp!!.uploading(File("${bean.localPath}${bean.name}"),bean.remotePath)
                Global.transferList.removeAt(0)
                continue
            }
        }

        toast("所有下载完成")
        println("所有任务下载完成")
        stopSelf()
    }
}
