package simple.ftpdroid

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_ftp_list.*
import org.jetbrains.anko.*
import simple.ftpdroid.FTP.FTP
import java.io.File
import java.io.IOException


class FtpListActivity : AppCompatActivity() {

    private val CODE_GET_FILE = 1000

    var presentPath = "/"

    private var ftp: FTP? = null
    private lateinit var adapter : FtpFileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ftp_list)

        setSupportActionBar(toolbar)
        initUI()
        PermissionHandler.checkPermission(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
        loadRemoteView()
    }

    private fun initUI(){
        uploadFileBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent,CODE_GET_FILE)
        }
    }

    private fun uploadFile(uri : Uri){
        val path = getPath(this,uri)
        println("get path========== $path")
        if(null != path){
            val index = path.lastIndexOf('/')
            val purePath = path.substring(0,index+1)
            val fileName = path.substring(index+1,path.length)
            println("======pure path = $purePath")
            println("======file name = $fileName")
            val file = File(path)
            val uploadBean = TransferBean(purePath, presentPath, fileName, file.length(), false, false, 0L, 0L)
            Global.transferList.add(uploadBean)
            if(!Global.isServiceRunning){
                println("启动服务======")
                Global.isServiceRunning = true
                startService<TransferService>()
            }
        }
    }

    private fun loadRemoteView() {
        if (ftp != null) {
            ftp!!.closeConnect()
        }
        ftp = FTP(Global.hostName, Global.userName, Global.password, Global.port)
        Global.ftp = ftp
        doAsync {
            try {
                ftp!!.openConnect()
                println("connect succeed=====================")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ftp!!.listFiles(FTP.REMOTE_PATH)
            uiThread {
                adapter = FtpFileAdapter(this@FtpListActivity)
                fileListView!!.adapter = adapter
                adapter.onFolderClick = {name->
                    println("click folder : ${name}============")
                    presentPath = "$presentPath$name/"
                    println("present path = ${presentPath}===============")
                    doAsync {
                        Global.currentFileList.clear()
                        ftp!!.listFiles(presentPath)
                        println("调用成功======")
                        uiThread {
                            println("ftp.size = ${Global.currentFileList.size}")
                            adapter.notifyDataSetChanged()
                            println("enter folder succeed ===============")
                        }
                    }
                }
                adapter.onFileClick = { name,position ->
                    val dialog = FileCtrlDialog(this@FtpListActivity)
                    dialog.fileName = name
                    dialog.onDownloadClick = {
                        Global.transferList.add(
                                TransferBean("${Environment.getExternalStorageDirectory().absolutePath}/Download/",presentPath,name,Global.currentFileList[position].size,true,false,0L,0L)
                        )
                        if(!Global.isServiceRunning){
                            Global.isServiceRunning = true
                            println("启动下载服务======")
                            startService<TransferService>()
                        }
                        dialog.cancel()
                    }
                    dialog.onDelClick = {
                        doAsync {
                            val fileToDel = "$presentPath$name"
                            println("delete file : $fileToDel")
                            if(fileToDel.length > 1){
                                ftp!!.deleteFile("$presentPath${name}")
                            }
                        }
                        dialog.cancel()
                    }
                    dialog.show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ftp_list_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_transfer -> {
                startActivity<TransferActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (presentPath.equals("/", ignoreCase = true)) {
            finish()
        } else {
            presentPath = presentPath.substring(0, presentPath.length - 1)
            presentPath = presentPath.substring(0,presentPath.lastIndexOf('/',0,true))
            try {
                doAsync {
                    Global.currentFileList.clear()
                    ftp!!.listFiles(presentPath)
                    uiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            ftp!!.closeConnect()
            ftp = null
            Global.ftp = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CODE_GET_FILE && resultCode == Activity.RESULT_OK && data != null){
            val uri = data.data
            println("uri ============== $uri")
            if (uri != null) {
                uploadFile(uri)
            }else{
                println("uri is null======")
                toast("选择文件错误")
            }
        }
    }

    private fun getPath(context: Context, uri: Uri): String? {

        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }
        return null
    }


    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                              selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }


    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}
