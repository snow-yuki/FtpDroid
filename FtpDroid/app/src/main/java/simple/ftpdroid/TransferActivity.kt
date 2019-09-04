package simple.ftpdroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_transfer.*
import java.util.*

class TransferActivity : AppCompatActivity() {

    private var timer : Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
        setSupportActionBar(toolbar)

        initUI()
        timer = Timer()
        val timerTask = object : TimerTask(){
            override fun run() {
                runOnUiThread {
                    loadTransferList()
                }
            }
        }
        timer!!.scheduleAtFixedRate(timerTask,0L,1000L)
    }

    private fun initUI() {
        transferListView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        loadTransferList()
    }

    private fun loadTransferList(){
        transferListView.adapter = TransferListAdapter(this,Global.transferList)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(timer != null){
            timer!!.cancel()
            timer = null
        }
    }
}
