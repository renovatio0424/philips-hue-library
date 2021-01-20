package com.reno.philipshuesampleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reno.philipshue.bridge.BridgeManager
import com.reno.philipshue.bridge.Bridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private var bridgeAdapter: RecyclerView.Adapter<*>? = null
    private val myDataSet = ArrayList<Bridge>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.rvBridge).apply {
            setHasFixedSize(true)
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@MainActivity)
            layoutManager = mLayoutManager
            bridgeAdapter = BridgeAdapter(this@MainActivity, myDataSet)
            adapter = bridgeAdapter
        }

        initBridgeList()
    }

    private fun initBridgeList() {
        CoroutineScope(Dispatchers.Main).launch {
            val bridgeList = BridgeManager().getBridgeList()
            myDataSet.addAll(bridgeList)
            bridgeAdapter?.notifyDataSetChanged()
        }
    }

    private class BridgeAdapter(
        private val activity: MainActivity,
        private val dataSet: ArrayList<Bridge>
    ) : RecyclerView.Adapter<BridgeAdapter.ViewHolder?>() {

        inner class ViewHolder(view: View) :
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
            holder.mTextView.text = dataSet[position].toString()
            holder.mTextView.setOnClickListener {
                Toast.makeText(activity, "clicked!", Toast.LENGTH_SHORT).show()
                BridgeControlActivity.createIntent(dataSet[position].internalIpAddress, activity)
                    .let {
                        activity.startActivity(it)
                    }
            }
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

    }
}
