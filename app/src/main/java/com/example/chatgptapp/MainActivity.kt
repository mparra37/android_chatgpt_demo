package com.example.chatgptapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    lateinit var etQuestion: EditText
    var query: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etQuestion = findViewById<EditText>(R.id.etQuestion)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val textResponse = findViewById<TextView>(R.id.txtResponse)

        btnSubmit.setOnClickListener{
            query = etQuestion.text.toString()
            //Toast.makeText(this, query, Toast.LENGTH_SHORT).show()

            getResponse(query){response ->
                runOnUiThread{
                    textResponse.setText(response)
                }
            }
            etQuestion.setText(" ")
        }
    }

    fun getResponse(question: String, callback: (String) -> Unit){
        val api_key = ""
        val url = "https://api.openai.com/v1/completions"
        query = etQuestion.text.toString()
        val requestBody = """
            {
              "model": "text-davinci-003", 
              "prompt": "$query",
              "max_tokens": 500,
              "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $api_key")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if(body != null){
                    Log.v("data", body)
                }else{
                    Log.v("data", "empty")
                }
                var jsonObject = JSONObject(body)
                val jsonArray : JSONArray = jsonObject.getJSONArray("choices")
                val textResult = jsonArray.getJSONObject(0).getString("text")
                //Toast.makeText(applicationContext, textResult, Toast.LENGTH_LONG).show()
                callback(textResult)
            }

        })
    }
}