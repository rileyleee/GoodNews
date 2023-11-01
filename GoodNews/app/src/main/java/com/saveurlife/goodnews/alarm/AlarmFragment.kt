package com.saveurlife.goodnews.alarm

import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentAlarmBinding
import com.saveurlife.goodnews.databinding.FragmentMyPageBinding

class AlarmFragment : Fragment(), GestureDetector.OnGestureListener {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var gestureDetector: GestureDetector
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestureDetector = GestureDetector(context, this)

        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val swipeThreshold = 100
        val swipeDistance = e2?.x?.minus(e1?.x ?: 0f) ?: 0f

        if (swipeDistance > swipeThreshold) { // 오른쪽으로 스와이프
            requireActivity().supportFragmentManager.popBackStack()
            return true
        }
        return false
    }
}