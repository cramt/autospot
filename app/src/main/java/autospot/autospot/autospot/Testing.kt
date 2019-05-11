package autospot.autospot.autospot

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_testing.*
import kotlinx.android.synthetic.main.content_testing.*

class Testing : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MainActivity.RotationHandler != null) {
            MainActivity.RotationHandler!!.on = true
        }
        setContentView(R.layout.activity_testing)
        setSupportActionBar(toolbar)
        send.setOnClickListener {
            Thread {
                val x = txtPosX.text.toString().toIntOrNull()
                val y = txtPosY.text.toString().toIntOrNull()
                if (x != null && y != null && MainActivity.RotationHandler != null) {
                    if (MainActivity.RotationHandler!!.sendUDPPacket(x.toString() + "," + y.toString())) {
                        runOnUiThread {
                            Toast.makeText(this@Testing, "sent", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@Testing, "didnt sent", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }.start()
        }
    }

    override fun onBackPressed() {
        if (MainActivity.RotationHandler != null) {
            MainActivity.RotationHandler!!.on = false
        }
        super.onBackPressed()
    }

}
