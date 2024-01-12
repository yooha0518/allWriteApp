package com.yoohayoung.allwrite

import JwtTokenManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class LoginActivity : AppCompatActivity() {

    interface ApiService {
        @POST("auth")
        fun loginUser(@Body requestBody: LoginRequestBody): Call<LoginResponse>
    }

    data class LoginRequestBody(
        @SerializedName("email") val email: String, //editable은 gson으로 직렬화할 수 없다.
        @SerializedName("password") val password: String //editable은 gson으로 직렬화할 수 없다.
    )

    data class LoginResponse(
        val token: TokenData
    )

    data class TokenData(
        val accessToken: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn_login = findViewById<Button>(R.id.btn_login)
        val btn_goJoin = findViewById<Button>(R.id.btn_goJoin)

        btn_login.setOnClickListener {
            Log.d("login", "로그인을 요청합니다.")
            loginUserWithRetrofit()
        }

        btn_goJoin.setOnClickListener {
            val startJoinActivityIntent = Intent(this, JoinActivity::class.java)
            startActivity(startJoinActivityIntent)
        }
    }

    private fun loginUserWithRetrofit() {
        val et_id = findViewById<EditText>(R.id.et_id)
        val et_password = findViewById<EditText>(R.id.et_password)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.30.1.56:5000/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val email = et_id.text.toString()
        val password = et_password.text.toString()

        val requestBody = LoginRequestBody(email, password)

        val call = apiService.loginUser(requestBody)
        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val accessToken = loginResponse?.token?.accessToken ?: ""

                    // JwtTokenManager를 사용하여 토큰 저장
                    val jwtTokenManager = JwtTokenManager(accessToken, this@LoginActivity)
                    jwtTokenManager.saveToken(accessToken)

                    val startActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivityIntent.putExtra("isLogin", true)
                    startActivity(startActivityIntent)
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    Log.d("login res", "로그인 실패")
                    Log.d("login Response Code", "Code: ${response.code()}")
                    response.errorBody()?.let {
                        try {
                            val errorBodyString = it.string()
                            Log.d("Error Response", errorBodyString)
                            Toast.makeText(this@LoginActivity, errorBodyString, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.e("Error Response", "Error reading error response", e)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("login Error", "Error: ${t.message}")
            }
        })
    }
}
