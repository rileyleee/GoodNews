package com.saveurlife.goodnews.tutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class TutorialPageAdapter (
    //튜토리얼 데이터
    private val pages: List<TutorialData>,
    //마지막 페이지인지 확인하는 변수
    private var isLastPage: Boolean,
    private var currentPage: Int = 0
) : RecyclerView.Adapter<TutorialPageAdapter.PageViewHolder>(){

    private var onItemClickListener: ((Int) -> Unit)? = null

//    fun setOnItemClickListener(listener: (Int) -> Unit) {
//        onItemClickListener = listener
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tutorial_item, parent, false)
        return PageViewHolder(itemView)
    }

    //튜토리얼 페이지 수
    override fun getItemCount(): Int = pages.size

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])

        currentPage = position


        //마지막 페이지인지의 유무에 따라 시작하기 버튼 활성화 설정
        //건너뛰기 버튼
        val tutorialSkip = holder.itemView.findViewById<TextView>(R.id.tutorial_skip)
        //시작하기 버튼
        val tutorialEnd = holder.itemView.findViewById<TextView>(R.id.tutorial_end)

        //마지막 페이지인 경우
        if(isLastPage && position == pages.size-1){
            //건너뛰기 비활성화
            tutorialSkip.visibility = View.INVISIBLE
            //시작하기 활성화
            tutorialEnd.visibility = View.VISIBLE
        }else{
            //건너뛰기 활성화
            tutorialSkip.visibility = View.VISIBLE
            //시작하기 비활성화
            tutorialEnd.visibility = View.INVISIBLE
        }

        // 클릭 이벤트 발생 시 TutorialActivity의 메서드를 호출하여 추가정보 입력 Activity로 이동
        tutorialEnd.setOnClickListener {
            (holder.itemView.context as? TutorialActivity)?.navigateToOtherActivity()
        }

        // 클릭 이벤트 발생 시 TutorialActivity의 메서드를 호출하여 추가정보 입력 Activity로 이동
        tutorialSkip.setOnClickListener {
            (holder.itemView.context as? TutorialActivity)?.navigateToOtherActivity()
        }

        // 카드를 클릭했을 때 리스너 호출
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
    }


    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pageTitleView: TextView = itemView.findViewById(R.id.tutorial_title)
        private val pageDetailView: TextView = itemView.findViewById(R.id.tutorial_detail)
        private val pageImageView: ImageView = itemView.findViewById(R.id.tutorial_img)

        fun bind(page: TutorialData) {
            pageTitleView.text = page.title
            pageDetailView.text = page.detail
            pageImageView.setImageResource(page.img)
        }
    }
}