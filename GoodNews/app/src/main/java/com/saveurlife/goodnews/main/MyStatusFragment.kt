package com.saveurlife.goodnews.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentMyStatusBinding
import com.saveurlife.goodnews.sync.SyncService

class MyStatusFragment : Fragment(), MyStatusDialogFragment.StatusSelectListener {
    private lateinit var binding: FragmentMyStatusBinding

    private lateinit var preferencesUtil: PreferencesUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userNameTextView.text = preferencesUtil.getString("name", "이름 없음")

        binding.layer.setOnClickListener {
            val dialogFragment = MyStatusDialogFragment()
            dialogFragment.show(childFragmentManager, "MyStatusDialogFragment")
        }

        binding.myGroup.setOnClickListener {
            (activity as? MainActivity)?.switchToChattingFragment(1)
        }
    }

    override fun onStatusSelected(status: String) {
        when (status) {
            "safe" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_safe_circle)
            "injury" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_injury_circle)
            "death" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_death_circle)
            "unknown" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_circle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyStatusBinding.inflate(inflater, container, false)
        preferencesUtil = PreferencesUtil(requireContext())

        val savedStatus = preferencesUtil.getString("status", "unknown")
        onStatusSelected(savedStatus)

        // 마지막 업데이트 시각
        val lastUpdateTime = preferencesUtil.getLong("SyncTime", 0L)
        val syncService = SyncService()
        if(lastUpdateTime == 0L){
            binding.myUpdateTime.text ="최초 업데이트 전"
        }else{
            binding.myUpdateTime.text = syncService.convertDateLongToString(lastUpdateTime)
        }

        // 이름 가져오기
        val userName = preferencesUtil.getString("name", "이름")
        binding.userNameTextView.text = userName

        return binding.root
    }

}