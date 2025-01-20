package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.LoginFragmentBinding
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment(){
    private var _binding: LoginFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var login : String
    private lateinit var password : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Авторизация
        binding.connServer.setOnClickListener {
            login = binding.inputUser.text.toString()
            password = binding.inputPass.text.toString()
            if (login.isNotEmpty() and password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {

                        val response = Network.httpClient.request {
                            url(Network.signIn_url)
                            parameter("login", login)
                            parameter("password", password)
                            method = HttpMethod.Post
                            contentType(ContentType.Application.Json)
                        }
                        withContext(Dispatchers.Main) {
                            if (response.status == HttpStatusCode.NotFound) {
                                Toast.makeText(requireContext(), "Необходимо зарегистрироваться", Toast.LENGTH_LONG).show()
                                binding.signUpTv.visibility = View.VISIBLE
                            } else if (response.status == HttpStatusCode.Found) {
                                Toast.makeText(requireContext(), "Авторизация прошла успешно", Toast.LENGTH_LONG).show()
                                findNavController().navigate(R.id.action_navigation_login_to_manageFragment)
                                binding.signUpTv.visibility=View.GONE
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Ошибка в попытке авторизации (сервер)", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else {
                Toast.makeText(requireContext(), "Введите имя пользователя и пароль", Toast.LENGTH_LONG).show()
            }



        }
        //Регистрация
        binding.signUpTv.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = Network.httpClient.request {
                        url(Network.signUp_url)
                        parameter("login", login)
                        parameter("password", password)
                        method = HttpMethod.Get
                        contentType(ContentType.Application.Json)
                    }
                    // Log.i("NETWORK_SIGNUP", response.status.toString())
                    withContext(Dispatchers.Main) {
                        if (response.status == HttpStatusCode.Accepted) {
                            Toast.makeText(requireContext(), "Регистрация прошла успешно", Toast.LENGTH_LONG).show()
                             val pref = requireActivity().getSharedPreferences(
                                        "save_user_acc",
                                        Context.MODE_PRIVATE
                                    )
                                    var editor = pref.edit()
                                    editor.putInt("user",1)
                                    editor.apply()
                        } else {
                            Toast.makeText(requireContext(), "Регистрация не удалась: ${response.status}", Toast.LENGTH_LONG).show()
                            binding.connServer.visibility = View.VISIBLE
                        }
                    }
                }
                catch (e:Exception){
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Ошибка в попытке регистрации (сервер)",Toast.LENGTH_LONG).show()
                    }
                }
            }

        }



    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
