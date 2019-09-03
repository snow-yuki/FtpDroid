package simple.ftpdroid

import org.apache.commons.net.ftp.FTPFile
import simple.ftpdroid.FTP.FTP

object Global {
    var hostName = ""
    var userName = ""
    var password = ""
    var port: Int = 2221

    var ftp : FTP? = null

    val transferList : MutableList<TransferBean> = mutableListOf()
    val currentFileList : MutableList<FTPFile> = mutableListOf()
}