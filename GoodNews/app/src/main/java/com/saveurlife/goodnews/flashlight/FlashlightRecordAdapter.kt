package com.saveurlife.goodnews.flashlight

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.MorseCode
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.first
import okhttp3.internal.wait

class FlashlightRecordAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 아이템 뷰 타입 상수 => 가독성, 유지 관리성 향상
    companion object {
        const val TYPE_SELF = 1
        const val TYPE_OTHER = 2
        var recordData:MutableList<FlashlightData> = mutableListOf()
        lateinit var realm:Realm
    }
    // 뷰홀더
    // SELF 아이템 뷰 홀더
    class SelfViewHolder(val layout: View) : RecyclerView.ViewHolder(layout) {
        val flashSelfItem: TextView = layout.findViewById(R.id.flash_self_item)
        val btnSave:TextView = layout.findViewById(R.id.flash_add_item)


        // 등록시 실행하는 코드
        init {
            btnSave.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    realm = Realm.open(GoodNewsApplication.realmConfiguration)
                    val item = recordData[position]

                    FlashlightFragment.flashListAdapter.addSelfList(item.content)
                    // 원하는 동작 수행
                    Log.d("Flashlight", "버튼 클릭: ${item.content}")
                    val result = realm.query<MorseCode>().sort("id", Sort.DESCENDING).first().find()
                    // realm 등록
                    if(result == null){
                        realm.writeBlocking {
                            copyToRealm(
                                MorseCode().apply {
                                    id = 1
                                    text = item.content
                                }
                            )
                        }
                    }else{
                        realm.writeBlocking {
                            copyToRealm(
                                MorseCode().apply {
                                    this.id = result.id + 1
                                    this.text = item.content
                                }
                            )
                        }

                    }
                }
            }
        }

    }

    // OTHER 아이템 뷰 홀더
    class OtherViewHolder(val layout: View) : RecyclerView.ViewHolder(layout) {
        val flashOtherItem: TextView = layout.findViewById(R.id.flash_other_item)
    }


    override fun getItemViewType(position: Int): Int {
        return when (recordData[position].type) {
            FlashType.SELF -> TYPE_SELF
            FlashType.OTHER -> TYPE_OTHER
            else -> throw IllegalArgumentException("Unknown FlashType in getItemViewType")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SELF -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_self_flash, parent, false)
                SelfViewHolder(view)
            }

            TYPE_OTHER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_other_flash, parent, false)
                OtherViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = recordData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = recordData[position]
        when (item.type) {
            FlashType.SELF -> {
                val selfHolder = holder as SelfViewHolder
                selfHolder.flashSelfItem.text = item.content
            }

            FlashType.OTHER -> {
                val otherHolder = holder as OtherViewHolder
                otherHolder.flashOtherItem.text = item.content
            }

            else -> throw IllegalArgumentException("Unknown FlashType in onBindViewHolder")
        }
    }
    fun addSelfList(text: String){
        recordData.add(FlashlightData(FlashType.SELF,text))
        notifyItemInserted(recordData.size)
    }

    fun addOtherList(text: String){
        recordData.add(FlashlightData(FlashType.OTHER, text))
        notifyItemInserted(recordData.size)
    }
}