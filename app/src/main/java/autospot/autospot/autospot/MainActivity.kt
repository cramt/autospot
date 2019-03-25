package autospot.autospot.autospot

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast

import kotlinx.android.synthetic.main.main_activity.*
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.android.synthetic.main.content_main.*
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        };

        Thread {
            val ip = wifiIpAddress(this@MainActivity)
            runOnUiThread {
                Toast.makeText(this@MainActivity, ip, Toast.LENGTH_LONG).show()
            }
            /*
            try {
                val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiMgr.connectionInfo
                val ip = wifiInfo.ipAddress
                val ipAddress = Formatter.formatIpAddress(ip)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, ip.toString(), Toast.LENGTH_LONG).show()
                }
            }
            catch (e: Throwable){
                runOnUiThread {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
            */

            val udp = UDPServerThread();
            val a = mutableListOf<Long>()
            var i = 0
            udp.addOnMessageListener {
                val b = it.toLongOrNull()
                i++
                udpThingy.text = (System.currentTimeMillis() - b!!).toString()
                /*
                if(b != null){
                    a.add(System.currentTimeMillis() - b)
                    if(a.size > 9){
                        udpThingy.text = a.average().toString()
                        a.clear()
                    }
                }
                */
            }
            udp.start()
        }.start()
    }

    protected fun wifiIpAddress(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }

        val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()

        var ipAddressString: String?
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            Log.e("WIFIIP", "Unable to get host address.")
            ipAddressString = null
        }

        return ipAddressString
    }
}
