    package com.saveurlife.goodnews.alert

    import android.annotation.SuppressLint
    import android.content.ClipData.Item
    import android.content.Intent
    import android.icu.text.SimpleDateFormat
    import android.icu.util.Calendar
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.recyclerview.widget.RecyclerView
    import com.saveurlife.goodnews.alert.TimeUtils.dateTimeToMillSec
    import com.saveurlife.goodnews.chatting.ChattingDetailActivity
    import com.saveurlife.goodnews.databinding.ItemAlertFamilyBinding
    import com.saveurlife.goodnews.databinding.ItemAlertFamilyLocationBinding
    import com.saveurlife.goodnews.databinding.ItemAlertHelpBinding
    import com.saveurlife.goodnews.family.FamilyFragment
    import com.saveurlife.goodnews.main.MainActivity
    import com.saveurlife.goodnews.models.Alert
    import com.saveurlife.goodnews.sync.SyncService
    import com.saveurlife.goodnews.sync.TimeService
    import java.text.ParseException
    import java.util.Locale
    import java.util.concurrent.TimeUnit

    object TimeUtils {
        @SuppressLint("SimpleDateFormat")
        fun dateTimeToMillSec(dateTime: String): Long {
            var timeInMilliseconds: Long = 0
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                val mDate = sdf.parse(dateTime)
                timeInMilliseconds = mDate.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return timeInMilliseconds
        }

        fun calculationTime(createDateTime: Long): String{
            val nowDateTime = Calendar.getInstance().timeInMillis //현재 시간 to millisecond
            var value = ""
            val differenceValue = nowDateTime - createDateTime //현재 시간 - 비교가 될 시간
            Log.i("createDateTime", createDateTime.toString())
            Log.i("nowDateTime", nowDateTime.toString())
            Log.i("differenceValue", differenceValue.toString())
            when {
                differenceValue < 60000 -> { //59초 보다 적다면
                    value = "방금 전"
                }
                differenceValue < 3600000 -> { //59분 보다 적다면
                    value =  TimeUnit.MILLISECONDS.toMinutes(differenceValue).toString() + "분 전"
                }
                differenceValue < 86400000 -> { //23시간 보다 적다면
                    value =  TimeUnit.MILLISECONDS.toHours(differenceValue).toString() + "시간 전"
                }
                differenceValue <  604800000 -> { //7일 보다 적다면
                    value =  TimeUnit.MILLISECONDS.toDays(differenceValue).toString() + "일 전"
                }
                differenceValue < 2419200000 -> { //3주 보다 적다면
                    value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/7).toString() + "주 전"
                }
                differenceValue < 31556952000 -> { //12개월 보다 적다면
                    value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/30).toString() + "개월 전"
                }
                else -> { //그 외
                    value =  (TimeUnit.MILLISECONDS.toDays(differenceValue)/365).toString() + "년 전"
                }
            }
            return value
        }
    }


    class AlertAdapter(private var alertList: List<Alert>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        // 다른 타입들에 대한 상수 정의
        companion object {
            private const val TYPE_SAVE = 1
            private const val TYPE_FAMILY = 2
            private const val TYPE_FAMILY_PLACE = 3
            private const val TYPE_DEFAULT = 4
        }

        // 클릭 리스너 인터페이스 정의
        interface OnItemClickListener {
            fun onItemClick(chatData: Alert)
        }

        var listener: OnItemClickListener? = null

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)

            return when (viewType){
                TYPE_SAVE -> SaveViewHolder(ItemAlertHelpBinding.inflate(inflater, parent, false));
                TYPE_FAMILY -> FamilyViewHolder(ItemAlertFamilyBinding.inflate(inflater, parent, false));
                TYPE_FAMILY_PLACE -> FamilyPlaceViewHolder(ItemAlertFamilyLocationBinding.inflate(inflater, parent, false));
                else -> ViewHolder(ItemAlertHelpBinding.inflate(inflater, parent, false));
            }
    //        val binding = ItemAlarmHelpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    //        return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var alert = alertList[position]

            when(holder){
                is SaveViewHolder -> holder.bindSave(alert)
                is FamilyViewHolder -> holder.bindFamily(alert)
                is FamilyPlaceViewHolder -> holder.bindFamilyPlace(alert)
                else -> (holder as ViewHolder).bind(alert)
            }
    //        holder.bind(alert)

            holder.itemView.setOnClickListener {
                listener?.onItemClick(alert)
            }
        }

        override fun getItemCount(): Int {
            return alertList.size
        }

        override fun getItemViewType(position: Int): Int {
            val alert = alertList[position]

            return when(alert.type){
                "구조" -> TYPE_SAVE
                "가족" -> TYPE_FAMILY
                "가족끊김" -> TYPE_FAMILY
                "장소" -> TYPE_FAMILY_PLACE
                else -> TYPE_DEFAULT
            }
    //        return super.getItemViewType(position)
        }


        class ViewHolder(private val binding: ItemAlertHelpBinding) : RecyclerView.ViewHolder(binding.root){
            private val timeService: TimeService = TimeService()
            fun bind(alert: Alert){
                binding.alertHelpText.text = alert.name + "님이 도움을 요청했습니다."
                binding.alertHelpTime.text = timeService.realmInstantToString(alert.time)

                binding.alertHelpMessage.setOnClickListener {
                    // 특정 위치를 클릭할 때의 동작을 여기에 추가
                    val intent = Intent(binding.root.context, ChattingDetailActivity::class.java).apply {
                        putExtra("chatRoomId", alert.senderId)  // 여기서 userId 등을 적절한 필드로 변경해야 합니다.
                        putExtra("chatName", alert.name)

                        when (alert.content) {
                            "멀쩡함" -> putExtra("chatOtherStatus", "safe")
                            "부상" -> putExtra("chatOtherStatus", "injury")
                            "?" -> putExtra("chatOtherStatus", "death")
                            "?" -> putExtra("chatOtherStatus", "unknown")
                        }
                        putExtra("page", 2)
                    }
                    binding.root.context.startActivity(intent)
                }

                binding.alertHelpLocation.setOnClickListener {
                    Toast.makeText(binding.root.context, "위도 경도는 ${alert.latitude}, ${alert.longitude}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        class SaveViewHolder(private val binding: ItemAlertHelpBinding) : RecyclerView.ViewHolder(binding.root){
            private val timeService: TimeService = TimeService()

            fun bindSave(alert: Alert) {
                binding.alertHelpText.text = "${alert.name}님이 도움을 요청했습니다."
    //            binding.alarmHelpTime.text = syncService.realmInstantToString(alert.time)
                binding.alertHelpTime.text = TimeUtils.calculationTime(dateTimeToMillSec(timeService.realmInstantToString(alert.time)))
                Log.i("syncService", timeService.realmInstantToString(alert.time)) //2024-02-01 00:34:42
                Log.i("테스트", TimeUtils.calculationTime(dateTimeToMillSec(timeService.realmInstantToString(alert.time))))

                binding.alertHelpMessage.setOnClickListener {
                    // 특정 위치를 클릭할 때의 동작을 여기에 추가
                    val intent = Intent(binding.root.context, ChattingDetailActivity::class.java).apply {
                        putExtra("chatRoomId", alert.senderId)  // 여기서 userId 등을 적절한 필드로 변경해야 합니다.
                        putExtra("chatName", alert.name)

                        when (alert.content) {
                            "멀쩡함" -> putExtra("chatOtherStatus", "safe")
                            "부상" -> putExtra("chatOtherStatus", "injury")
                            "?" -> putExtra("chatOtherStatus", "death")
                            "?" -> putExtra("chatOtherStatus", "unknown")
                        }
                        putExtra("page", 2)
                    }
                    binding.root.context.startActivity(intent)
                }

                binding.alertHelpLocation.setOnClickListener {
                    Toast.makeText(binding.root.context, "위도 경도는 ${alert.latitude}, ${alert.longitude}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        class FamilyViewHolder(private val binding: ItemAlertFamilyBinding) : RecyclerView.ViewHolder(binding.root){
            private val timeService: TimeService = TimeService()

            fun bindFamily(alert: Alert) {
                if(alert.type == "가족"){
                    binding.alertFamilyText.text = "가족 ${alert.name}님과 연결되었습니다."
                    binding.alertFamilyTime.text = TimeUtils.calculationTime(dateTimeToMillSec(timeService.realmInstantToString(alert.time)))
                    binding.alertFamilyMessage.visibility  = View.VISIBLE
                    binding.alertFamilyLocation.visibility  = View.VISIBLE

                    Log.i("syncService11", timeService.realmInstantToString(alert.time)) //2024-02-01 00:34:42
                    Log.i("테스트11", TimeUtils.calculationTime(dateTimeToMillSec(timeService.realmInstantToString(alert.time))))

                    binding.alertFamilyMessage.setOnClickListener {
                        // 특정 위치를 클릭할 때의 동작을 여기에 추가
                        val intent = Intent(binding.root.context, ChattingDetailActivity::class.java).apply {
                            putExtra("chatRoomId", alert.senderId)  // 여기서 userId 등을 적절한 필드로 변경해야 합니다.
                            putExtra("chatName", alert.name)

                            when (alert.content) {
                                "멀쩡함" -> putExtra("chatOtherStatus", "safe")
                                "부상" -> putExtra("chatOtherStatus", "injury")
                                "?" -> putExtra("chatOtherStatus", "death")
                                "?" -> putExtra("chatOtherStatus", "unknown")
                            }
                            putExtra("page", 2)
                        }
                        binding.root.context.startActivity(intent)
                    }

                    binding.alertFamilyLocation.setOnClickListener {
                        Toast.makeText(binding.root.context, "위도 경도는 ${alert.latitude}, ${alert.longitude}", Toast.LENGTH_SHORT).show()
                    }

                }else if(alert.type == "가족끊김"){
                    binding.alertFamilyText.text = "가족 ${alert.name}님과 연결이 끊겼습니다."
                    binding.alertFamilyTime.text = TimeUtils.calculationTime(dateTimeToMillSec(timeService.realmInstantToString(alert.time)))
                    binding.alertFamilyMessage.visibility  = View.GONE
                    binding.alertFamilyLocation.visibility  = View.GONE
                }
            }
        }

        class FamilyPlaceViewHolder(private val binding: ItemAlertFamilyLocationBinding) : RecyclerView.ViewHolder(binding.root){
            private val timeService: TimeService = TimeService()

            fun bindFamilyPlace(alert: Alert) {
                binding.alarmFamilyLocationText.text = "${alert.name}님이 모임 장소를 변경했습니다."
                binding.alarmFamilyLocationTime.text = TimeUtils.calculationTime(dateTimeToMillSec(timeService.realmInstantToString(alert.time)))



                binding.familyLocationFragment.setOnClickListener {
                    // 특정 위치를 클릭할 때의 동작을 여기에 추가
                    val intent = Intent(binding.root.context, MainActivity::class.java)
                    intent.action = "showFamilyFragmentByAlert"
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }