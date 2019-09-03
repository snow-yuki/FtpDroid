package simple.ftpdroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_transfer.*

class TransferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        initUI()
    }

    private fun initUI() {
        loadTransferList()
    }

    private fun loadTransferList(){
        //todo: 定时（1s）刷新列表
    }
}
