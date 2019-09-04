package simple.ftpdroid.FTP

import android.util.Log
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPClientConfig
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import simple.ftpdroid.Global
import simple.ftpdroid.ProgressInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class FTP(private val hostName: String, private val userName: String, private val password: String, private val port: Int) {

    val ftpClient: FTPClient?

    /**
     * FTP当前目录.
     */
    private var currentPath = ""

    /**
     * 统计流量.
     */
    private var response: Double = 0.toDouble()


    init {
        this.ftpClient = FTPClient()
    }


    /**
     * 打开FTP服务.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun openConnect() {
        // 中文转码
        ftpClient!!.controlEncoding = "UTF-8"

        // 连接至服务器
        ftpClient.defaultPort = this.port
        ftpClient.connect(hostName)
        Log.d("无", "ftpClient connect完毕")
        // 获取响应值
        var reply: Int = ftpClient.replyCode // 服务器响应值
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect()
            throw IOException("connect fail: $reply")
        }
        // 登录到服务器
        ftpClient.login(userName, password)
        // 获取响应值
        reply = ftpClient.replyCode
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect()
            throw IOException("connect fail: $reply")
        } else {
            // 获取登录信息
            val config = FTPClientConfig(ftpClient.systemType.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
            config.serverLanguageCode = "zh"
            ftpClient.configure(config)
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode()
            // 二进制文件支持
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)
            println("login")
        }

    }

    /**
     * 关闭FTP服务.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun closeConnect() {
        if (ftpClient != null) {
            // 登出FTP
            ftpClient.logout()
            // 断开连接
            ftpClient.disconnect()
            println("logout")
        }
    }

    fun listFiles(remotePath: String) {
        try {
            println("进入 list Files========")
            val files = ftpClient!!.listFiles(remotePath)
            Collections.addAll(Global.currentFileList, *files)
            println("list. size = " + Global.currentFileList.size + "==========")
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun deleteFile(filePath: String) {
        try {
            ftpClient!!.deleteFile(filePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 下载.
     * @param remotePath FTP目录
     * @param fileName 文件名
     * @param localPath 本地目录
     * @return Result
     * @throws IOException
     */
    @Throws(IOException::class)
    fun download(remotePath: String, fileName: String, localPath: String): Result? {
        var flag = true
        var result: Result? = null
        // 初始化FTP当前目录
        currentPath = remotePath
        // 初始化当前流量
        response = 0.0
        // 更改FTP目录
        ftpClient!!.changeWorkingDirectory(remotePath)
        // 得到FTP当前目录下所有文件
        val ftpFiles = ftpClient.listFiles()
        // 循环遍历
        for (ftpFile in ftpFiles) {
            // 找到需要下载的文件
            if (ftpFile.name == fileName) {
                println("download...")
                // 创建本地目录
                val file = File(localPath + fileName)
                // 下载前时间
                val startTime = Date()
                if (ftpFile.isDirectory) {
                    // 下载多个文件
                    flag = downloadMany(file)
                } else {
                    // 下载当个文件
                    flag = downloadSingle(file, ftpFile)
                }
                // 下载完时间
                val endTime = Date()
                // 返回值
                result = Result(flag, Util.getFormatTime(endTime.time - startTime.time), Util.getFormatSize(response))
            }
        }
        return result
    }

    /**
     * 下载单个文件.
     * @param localFile 本地目录
     * @param ftpFile FTP目录
     * @return true下载成功, false下载失败
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun downloadSingle(localFile: File, ftpFile: FTPFile): Boolean {
        var flag = true
        // 创建输出流
        val outputStream = FileOutputStream(localFile)
        // 统计流量
        response += ftpFile.size.toDouble()
        Log.d("当前下载response", "" + response)
        // 下载单个文件
        flag = ftpClient!!.retrieveFile(localFile.name, outputStream)
        // 关闭文件流
        outputStream.close()
        return flag
    }

    /**
     * 下载多个文件.
     * @param localFile 本地目录
     * @return true下载成功, false下载失败
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun downloadMany(localFile: File): Boolean {
        var flag = true
        // FTP当前目录
        if (currentPath != REMOTE_PATH) {
            currentPath = currentPath + REMOTE_PATH + localFile.name
        } else {
            currentPath += localFile.name
        }
        // 创建本地文件夹
        localFile.mkdir()
        // 更改FTP当前目录
        ftpClient!!.changeWorkingDirectory(currentPath)
        // 得到FTP当前目录下所有文件
        val ftpFiles = ftpClient.listFiles()
        // 循环遍历
        for (ftpFile in ftpFiles) {
            // 创建文件
            val file = File(localFile.path + "/" + ftpFile.name)
            if (ftpFile.isDirectory) {
                // 下载多个文件
                flag = downloadMany(file)
            } else {
                // 下载单个文件
                flag = downloadSingle(file, ftpFile)
            }
        }
        return flag
    }

    /**
     * 上传.
     * @param localFile 本地文件
     * @param remotePath FTP目录
     * @return Result
     * @throws IOException
     */
    @Throws(IOException::class)
    fun uploading(localFile: File, remotePath: String): Result {
        var flag = true
        var result: Result? = null
        // 初始化FTP当前目录
        currentPath = remotePath
        // 初始化当前流量
        response = 0.0
        // 二进制文件支持
        ftpClient!!.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)
        // 使用被动模式设为默认
        ftpClient.enterLocalPassiveMode()
        // 设置模式
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE)
        // 改变FTP目录
        ftpClient.changeWorkingDirectory(remotePath)

        // 获取上传前时间
        val startTime = Date()
        if (localFile.isDirectory) {
            // 上传多个文件
            flag = uploadingMany(localFile)
        } else {
            // 上传单个文件
            flag = uploadingSingle(localFile)
        }
        // 获取上传后时间
        val endTime = Date()
        // 返回值
        result = Result(flag, Util.getFormatTime(endTime.time - startTime.time), Util.getFormatSize(response))
        return result
    }

    /**
     * 上传单个文件.
     * @param localFile 本地文件
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun uploadingSingle(localFile: File): Boolean {
        println("======uploading single")
        var flag = true
        // 创建输入流
        val inputStream = FileInputStream(localFile)
        val pif = ProgressInputStream(inputStream){progress ->
            Global.transferList.first().lastProgress = Global.transferList.first().progress
            Global.transferList.first().progress = progress
        }
        // 统计流量
        response += inputStream.available().toDouble() / 1
        // 上传单个文件
        println("begin store file============")

        flag = ftpClient!!.storeFile(localFile.name, pif)
        println("store file finish======")
        // 关闭文件流
        inputStream.close()
        return flag
    }

    /**
     * 上传多个文件.
     * @param localFile 本地文件夹
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun uploadingMany(localFile: File): Boolean {
        var flag = true
        // FTP当前目录
        if (currentPath != REMOTE_PATH) {
            currentPath = currentPath + REMOTE_PATH + localFile.name
        } else {
            currentPath += localFile.name
        }
        // FTP下创建文件夹
        ftpClient!!.makeDirectory(currentPath)
        // 更改FTP目录
        ftpClient.changeWorkingDirectory(currentPath)
        // 得到当前目录下所有文件
        val files = localFile.listFiles()
        // 遍历得到每个文件并上传
        for (file in files) {
            if (file.isHidden) {
                continue
            }
            if (file.isDirectory) {
                // 上传多个文件
                flag = uploadingMany(file)
            } else {
                // 上传单个文件
                flag = uploadingSingle(file)
            }
        }
        return flag
    }

    internal inner class ConnectionThread : Thread() {
        override fun run() {
            try {
                ftpClient!!.connect(hostName)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    companion object {

        /**
         * FTP根目录.
         */
        val REMOTE_PATH = "/"
    }
}
