package com.yoohayoung.allwrite

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Objects

class LoginActivity : AppCompatActivity() {

    interface ApiService {
        @POST("auth")  // 실제 서버의 엔드포인트 URL로 변경해야 합니다.
        fun loginUser(@Body requestBody: LoginRequestBody): Call<LoginResponse>
    }

    data class LoginRequestBody(
        val username: Editable,
        val password: Editable
    )

    data class LoginResponse(
        val token: TokenData
    )

    data class TokenData(
        val accessToken: String
        // refreshToken 또는 다른 필요한 토큰 데이터가 있다면 여기에 추가할 수 있습니다.
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 로그인 버튼과 회원가입 버튼을 레이아웃에서 찾아 변수에 할당합니다.
        val btn_login = findViewById<Button>(R.id.btn_login)
        val btn_goJoin = findViewById<Button>(R.id.btn_goJoin)

        // 로그인 버튼 클릭 리스너 설정
        btn_login.setOnClickListener {
            // Retrofit을 사용하여 서버에 로그인 요청을 보냅니다.
            Log.d("login","로그인을 요청합니다.")
            loginUserWithRetrofit()
        }

        // 회원가입 버튼 클릭 리스너 설정
        btn_goJoin.setOnClickListener {
            // JoinActivity로 이동합니다.
            val startJoinActivityIntent = Intent(this, JoinActivity::class.java)
            startActivity(startJoinActivityIntent)
        }
    }

    // Retrofit을 사용하여 로그인 요청을 처리하는 함수
    private fun loginUserWithRetrofit() {
        val et_id = findViewById<EditText>(R.id.et_id)
        val et_password = findViewById<EditText>(R.id.et_password)

        Log.d("login_id", et_id.text.toString());
        Log.d("login_password", et_password.text.toString());
        // 서버와 통신하기 위한 Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.30.1.56:5000/api/v1/")  // 실제 서버의 기본 URL로 교체해야 합니다.
            .addConverterFactory(GsonConverterFactory.create())  // Gson을 사용하여 JSON 데이터 변환
            .build()

        // ApiService 인터페이스의 구현체 생성
        val apiService = retrofit.create(ApiService::class.java)

        // 사용자 이름과 비밀번호 설정
        val username = et_id.text // 실제 사용자 이름으로 교체
        val password = et_password.text  // 실제 비밀번호로 교체

        // 로그인 요청을 위한 RequestBody 생성
        val requestBody = LoginRequestBody(username, password)

        // Retrofit을 사용하여 로그인 요청을 수행하고 응답을 처리
        val call = apiService.loginUser(requestBody)
        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // 서버로부터 응답이 성공적으로 도착한 경우
                    Log.d("login_res", response.toString());
                    val loginResponse = response.body()
                    val accessToken = loginResponse?.token?.accessToken ?: ""

                    Log.d("login_token",accessToken);

                    // 토큰 정보를 SharedPreferences에 저장 (보안을 위해 안전한 저장소에 저장하는 것이 좋습니다.)
                    val sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("accessToken", accessToken)
                    editor.apply()

                    // MainActivity로 이동
                    val startActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivityIntent.putExtra("isLogin", true)
                    startActivity(startActivityIntent)
                } else {
                    // 서버로부터 응답이 실패한 경우
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    Log.d("login res","로그인 실패");
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 요청이 실패한 경우
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("login Error","Error: ${t.message}",);
            }
        })
    }
}
