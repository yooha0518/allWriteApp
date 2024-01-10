package com.yoohayoung.allwrite

import JwtTokenManager
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.yoohayoung.allwrite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 저장된 토큰을 가져와 검증
        val jwtTokenManager = JwtTokenManager(BuildConfig.ACCESSSECRET, this) // 여기에 실제 비밀 키가 필요하면 추가
        if (!jwtTokenManager.verifyToken()) {
            // 토큰 검증 실패: LoginActivity를 시작하여 로그인을 유도
            val startLoginActivityIntent = Intent(this, LoginActivity::class.java)
            startActivity(startLoginActivityIntent)
            finish() // 현재 액티비티 종료
            return
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_mypage, R.id.navigation_friend
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
