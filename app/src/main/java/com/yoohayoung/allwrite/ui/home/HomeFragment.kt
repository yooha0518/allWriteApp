package com.yoohayoung.allwrite.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yoohayoung.allwrite.LoginActivity
import com.yoohayoung.allwrite.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    interface ApiService {
        @GET("question/all")
        fun getQuestion(): Call<List<HomeFragment.QuestionResponse>>
    }

    data class QuestionResponse(
        val _id: String,
        val answerId: List<String>,
        val content: String,
        val date: String,
        val __v: Int
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        readQuestion();
        return root
    }

    private fun readQuestion(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.30.1.56:5000/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        Log.d("getQuestion","전체 질문 api를 요청합니다.")

        val call = apiService.getQuestion()
        call.enqueue(object : retrofit2.Callback<List<HomeFragment.QuestionResponse>> {
            override fun onResponse(
                call: Call<List<HomeFragment.QuestionResponse>>,
                response: Response<List<HomeFragment.QuestionResponse>>
            ) {
                if (response.isSuccessful) {
                    val questions = response.body()  // List<QuestionResponse> 형태로 받음
                    Log.d("questionRes", questions.toString())
                } else {
                    Log.e("getQuestionsError", "response is fail")
                }
            }

            override fun onFailure(call: Call<List<HomeFragment.QuestionResponse>>, t: Throwable) {
                Log.e("getQuestionsError", "Error: ${t.message}")
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}