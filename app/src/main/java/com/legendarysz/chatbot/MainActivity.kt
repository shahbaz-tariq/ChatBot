package com.legendarysz.chatbot

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

//MainActivity.kt
class MainActivity : AppCompatActivity() {

    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var rvMain: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var ibSend: ImageButton
    private lateinit var anim: LottieAnimationView
    private lateinit var cv1: CardView
    private lateinit var cv2: CardView
    private lateinit var cv3: CardView
    private lateinit var cv4: CardView
    private lateinit var cv5: CardView

    val JSON: MediaType = "application/json;".toMediaType()
    private val client = OkHttpClient()

    //Insert Your Open API Key in this variable
    val OPENAI_API_KEY = "Your Open API Key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMain = findViewById<RecyclerView>(R.id.rvMain)
        etMessage = findViewById<EditText>(R.id.etMessage)
        ibSend = findViewById<ImageButton>(R.id.ibSend)
        anim = findViewById(R.id.animation_view)
        cv1 = findViewById(R.id.cv1)
        cv2 = findViewById(R.id.cv2)
        cv3 = findViewById(R.id.cv3)
        cv4 = findViewById(R.id.cv4)
        cv5 = findViewById(R.id.cv5)

        messageList = arrayListOf()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvMain.layoutManager = layoutManager

        messageAdapter = MessageAdapter(messageList)

        rvMain.adapter = messageAdapter

        //addToChat("What is Android?", Message.SENT_BY_ME)
        //addToChat("What is Android? What is android? What is Android? What is Android?", Message.SENT_BY_BOT)

        ibSend.setOnClickListener {
            val question = etMessage.text.toString()
            addToChat(question, Message.SENT_BY_ME)
            etMessage.text.clear()
            callApi(question)
            anim.isVisible = false
            cv1.isVisible = false
            cv2.isVisible = false
            cv3.isVisible = false
            cv4.isVisible = false
            cv5.isVisible = false

            // Hide the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etMessage.windowToken, 0)
        }
    }

    fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            rvMain.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    fun addResponse(response:String){
        messageList.removeAt(messageList.size-1)
        addToChat(response,Message.SENT_BY_BOT)
    }

    fun callApi(question: String) {

        addToChat("Typing...",Message.SENT_BY_BOT)

        val jsonObj = JSONObject()
        jsonObj.put("model", "gpt-3.5-turbo-instruct")
        jsonObj.put("prompt", question)
        jsonObj.put("max_tokens", 1000)
        jsonObj.put("temperature", 0)

        val body = jsonObj.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer $OPENAI_API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response due to "+e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful){
                    val jsonObject = JSONObject(response.body!!.string())
                    val jsonArray = jsonObject.getJSONArray("choices")
                    val result:String = jsonArray.getJSONObject(0).getString("text")
                    addResponse(result.trim())
                }else{
                    addResponse("Failed to load response due to "+ response.body!!.string())
                }
            }
        })
    }

}