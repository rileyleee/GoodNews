//package com.saveurlife.goodnews.chatting
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toolbar
//import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.saveurlife.goodnews.R
//import com.saveurlife.goodnews.databinding.FragmentGroupChattingBinding
//import com.saveurlife.goodnews.databinding.FragmentOneChattingBinding
//import com.saveurlife.goodnews.main.MainActivity
//
//class GroupChattingFragment : Fragment() {
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
//    ): View? {
//        var binding = FragmentGroupChattingBinding.inflate(inflater, container, false)
//
//        //그룹 생성 선택 시
//        binding.createGroup.setOnClickListener {
//            val navController = findNavController()
//            navController.navigate(R.id.chooseGroupMemberFragment)
//            (activity as? MainActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility = View.GONE
//            (activity as? MainActivity)?.findViewById<com.google.android.material.bottomappbar.BottomAppBar>(R.id.bottomAppBar)?.visibility = View.GONE
//            (activity as? MainActivity)?.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.mainCircleAddButton)?.visibility = View.GONE
//            (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.navigationView)?.visibility = View.GONE
//
//        }
//
////        val chatting = listOf(
////            OnechattingData("김싸피, 이싸피", "갈게", "오후 10:26", false, "safe"),
////            OnechattingData("이싸피, 김싸피", "안녕", "오후 1:04", false, "injury"),
////            OnechattingData("선싸피, 박싸피", "어디야", "오전 10:22", true,"death"),
////            OnechattingData("신싸피, 정싸피", "안녕하세요", "2023-11-07", false, "unknown"),
////            OnechattingData("박싸피, 민싸피", "조심해", "2023-11-06", true, "safe"),
////        )
////
////        val adapter = GroupChattingAdapter(chatting)
////        val recyclerView = binding.recyclerViewChatting
////        recyclerView.layoutManager = LinearLayoutManager(context)
////        recyclerView.adapter = adapter
//
//
//        return binding.root
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Toolbar 다시 표시
//        (activity as? MainActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility = View.VISIBLE
//        // BottomAppBar 및 FloatingActionButton 다시 표시
//        (activity as? MainActivity)?.findViewById<com.google.android.material.bottomappbar.BottomAppBar>(R.id.bottomAppBar)?.visibility = View.VISIBLE
//        (activity as? MainActivity)?.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.mainCircleAddButton)?.visibility = View.VISIBLE
//        (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.navigationView)?.visibility = View.VISIBLE
//    }
//}