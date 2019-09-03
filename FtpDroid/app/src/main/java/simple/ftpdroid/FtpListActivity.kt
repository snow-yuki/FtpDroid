package simple.ftpdroid

import android.os.Process;

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_ftp_list.*
import org.apache.commons.net.ftp.FTPFile
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import simple.ftpdroid.FTP.FTP
import java.io.IOException
import java.util.ArrayList

class FtpListActivity : AppCompatActivity() {

    var presentPath = "/"

    private var ftp: FTP? = null
    private lateinit var adapter : FtpFileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ftp_list)

        setSupportActionBar(toolbar)
        loadRemoteView()
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
                adapter.onFildClick = {name ->
                    toast("click file : ${name}")
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
            R.id.action_mkdir -> {
                toast("make dirs")
            }
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

    override fun onStop() {
        super.onStop()
        try {
            ftp!!.closeConnect()
            ftp = null
            Global.ftp = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
