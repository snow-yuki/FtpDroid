package simple.ftpdroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private var SharePanelHeight = 0
    private var ConnectPanelHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SharePanelHeight = dip(116)
        ConnectPanelHeight = dip(216)

        initBtn()
    }

    private fun initBtn(){
        funServer.setOnClickListener {
            detailFunShare.layoutParams.height = 0
            detailFunCon.visibility = View.GONE
            detailFunShare.visibility = View.VISIBLE
            funShareNameText.setTextColor(ContextCompat.getColor(this,R.color.accentPink))
            funConNameText.setTextColor(ContextCompat.getColor(this,R.color.black))
            val animaion = object : Animation(){
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    when(interpolatedTime){
                        1f -> {
                            detailFunShare.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                        }
                        else -> detailFunShare.layoutParams.height = (SharePanelHeight*interpolatedTime).toInt()
                    }
                    detailFunShare.requestLayout()
                }

                override fun willChangeBounds() = true
            }
            animaion.duration = 500L
            detailFunShare.startAnimation(animaion)
        }

        funConnect.setOnClickListener {
            detailFunCon.layoutParams.height = 0
            detailFunShare.visibility = View.GONE
            detailFunCon.visibility = View.VISIBLE
            funShareNameText.setTextColor(ContextCompat.getColor(this,R.color.black))
            funConNameText.setTextColor(ContextCompat.getColor(this,R.color.accentPink))
            val animation = object : Animation(){
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    when(interpolatedTime){
                        1f -> {
                            detailFunCon.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                        }
                        else -> detailFunCon.layoutParams.height = (ConnectPanelHeight*interpolatedTime).toInt()
                    }
                    detailFunCon.requestLayout()
                }

                override fun willChangeBounds() = true
            }
            animation.duration = 500L
            detailFunCon.startAnimation(animation)
        }

        connectBtn.setOnClickListener {
            startActivity<FtpListActivity>()
        }
    }
}
