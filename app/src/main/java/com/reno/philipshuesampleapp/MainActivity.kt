package com.reno.philipshuesampleapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reno.philipshue.bridge.IUPnpDiscoveryManager
import com.reno.philipshue.bridge.UPnPDiscoveryManager
import com.reno.philipshue.model.UPnPDevice
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

        val uPnPManager = UPnPDiscoveryManager.Builder(this).build()
        uPnPManager.discoverDevices(object: IUPnpDiscoveryManager.UPnPListener {
            override fun onStart() {
                Log.d(TAG, "Start discovery")
            }

            override fun onFoundNewDevice(device: UPnPDevice) {
                Log.d(TAG, "Found new device")
                Log.d("App", device.location)
                myDataSet.add(device.toString())
                (mAdapter as MyAdapter).notifyDataSetChanged()
            }

            override fun onFinish(devices: HashSet<UPnPDevice>) {
                // To do something
                Log.d(TAG,"Discovery finished")
            }

            override fun onError(exception: Exception) {
                Log.d(TAG, "Error: " + exception.localizedMessage)
                exception.printStackTrace()
            }

        })
    }

    private class MyAdapter internal constructor(private val mDataset: ArrayList<String>) :
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
            holder.mTextView.text = mDataset[position]
        }

        override fun getItemCount(): Int {
            return mDataset.size
        }

    }
}
