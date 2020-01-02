package com.reno.philipshuesampleapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reno.philipshue.bridge.local.IDiscoveryManager
import com.reno.philipshue.bridge.local.NUPnPDiscoveryManager
import com.reno.philipshue.bridge.local.UPnPDiscoveryManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private val myDataSet = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)

        // use a linear layout manager
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager

        // specify an adapter (see also next example)
        mAdapter = MyAdapter(myDataSet)
        mRecyclerView.adapter = mAdapter

        val uPnPDiscoveryManager = UPnPDiscoveryManager.Builder(this).build()
        CoroutineScope(Dispatchers.IO).launch {
            val uPnPDevices = uPnPDiscoveryManager.discoverDevices().toList()
            uPnPDevices.forEach { myDataSet.add(it.toString()) }
        }

        myDataSet.add("END")

        val discoverManager: IDiscoveryManager =
            NUPnPDiscoveryManager()
        CoroutineScope(Dispatchers.IO).launch {
            val bridge = discoverManager.getBridges()
            launch(Dispatchers.Main) {
                bridge.forEach {
                    myDataSet.add(it.toString())
                }
            }
        }

        login_button.setOnClickListener {
            startActivity(Intent(this, HueLoginActivity::class.java))
        }
    }

    private class MyAdapter internal constructor(private val dataSet: ArrayList<String>) :
        RecyclerView.Adapter<MyAdapter.ViewHolder?>() {

        internal inner class ViewHolder(view: View) :
            RecyclerView.ViewHolder(view) {
            val mTextView: TextView = view.findViewById(R.id.textView)

        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_row_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.mTextView.text = dataSet[position]
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

    }
}
