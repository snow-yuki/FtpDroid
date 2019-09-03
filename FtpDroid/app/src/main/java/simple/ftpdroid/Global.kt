package simple.ftpdroid

import simple.ftpdroid.FTP.FTP

object Global {
    var hostName = ""
    var userName = ""
    var password = ""
    var port: Int = 2221

    var ftp : FTP? = null

    val transferList : MutableList<TransferBean> = mutableListOf()
}