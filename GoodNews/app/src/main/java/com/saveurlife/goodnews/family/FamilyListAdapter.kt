package com.saveurlife.goodnews.family

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.flashlight.FlashType
import com.saveurlife.goodnews.flashlight.FlashlightRecordAdapter
import io.realm.kotlin.Realm

class FamilyListAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var familyList: MutableList<FamilyData> = mutableListOf()

    companion object{
        const val TYPE_WAIT = 1
        const val TYPE_ACCEPT = 2
        lateinit var realm:Realm

    }
    
    // 뷰홀더 두개 필요
    class AcceptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.familyNameTextView)
        val statusView: View = view.findViewById(R.id.familyStatusCircle)
        val lastAccessTimeView: TextView = view.findViewById(R.id.familyLastAccessTime)
    }
    class WaitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.WaitNameTextView)
        val numberView: TextView = view.findViewById(R.id.WaitNumberTextView)
        val commentView: TextView = view.findViewById(R.id.WaitCommentTextView)
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
                WaitViewHolder(view)
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
                acceptViewHolder.lastAccessTimeView.text = item.lastAccessTime

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
                waitViewHolder.numberView.text = item.phoneNumber
                waitViewHolder.commentView.text = item.comment
            }
        }
    }

    fun addFamilyWait(name:String, phoneNumber: String, comment:String ){
        familyList.add(FamilyData(name,Status.NOT_SHOWN,"",FamilyType.WAIT, phoneNumber, comment))
        notifyItemInserted(familyList.size)
    }
    fun addFamilyInfo(name:String, status:Status, lastAccessTime : String){
        familyList.add(FamilyData(name, status, lastAccessTime, FamilyType.ACCEPT))
        notifyItemInserted(familyList.size)
    }
}