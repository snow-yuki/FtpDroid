package simple.ftpdroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_transfer.*

class TransferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
        setSupportActionBar(toolbar)

        initUI()
    }

    private fun initUI() {
        loadTransferList()
    }

    private fun loadTransferList(){
        transferListView.adapter = TransferListAdapter(this,Global.transferList)
    }
}
