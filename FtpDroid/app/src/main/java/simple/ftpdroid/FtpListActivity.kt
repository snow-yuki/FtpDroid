package simple.ftpdroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_ftp_list.*

class FtpListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ftp_list)

        loadFileData()
    }

    private fun loadFileData() {
        val tempNameList = arrayListOf("00000","1111","222","333","4444","5555"
        ,"6666","7777","8888","9999")
        val adapter = FtpFileAdapter(this,tempNameList)
        fileListView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_pro_key,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_sync_key -> {
                syncKey()
            }
            R.id.action_key_src -> {
                if(currentKeySrc == localSrc){
                    currentKeySrc = netSrc
                    loadKeyFromServer()
                    toast("key from server")
                }else{
                    currentKeySrc = localSrc
                    loadKeyFromLocal()
                    toast("key from local")
                }
            }
            R.id.action_donate_info -> {
                activity!!.startActivity<DonateInfoActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
