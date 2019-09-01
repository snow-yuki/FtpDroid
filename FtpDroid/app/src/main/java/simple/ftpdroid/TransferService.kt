package simple.ftpdroid

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.jetbrains.anko.toast

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

        while (Global.downloadList.isNotEmpty() or Global.uploadList.isNotEmpty()){
            if(Global.downloadList.isNotEmpty()){
                val downloadInfo = Global.downloadList.first()
                if(Global.ftp != null){
                    Global.ftp!!.download(downloadInfo.remotePath, downloadInfo.remoteFileName, downloadInfo.localPath)
                    Global.downloadList.removeAt(0)
                    continue
                }else{
                    println("ftp连接已关闭")
                    toast("下载失败：ftp连接已关闭")
                    return
                }
            }
            if(Global.uploadList.isNotEmpty()){
                val uploadInfo = Global.uploadList.first()
                if(Global.ftp != null){
                    Global.ftp!!.uploading(uploadInfo.localFile,uploadInfo.remotePath)
                    Global.uploadList.removeAt(0)
                    continue
                }else{
                    println("ftp连接已关闭")
                    toast("上传失败：ftp连接已关闭")
                    return
                }
            }
        }

        toast("所有下载完成")
        println("所有任务下载完成")
        stopSelf()
    }
}
