package com.example.myapplication

import com.example.myapplication.LedStateModel
import com.example.myapplication.R
import io.ktor.client.utils.EmptyContent.contentType
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.ManageFragmentBinding
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ManageFragment : Fragment(){
    private var _binding: ManageFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ManageFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }
    //Запрос состояния каждые 500 мс

    private fun repeatAPIcall(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while(isActive) {
                //do your network request here
                try {
                    val response = Network.httpClient.request {
                        url(Network.condition_url)
                        method = HttpMethod.Get
                        contentType(ContentType.Application.Json)
                    }
                    withContext(Dispatchers.Main) {
                        if (response.status == HttpStatusCode.OK) {
                            if (response.bodyAsText() == "0"){
                                binding.currStateOut.text = " потушено"
                                binding.toggle.isChecked = false
                            } else if (response.bodyAsText() == "1") {
                                binding.currStateOut.text = " первые 6"
                                binding.toggle.isChecked = true

                            }
                            else if (response.bodyAsText() == "2") {
                                binding.currStateOut.text = " вторые 6"
                                binding.toggle.isChecked = true
                            }
                            else if (response.bodyAsText() == "3") {
                                binding.currStateOut.text = " последние 4"
                                binding.toggle.isChecked = true
                            }
                            else {
                                binding.currStateOut.text = " зажжено всё"
                                binding.toggle.isChecked = true
                            }
                                //Toast.makeText(requireContext(), "Состояние обновлено ${response.bodyAsText()}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Не удается получить текущее состояние", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Ошибка в попытке получения состояния(сервер) ", Toast.LENGTH_SHORT).show()
                    }
                }
                delay(500)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repeatAPIcall = repeatAPIcall()
        repeatAPIcall.start()

        var i = 0;
        //Вывод логов из БД
        binding.getdataBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = Network.httpClient.request {
                        url(Network.logs_url)
                        method = HttpMethod.Get
                        contentType(ContentType.Application.Json)
                    }
                    withContext(Dispatchers.Main) {
                        if (response.status == HttpStatusCode.OK) {
                            binding.items.removeAllViews()
                            Toast.makeText(requireContext(), "10 последних записей выведены",Toast.LENGTH_SHORT).show()
                            val data: List<LedStateModel> = response.body()
                            val context = requireContext()
                            data.forEach {
                                val el = LayoutInflater.from(context).inflate(R.layout.list_item, null)
                                el.findViewById<TextView>(R.id.state_tv).text = it.action
                                el.findViewById<TextView>(R.id.date_tv).text = it.dataTime
                                binding.items.addView(el)
                            }

                        } else {
                            Toast.makeText(requireContext(), "Не удалось получить данные", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Ошибка в попытке получение логов (сервер)", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //Включение/выключение по кнопке
        binding.toggle.setOnClickListener {
            if(binding.toggle.isChecked){
                //Button is ON
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = Network.httpClient.request {
                            url(Network.comm4_url)
                            method = HttpMethod.Post
                            contentType(ContentType.Application.Json)
                        }
                        withContext(Dispatchers.Main) {
                            if (response.status == HttpStatusCode.OK) {
                                Toast.makeText(requireContext(), "Состояние передано (4)", Toast.LENGTH_SHORT).show()
                                binding.currStateOut.text = " зажжено всё"
                            } else {
                                Toast.makeText(requireContext(), "Не удалось передать состояние (4)", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) { Toast.makeText(requireContext(), "Ошибка в попытке передачи состояния led (сервер)", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                //btn is off
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = Network.httpClient.request {
                            url(Network.comm0_url)
                            method = HttpMethod.Post
                            contentType(ContentType.Application.Json)
                        }
                        withContext(Dispatchers.Main) {
                            if (response.status == HttpStatusCode.OK) {
                                Toast.makeText(requireContext(), "Состояние передано (0)", Toast.LENGTH_SHORT).show()
                                binding.currStateOut.text = " потушено"
                            } else {
                                Toast.makeText(requireContext(), "Не удалось передать состояние (0)", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Ошибка в попытке передачи состояния led (сервер)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        // состояния 1,2,3
        binding.state1Btn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = Network.httpClient.request {
                        url(Network.comm1_url)
                        method = HttpMethod.Post
                        contentType(ContentType.Application.Json)
                    }
                    withContext(Dispatchers.Main) {
                        if (response.status == HttpStatusCode.OK) {
                            Toast.makeText(requireContext(), "Состояние передано (1)", Toast.LENGTH_SHORT).show()
                            binding.toggle.isChecked = true
                            binding.currStateOut.text = " первые 6"
                        } else {
                            Toast.makeText(requireContext(), "Не удалось передать состояние (1)", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { Toast.makeText(requireContext(), "Ошибка в попытке передачи состояния led (сервер)", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.state2Btn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = Network.httpClient.request {
                        url(Network.comm2_url)
                        method = HttpMethod.Post
                        contentType(ContentType.Application.Json)
                    }
                    withContext(Dispatchers.Main) {
                        if (response.status == HttpStatusCode.OK) {
                            Toast.makeText(requireContext(), "Состояние передано (2)", Toast.LENGTH_SHORT).show()
                            binding.toggle.isChecked = true
                            binding.currStateOut.text = " вторые 6"
                        } else {
                            Toast.makeText(requireContext(), "Не удалось передать состояние (2)", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { Toast.makeText(requireContext(), "Ошибка в попытке передачи состояния led (сервер)", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.state3Btn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = Network.httpClient.request {
                        url(Network.comm3_url)
                        method = HttpMethod.Post
                        contentType(ContentType.Application.Json)
                    }
                    withContext(Dispatchers.Main) {
                        if (response.status == HttpStatusCode.OK) {
                            Toast.makeText(requireContext(), "Состояние передано (3)", Toast.LENGTH_SHORT).show()
                            binding.toggle.isChecked = true
                            binding.currStateOut.text = " последние 4"
                        } else {
                            Toast.makeText(requireContext(), "Не удалось передать состояние (3)", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { Toast.makeText(requireContext(), "Ошибка в попытке передачи состояния led (сервер)", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //Выход из аккаунта
        binding.signoutBtn.setOnClickListener{
            repeatAPIcall.cancel()
            binding.connStatus.setImageResource(R.drawable.conn_fail);
            binding.toggle.isClickable = false
            binding.getdataBtn.isClickable = false
            val pref = requireActivity().getSharedPreferences(
                "save_user_acc",
                Context.MODE_PRIVATE
            )
            var editor = pref.edit()
            editor.putInt("user",0)
            editor.apply()
            binding.signoutBtn.isClickable = false

        }
        binding.signUpAgain.setOnClickListener {
            repeatAPIcall.cancel()
            findNavController().navigate(R.id.action_manage_fragment_to_navigation_start)


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
