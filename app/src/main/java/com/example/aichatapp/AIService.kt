package com.example.aichatapp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import com.example.aichatapp.BuildConfig



object AIService {
    private val client = OkHttpClient()

    fun sendToAI(message: String, callback: (String) -> Unit) {
        val json = """{"prompt":"$message","max_tokens":100}"""
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions") // Gemini হলে URL পরিবর্তন
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")

            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("AI Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { resp ->
                    val aiText = JSONObject(resp)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getString("text")
                    callback(aiText)
                }
            }
        })
    }
}
