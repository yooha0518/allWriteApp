package com.yoohayoung.allwrite.ui.mypage

import JwtTokenManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yoohayoung.allwrite.LoginActivity
import com.yoohayoung.allwrite.databinding.FragmentMypageBinding
import com.yoohayoung.allwrite.BuildConfig
import com.yoohayoung.allwrite.R


class MyPageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private lateinit var jwtTokenManager: JwtTokenManager // JwtTokenManager 인스턴스 생성

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myPageViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)

        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMypage
        myPageViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // JwtTokenManager 인스턴스 초기화
        jwtTokenManager = JwtTokenManager(BuildConfig.ACCESSSECRET, requireContext()) // 비밀 키 설정 필요

        //jwtTokenManager.decodeToken()

        // "btn_logout" 버튼 가져오기
        val btn_Logout: Button = binding.btnLogout
        // tv_name TextView 찾기
        val tv_name: TextView = binding.tvName // tvName은 XML에서 정의한 TextView의 ID입니다.

        // 토큰 해독 및 tv_name에 설정
        val payload = jwtTokenManager.decodeToken()
        val userName: String? = payload?.get("name") as? String

        if (userName != null) {
            // 가져온 userName 값을 사용하여 원하는 작업을 수행할 수 있습니다.
            // 예: TextView에 값을 설정하거나 로그로 출력하는 등의 작업
            Log.d("token","token: "+payload);
            tv_name.text = userName
        } else {
            // "name" 키가 payload에 없거나 값이 null일 경우 처리할 로직을 여기에 추가하세요.
            Log.d("token","token error: name키가 없거나 null입니다.");
        }

        // 버튼에 클릭 리스너 추가
        btn_Logout.setOnClickListener {
            // JwtTokenManager의 deleteToken 메서드 호출
            jwtTokenManager.deleteToken()

            // 로그인 화면으로 이동하거나 다른 필요한 작업을 수행합니다.
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()  // 현재 액티비티 종료
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
