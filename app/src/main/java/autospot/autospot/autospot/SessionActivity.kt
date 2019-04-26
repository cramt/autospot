package autospot.autospot.autospot

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
        handler.activity = this

        handler.addOnPositionListener(::onPosition)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        handler.removeOnPositionListener(::onPosition)
        handler.activity = oldActivity
    }
}
