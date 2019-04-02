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
            val udp = UDPServerThread();
            val hashmap = hashMapOf<String, MutableList<Long>>()
            udp.addOnMessageListener {
                val splittet = it.split("=")
                val mac = splittet[0]
                val value = splittet[1].toLongOrNull()
                if (value != null) {
                    runOnUiThread {
                        if (hashmap[mac] == null) {
                            hashmap[mac] = mutableListOf()
                        }
                        hashmap[mac]!!.add(value)
                    }
                }
            }
            udp.start()
            while (true) {
                runOnUiThread {
                    var str = "";
                    val lhashmap = hashmap.clone() as HashMap<String, List<Long>>;
                    hashmap.clear()
                    for (key in lhashmap.keys) {
                        str += key + ": " + lhashmap[key]?.average() + "\r\n"
                    }


                    udpThingy.text = str
                }
                Thread.sleep(1000)
            }
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
