package com.reno.philipshuesampleapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.reno.philipshue.bridgecontrol.BridgeController
import com.reno.philipshue.bridgecontrol.Light
import kotlinx.android.synthetic.main.activity_control_light.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BridgeControlActivity : AppCompatActivity() {
    private lateinit var lightController: BridgeController
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_control_light)

        if (!intent.hasExtra(EXTRA_BRIDGE_IP))
            finish()

        val bridgeIp = intent.getStringExtra(EXTRA_BRIDGE_IP)

        Log.d("test", "bridge ip: $bridgeIp")
        lightController = BridgeController(bridgeIp)

        fetchLightList()

        rvLight.layoutManager = LinearLayoutManager(this)
        rvLight.adapter = ControlAdapter().apply {
            onClickPower = { lightIdx, turnOn ->
                token ?: showToast("no token")
                CoroutineScope(Dispatchers.Main).launch {
                    lightController.turnOn(token!!, lightIdx, turnOn)
                }
            }

            onClickColor = { lightIdx ->
                token ?: showToast("no token")

                var selectColor = Color.WHITE
                ColorPickerDialogBuilder.with(this@BridgeControlActivity)
                    .setTitle("Select Light Color")
                    .initialColor(Color.WHITE)
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(12)
                    .setOnColorSelectedListener {
                        selectColor = it
                    }
                    .setPositiveButton("Choose") { _, _, _ ->
                        CoroutineScope(Dispatchers.Main).launch {
                            lightController.changeColor(
                                token!!,
                                lightIdx,
                                selectColor
                            )
                        }
                    }
                    .setNegativeButton("Close") { dialog, _ ->
                        dialog.dismiss()
                    }.build()
                    .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_control, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_request_light_list) {
            fetchLightList()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchLightList() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                token = lightController.getToken()
                Log.d("token", "token: $token")
                token ?: showToast("no token")

                val lightList = lightController.getLights(token!!)
                Log.d("light", "light list: $lightList")

                (rvLight.adapter as ControlAdapter).addAll(lightList)
            } catch (exception: Exception) {
                exception.printStackTrace()
                exception.message?.let { showToast(it) }
            }

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@BridgeControlActivity, message, Toast.LENGTH_SHORT)
            .show()
    }

    class ControlAdapter : RecyclerView.Adapter<ControlViewHolder>() {
        private var lightList = ArrayList<Light>()
        var onClickPower: ((lightIdx: Int, turnOn: Boolean) -> Unit)? = null
        var onClickColor: ((lightIdx: Int) -> Unit)? = null

        fun addAll(lightList: List<Light>) {
            this.lightList.addAll(lightList)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ControlViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_light, parent, false)
            return ControlViewHolder(view)
        }

        override fun onBindViewHolder(holder: ControlViewHolder, position: Int) {
            holder.onBind(lightList[position], onClickPower, onClickColor)
        }

        override fun getItemCount(): Int {
            return lightList.size
        }

    }

    class ControlViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvLightName: TextView = view.findViewById(R.id.tvLightName)
        private val butLightPower: Button = view.findViewById(R.id.butLightPower)
        private val butLightColor: Button = view.findViewById(R.id.butLightColor)

        fun onBind(
            light: Light,
            onClickPower: ((lightIdx: Int, turnOn: Boolean) -> Unit)?,
            onClickColor: ((lightIdx: Int) -> Unit)?
        ) {
            tvLightName.text = light.name
            butLightPower.text = if (light.state.on) "Turn On" else "Turn Off"
            butLightPower.setOnClickListener {
                onClickPower?.invoke(light.id, !light.state.on)
                light.state.on = !light.state.on
                butLightPower.text = if (light.state.on) "Turn On" else "Turn Off"
            }

            butLightColor.text = itemView.context.getString(R.string.select_color)
            butLightColor.setOnClickListener {
                onClickColor?.invoke(light.id)
            }
        }
    }

    companion object {
        private const val EXTRA_BRIDGE_IP = "extra.bridge_ip"

        fun createIntent(bridgeIp: String, activity: AppCompatActivity): Intent {
            return Intent(activity, BridgeControlActivity::class.java).apply {
                putExtra(EXTRA_BRIDGE_IP, bridgeIp)
            }
        }
    }
}