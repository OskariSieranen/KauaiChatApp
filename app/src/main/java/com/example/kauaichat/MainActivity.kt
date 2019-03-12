package com.example.kauaichat
import android.content.Intent
import android.content.Context
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.support.v7.widget.RecyclerView
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.PrintWriter
import java.net.Socket
import java.util.*


const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    lateinit var socket: Socket
    val messages = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyRecyclerViewAdapter(this, messages)
            Thread(Runnable {
                socket = Socket("10.0.2.2", 55555)
                Log.d(TAG, "Socket Got")
                val scanner = Scanner(socket.getInputStream())
                Log.d(TAG, "Scanner Got")

                while (true) {
                    val read = scanner.nextLine()
                    messages.add(read)
                    runOnUiThread {
                        (recyclerView.adapter as MyRecyclerViewAdapter).notifyDataSetChanged()
                        (recyclerView.layoutManager!!.smoothScrollToPosition(recyclerView, null, messages.size))}
                }
            }).start()
            Log.d(TAG, "Create")
        Log.d(TAG, "HERE")
        button.setOnClickListener {
            Thread(Runnable {
                val editMessage = findViewById<EditText>(R.id.MessageText)
                val message = editMessage.text.toString()
                Log.d(TAG, "Trying to send message?")
                val output = PrintWriter(socket.getOutputStream(), true)
                output.println(message)
                runOnUiThread{MessageText.text.clear()}
            }).start()
        }


    }

    class MyRecyclerViewAdapter(private val context: Context, private val mydata: List<String>) :
        RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(vg: ViewGroup, vt: Int): MyViewHolder {
            Log.d("ZZZ", "onCreateViewHolder()")
            val itemView = LayoutInflater.from(context).inflate(R.layout.myitemlayout, vg, false)
            return MyViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return mydata.size
        }

        override fun onBindViewHolder(vh: MyViewHolder, pos: Int) {
            Log.d("ZZZ", "onBindViewHolder($pos)")
            vh.itemView.findViewById<TextView>(R.id.textView2).text = mydata[pos]
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
    }
}

