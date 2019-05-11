package autospot.autospot.autospot

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_session.*
import kotlinx.android.synthetic.main.content_session.*

class SessionActivity : AppCompatActivity() {
    fun onPosition(it: Vector2) {
        runOnUiThread {
            posX.text = it.x.toString()
            posY.text = it.y.toString()
        }
    }

    val handler = MainActivity.PositionHandler!!
    val oldActivity = handler.activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)
        setSupportActionBar(toolbar)
        val pref = this.getPreferences(Context.MODE_PRIVATE).edit()
        handler.positions.toList().forEach {
            pref.putFloat("mac_" + it.first+"_x", it.second.x.toFloat())
            pref.putFloat("mac_" + it.first+"_y", it.second.y.toFloat())
        }
        pref.apply()
        if(MainActivity.RotationHandler != null){
            MainActivity.RotationHandler!!.on = true
        }
        handler.addOnPositionListener(::onPosition)
        MainActivity.RotationHandler?.addOnStepsListener { xy, yz ->
            runOnUiThread {
                stepsXY.text = xy.toString()
                stepsYZ.text = yz.toString()
            }
        }
    }

    override fun onBackPressed() {
        handler.removeOnPositionListener(::onPosition)
        handler.activity = oldActivity
        if(MainActivity.RotationHandler != null){
            MainActivity.RotationHandler!!.on = false
        }
        super.onBackPressed()
    }
}
