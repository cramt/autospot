package autospot.autospot.autospot

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_testing.*
import kotlinx.android.synthetic.main.content_testing.*

class Testing : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)
        setSupportActionBar(toolbar)
        send.setOnClickListener {
            Thread {
                val x = txtPosX.text.toString().toDoubleOrNull()
                val y = txtPosY.text.toString().toDoubleOrNull()
                if (x != null && y != null && MainActivity.RotationHandler != null) {
                    MainActivity.RotationHandler!!.sendUDPPacket(x.toString() + "," + y.toString())
                }
            }.start()
        }
    }

}
