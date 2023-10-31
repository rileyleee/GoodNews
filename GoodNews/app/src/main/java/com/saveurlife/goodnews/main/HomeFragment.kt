package com.saveurlife.goodnews.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.myStatusUpdateButtom.setOnClickListener {
//            val dialogFragment = MyStatusDialogFragment()
//            dialogFragment.show(childFragmentManager, "MyStatusDialogFragment")
//        }
//    }

//    override fun onStatusSelected(status: String) {
//        when (status) {
//            "safe" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_safe_circle)
//            "injury" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_injury_circle)
//            "death" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_death_circle)
//            "unknown" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_circle)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val myStatusFragment = MyStatusFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.containerMyStatus, myStatusFragment)
            .commit()

        // WifiFragment 추가
        val mainMeshFragment = MainMeshFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.containerMainMesh, mainMeshFragment)
            .commit()

        return binding.root
    }
}