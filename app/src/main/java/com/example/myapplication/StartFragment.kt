package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.StartFragmentBinding

class StartFragment : Fragment(){
    private var _binding: StartFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var s : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = StartFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setupConn.setOnClickListener {
            val pref = requireActivity().getSharedPreferences(
                "save_user_acc",
                Context.MODE_PRIVATE
            )
            var s = pref.getInt("user",0)
            //Toast.makeText(requireContext(), "s =  $s", Toast.LENGTH_SHORT).show()
            if (s == 1){
                findNavController().navigate(R.id.action_navigation_start_to_manage_fragment)
            }
            else{
            findNavController().navigate(R.id.action_navigation_start_to_navigation_login)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
