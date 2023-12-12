package com.saveurlife.goodnews.family

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class FamilyListAdapter(private val listener: OnItemClickListener ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var familyList: MutableList<FamilyData> = mutableListOf()

    companion object{
        const val TYPE_WAIT = 1
        const val TYPE_ACCEPT = 2
    }


    // 뷰홀더 두개 필요
    // 이미 등록된 가족
    class AcceptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.familyNameTextView)
        val statusView: View = view.findViewById(R.id.familyStatusCircle)
        val lastAccessTimeView: TextView = view.findViewById(R.id.familyLastAccessTime)
    }

    interface OnItemClickListener {
        fun onAcceptButtonClick(position: Int)
        fun onRejectButtonClick(position: Int)

    }
    
    // 가족 수락을 위함
    class WaitViewHolder(view: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.WaitNameTextView)
        private val acceptBtn: TextView = view.findViewById(R.id.acceptButton)
        private val rejectBtn: TextView = view.findViewById(R.id.rejectButton)

        init {
            acceptBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    // 서버 요청
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAcceptButtonClick(position)
                    }
                }
            }
            rejectBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRejectButtonClick(position)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (familyList[position].type) {
            FamilyType.ACCEPT -> TYPE_ACCEPT
            FamilyType.WAIT -> TYPE_WAIT
            else -> throw IllegalArgumentException("Unknown FamilyType in getItemViewType")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ACCEPT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_family, parent, false)
                AcceptViewHolder(view)
            }
            TYPE_WAIT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_family_wait, parent, false)
                WaitViewHolder(view, listener)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return familyList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = familyList[position]


        when (item.type) {
            FamilyType.ACCEPT -> {
                val acceptViewHolder = holder as AcceptViewHolder

                acceptViewHolder.nameView.text = item.name
                acceptViewHolder.lastAccessTimeView.text = item.lastAccessTime.toString()

                when (item.status) {
                    Status.HEALTHY -> {
                        acceptViewHolder.statusView.backgroundTintList =
                            ContextCompat.getColorStateList(acceptViewHolder.itemView.context, R.color.safe)
                    }

                    Status.INJURED -> {
                        acceptViewHolder.statusView.backgroundTintList =
                            ContextCompat.getColorStateList(acceptViewHolder.itemView.context, R.color.caution)
                    }

                    Status.DECEASED -> {
                        acceptViewHolder.statusView.backgroundTintList =
                            ContextCompat.getColorStateList(acceptViewHolder.itemView.context, R.color.black)
                    }

                    else -> {
                        acceptViewHolder.statusView.backgroundTintList = null
                    }
                }
            }

            FamilyType.WAIT -> {
                val waitViewHolder = holder as WaitViewHolder
                waitViewHolder.nameView.text = item.name
            }
        }
    }

    fun addFamilyWait(name:String, acceptNumber:Int){
        familyList.add(FamilyData(name,Status.NOT_SHOWN,"" ,FamilyType.WAIT, acceptNumber))
    }
    fun addFamilyInfo(name:String, status: Status, lastAccessTime: String){
        familyList.add(FamilyData(name, status, lastAccessTime, FamilyType.ACCEPT))
    }

    fun resetFamilyList(){
        familyList = mutableListOf()
    }

}
