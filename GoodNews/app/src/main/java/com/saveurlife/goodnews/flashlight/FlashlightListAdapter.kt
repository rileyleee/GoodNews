package com.saveurlife.goodnews.flashlight

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class FlashlightListAdapter() :
    RecyclerView.Adapter<FlashlightListAdapter.ListAdapter>() {

    var saveData:MutableList<FlashlightData> = mutableListOf()

    // isFlashing boolean
    var isFlashing = false

    // 클릭 리스너
    interface OnItemClickListener {
        fun onItemClick(data: FlashlightData, position: Int)
    }

    // 리스너 객체 참조 저장
    private var listener: OnItemClickListener? = null

    // 리스너 객체 참조 => 어댑터에 전달
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ListAdapter(val layout: View) : RecyclerView.ViewHolder(layout) {
        val flashLightTextBox: TextView = layout.findViewById(R.id.flashLightTextBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flash, parent, false)
        return ListAdapter(view)
    }

    override fun onBindViewHolder(holder: ListAdapter, position: Int) {
        val data = saveData[position]
        holder.flashLightTextBox.text = saveData[position].content

        // 아이템 클릭 시 리스너 onItemClick 메서드 호출
        holder.itemView.setOnClickListener {
            listener?.onItemClick(data, position)
        }

        // 첫 번째 아이템 시작 여백 주기
        with(holder.itemView.layoutParams as RecyclerView.LayoutParams) {
            leftMargin = when (position) {
                0 -> TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    20f,
                    holder.itemView.context.resources.displayMetrics
                ).toInt()

                else -> 0
            }
        }
    }

    override fun getItemCount(): Int {
        return saveData.size
    }

    fun addSelfList(text: String){
        saveData.add(FlashlightData(FlashType.SELF,text))
        notifyItemInserted(saveData.size)
    }

    fun addOtherList(text: String){
        saveData.add(FlashlightData(FlashType.OTHER, text))
        notifyItemInserted(saveData.size)
    }

}