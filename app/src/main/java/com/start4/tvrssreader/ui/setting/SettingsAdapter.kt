import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView

sealed class SettingItem {
    data class Toggle(val id: Int, val title: String, var isEnabled: Boolean) : SettingItem()
    data class Action(val id: Int, val title: String, val subTitle: String) : SettingItem()
}

class SettingsAdapter(
    private val items: List<SettingItem>,
    private val onToggle: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val tv = TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            textSize = 20f
            setPadding(48, 24, 48, 24)
            setTextColor("#E2E2E2".toColorInt())

            // 焦点反馈
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
        }
        return object : RecyclerView.ViewHolder(tv) {}
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val tv = holder.itemView as TextView

        when (item) {
            is SettingItem.Toggle -> {
                val status = if (item.isEnabled) "开" else "关"
                tv.text = "${item.title}: $status"
                tv.setOnClickListener {
                    item.isEnabled = !item.isEnabled
                    onToggle(item.id, item.isEnabled)
                    notifyItemChanged(position) // 局部刷新
                }
            }

            is SettingItem.Action -> {
                tv.text = "${item.title}\n${item.subTitle}"
                tv.textSize = 18f
            }
        }
    }

    override fun getItemCount() = items.size
}