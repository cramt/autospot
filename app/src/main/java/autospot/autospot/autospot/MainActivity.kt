package autospot.autospot.autospot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast

import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        var PositionHandler: AutoSpotPositionHandler? = null
        var RotationHandler: AutoSpotRotationHandler? = null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        val ESPHeaders = arrayOf(ESPHeader1, ESPHeader2, ESPHeader3)
        val ESPStrength = arrayOf(ESPstrength1, ESPstrength2, ESPstrength3)
        val ESPPosX = arrayOf(ESPPosX1, ESPPosX2, ESPPosX3)
        val ESPPosY = arrayOf(ESPPosY1, ESPPosY2, ESPPosY3)

        Thread {
            val udp = UDPServerThread();
            PositionHandler = AutoSpotPositionHandler(udp, this)
            RotationHandler = AutoSpotRotationHandler()
            PositionHandler!!.addOnHomeListener { ip ->
                RotationHandler!!.ip = ip
            }
            PositionHandler!!.addOnPositionListener(RotationHandler!!::onPosition)
            fun onRawData(it: HashMap<String, Double>) {
                runOnUiThread {
                    val s_x = SpotlightPosX.text.toString().toDoubleOrNull()
                    if (s_x != null) {
                        RotationHandler!!.x = s_x
                    }
                    val s_y = SpotlightPosY.text.toString().toDoubleOrNull()
                    if (s_y != null) {
                        RotationHandler!!.y = s_y
                    }
                    val s_z = SpotlightPosZ.text.toString().toDoubleOrNull()
                    if (s_z != null) {
                        RotationHandler!!.z = s_z
                    }
                    if (it.keys.count() > 3) {
                        statusText.text = "too many (" + it.keys.count().toString() + ") EPS's connected"
                    } else {
                        statusText.text = it.keys.count().toString() + " EPS's connected"
                        val positions = hashMapOf<String, Vector2>()
                        for ((i, key) in it.keys.withIndex()) {
                            ESPHeaders[i].text = "\r\n" + key
                            ESPStrength[i].text = it[key]!!.toString() + " RSSI"
                            val x = ESPPosX[i].text.toString().toFloatOrNull()
                            val y = ESPPosY[i].text.toString().toFloatOrNull()
                            if (y != null && x != null) {
                                positions[key] = Vector2(x.toDouble(), y.toDouble())
                            }
                        }
                        PositionHandler!!.positions = positions
                    }
                }
            }
            PositionHandler!!.addOnRawDataListener(::onRawData)
            runOnUiThread {
                startSessionBtn.setOnClickListener {
                    PositionHandler!!.removeOnRawDataListener(::onRawData)
                    val intent = Intent(this, SessionActivity::class.java)
                    startActivity(intent)
                }
            }
            udp.start()
        }.start()
    }
}
