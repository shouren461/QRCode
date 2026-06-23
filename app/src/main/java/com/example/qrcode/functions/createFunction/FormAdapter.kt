package com.example.qrcode.functions.createFunction
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import com.example.qrcode.R
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcode.functions.createFunction.input.FieldConfig
import com.example.qrcode.functions.createFunction.input.FieldType

//动态表单适配器，根据Strategy提供的配置信息，实时渲染输入框
class FormAdapter(private val fields: List<FieldConfig>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // 实时存储用户在输入框中填写的值,存储文本和开关的值
    val values = mutableMapOf<String, String>()
    val switches = mutableMapOf<String, Boolean>()
    //根据字段配置中的类型(Enum),返回对应的布局序号为ViewType
    override fun getItemViewType(position: Int): Int  = fields[position].type.ordinal
    //ViewHolders:管理不同布局的视图引用
    class InputViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLabelInput: TextView = view.findViewById(R.id.tv_field_label_input)
        val ivIcon: ImageView = view.findViewById(R.id.iv_icon)
        val etInput: EditText = view.findViewById(R.id.et_input)
    }
    class InputNoIconViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLabelInput: TextView = view.findViewById(R.id.tv_field_label_input_noIcon)
        val etInput: EditText = view.findViewById(R.id.et_input_noIcon)
    }
    class MultilineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val etInput: EditText = view.findViewById(R.id.etInput)
        val tvLabelMulti: TextView = view.findViewById(R.id.tv_field_label_multiline)
    }
    class CounterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val etInput: EditText = view.findViewById(R.id.etInput)
        val tvCount: TextView = view.findViewById(R.id.tv_count)
    }
    class DropdownViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.iv_icon)
        val tvValue: TextView = view.findViewById(R.id.tv_value)
    }
    class SwitchViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvLabel: TextView = view.findViewById(R.id.tv_label)
        val switchHolder: SwitchCompat = view.findViewById(R.id.switch_holder)
    }
    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val ivIconTime: ImageView = view.findViewById(R.id.iv_icon_time)
        val tvLabel: TextView = view.findViewById(R.id.tv_label)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
    }
    class DateTimeViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val tvLabelNoIcon: TextView = view.findViewById(R.id.tv_label_datetime_no_Icon)
        val tvDateTime : TextView = view.findViewById(R.id.tv_datetime_no_icon)
    }
    class QuickInputViewHolder(view: View): RecyclerView.ViewHolder(view){
        val ivIconQuick: ImageView = view.findViewById(R.id.iv_icon_quick)
        val etInputQuick: EditText = view.findViewById(R.id.et_input_quick)
        val tvPrefix: TextView = view.findViewById(R.id.tv_quick_prefix)
        val tvSuffix: TextView  = view.findViewById(R.id.tv_quick_suffix)
    }
    class QuickInputNoIconViewHolder(view: View): RecyclerView.ViewHolder(view){
        val etInputQuickNoIcon: EditText = view.findViewById(R.id.et_input_quick_no_icon)
        val tvPrefixNoIcon: TextView = view.findViewById(R.id.tv_quick_prefix_no_icon)
        val tvSuffixNoIcon: TextView  = view.findViewById(R.id.tv_quick_suffix_no_icon)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
       val inflater = LayoutInflater.from(parent.context)
        //根据不同的个ViewType加载不同的xml布局文件并绑定到对应的ViewHolder上
        return when(viewType){
            FieldType.INPUT_NO_ICON.ordinal -> InputNoIconViewHolder(inflater.inflate(R.layout.item_form_input_no_icon,parent,false))
            FieldType.MULTILINE.ordinal -> MultilineViewHolder(inflater.inflate(R.layout.item_form_multiple_lines,parent,false))
            FieldType.TEXT_COUNTER.ordinal -> CounterViewHolder(inflater.inflate(R.layout.item_form_text_counter,parent,false))
            FieldType.DROPDOWN.ordinal -> DropdownViewHolder(inflater.inflate(R.layout.item_form_dropdown,parent,false))
            FieldType.SWITCH.ordinal -> SwitchViewHolder(inflater.inflate(R.layout.item_form_switch,parent,false))
            FieldType.DATE.ordinal -> DateViewHolder(inflater.inflate(R.layout.item_form_date,parent,false))
            FieldType.DATETIME.ordinal -> DateTimeViewHolder(inflater.inflate(R.layout.item_form_datetime,parent,false))
            FieldType.QUICK_INPUT.ordinal -> QuickInputViewHolder(inflater.inflate(R.layout.item_form_quick_input,parent,false))
            FieldType.QUICK_INPUT_NO_ICON.ordinal -> QuickInputNoIconViewHolder(inflater.inflate(R.layout.item_form_quick_input_no_icon,parent,false))
            else -> InputViewHolder(inflater.inflate(R.layout.item_form_input,parent,false))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = fields[position]
        //使用when表达式针对不同的ViewHolder进行数据绑定和交互监听
        when(holder){
            is InputViewHolder ->{
                bindLabel(holder.tvLabelInput,item.label)
                holder.ivIcon.setImageResource(item.iconRes)
                holder.etInput.hint  =item.hint
                //绑定通用的文字变化监听器
                bindTextWatcher(holder.etInput,item.key)
            }
            is InputNoIconViewHolder ->{
                bindLabel(holder.tvLabelInput,item.label)
                holder.etInput.hint  =item.hint
                //绑定通用的文字变化监听器
                bindTextWatcher(holder.etInput,item.key)
            }
            is MultilineViewHolder ->{
                bindLabel(holder.tvLabelMulti,item.label)
                holder.etInput.hint = item.hint
                bindTextWatcher(holder.etInput,item.key)
            }
            is CounterViewHolder ->{
                holder.etInput.hint = item.hint
                holder.tvCount.text = (values[item.key] ?. length ?: 0).toString()
                    //专属监听，除了保存数据还要更新右下角的计数值
                holder.etInput.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val len = s ?.length ?: 0
                        holder.tvCount.text = len.toString()
                        values[item.key] = s.toString()
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
            is DropdownViewHolder ->{
                holder.ivIcon.setImageResource(item.iconRes)
                holder.tvValue.text =values[item.key] ?: item.defaultValue
                //点击弹出列表对话框进行选择
                holder.itemView.setOnClickListener {
                    showListDialog(holder.itemView.context,item){selected ->
                        values[item.key] = selected
                        //动态获取当前项位置
                        val currentPosition = holder.bindingAdapterPosition
                        if (currentPosition != RecyclerView.NO_POSITION){//当前选项存在的刷新列表
                            notifyItemChanged(currentPosition) //选中后刷新当前项显示
                        }
                    }
                }
            }
            is SwitchViewHolder ->{
                holder.tvLabel.text = item.label
                //初始化开关状态
                holder.switchHolder.isChecked = switches[item.key] ?: (item.defaultValue == "true")
                //监听开关并切换保存到map
                holder.switchHolder.setOnCheckedChangeListener {_,isChecked ->
                    switches[item.key] = isChecked
                }
            }

            is DateViewHolder ->{
                holder.ivIconTime.setImageResource(item.iconRes)
                holder.tvLabel.text = item.label
                holder.tvTime.text = values[item.key] ?: item.defaultValue
                //点击触发系统日期选择器
                holder.itemView.setOnClickListener {
                 //仅弹出日期选择器
                        showDatePicker(holder.itemView.context){date ->
                            values[item.key]  = date
                            //动态获取当前项位置
                            val currentPosition = holder.bindingAdapterPosition
                            if (currentPosition != RecyclerView.NO_POSITION){//当前选项存在的刷新列表
                                notifyItemChanged(currentPosition) //选中后刷新当前项显示
                            }
                        }
                }
            }

            is DateTimeViewHolder -> {
              holder.tvLabelNoIcon.text = item.label
                holder.tvDateTime.text = values[item.key] ?: item.defaultValue
                //点击触发系统日期选择器
                holder.itemView.setOnClickListener {
                    if (item.type == FieldType.DATETIME){ //弹出日期时间选择器
                        showDateTimePicker(holder.itemView.context){dateTime->
                            values[item.key] = dateTime
                            val currentPosition = holder.bindingAdapterPosition
                            if (currentPosition != RecyclerView.NO_POSITION){
                                notifyItemChanged(currentPosition)
                            }
                        }
                    }
                }
            }

            //添加前缀后缀方案
            is QuickInputViewHolder ->{
                holder.ivIconQuick.setImageResource(item.iconRes)
                holder.etInputQuick.hint = item.hint
                bindTextWatcher(holder.etInputQuick,item.key)

                //设置前缀点击事项
                holder.tvPrefix.setOnClickListener {
                    val latestText  =holder.etInputQuick.text.toString() //获取最新的编辑框字符
                    val prefix = holder.tvPrefix.text.toString()
                    if (!latestText.startsWith(prefix)){
                        holder.etInputQuick.setText(prefix + latestText)
                        holder.etInputQuick.setSelection(holder.etInputQuick.text.length)
                    }
                }
                //设置后缀点击事件
                holder.tvSuffix.setOnClickListener {
                    val latestText  =holder.etInputQuick.text.toString()
                    val suffix = holder.tvSuffix.text.toString()
                    if (!latestText.endsWith(suffix)){
                        holder.etInputQuick.setText(latestText + suffix)
                        holder.etInputQuick.setSelection(holder.etInputQuick.text.length)
                    }
                }

            }
            //对于Paypal应用添加前缀后缀方案
            is QuickInputNoIconViewHolder ->{
                holder.etInputQuickNoIcon.hint = item.hint
                bindTextWatcher(holder.etInputQuickNoIcon,item.key)

                //设置前缀点击事项
                holder.tvPrefixNoIcon.setOnClickListener {
                    val latestText  =holder.etInputQuickNoIcon.text.toString()
                    val prefix = holder.tvPrefixNoIcon.text.toString()
                    if (!latestText.startsWith(prefix)){
                        holder.etInputQuickNoIcon.setText(prefix + latestText)
                        holder.etInputQuickNoIcon.setSelection(holder.etInputQuickNoIcon.text.length)
                    }
                }
                //设置后缀点击事件
                holder.tvSuffixNoIcon.setOnClickListener {
                    val latestText  =holder.etInputQuickNoIcon.text.toString()
                    val suffix = holder.tvSuffixNoIcon.text.toString()
                    if (!latestText.endsWith(suffix)){
                        holder.etInputQuickNoIcon.setText(latestText + suffix)
                        holder.etInputQuickNoIcon.setSelection(holder.etInputQuickNoIcon.text.length)
                    }
                }
            }
        }

    }

    //编写标签绑定方法
    private fun bindLabel(labelView: TextView,labelText: String?){
        if (!labelText.isNullOrBlank()){
            labelView.text = labelText
            labelView.visibility = View.VISIBLE
        }else{
            labelView.visibility = View.GONE
        }
    }
    //弹出日期选择器(用于Time类型)
    private fun showDatePicker(context: Context, onSelected: (String) -> Unit) {
        val calendar  = Calendar.getInstance()
        DatePickerDialog(context,{_,year,month,day ->
            //确定日期格式
            onSelected("${month +1}月${day}日")
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    //弹出日期时间选择器
    @SuppressLint("DefaultLocale")
    private fun showDateTimePicker(context: Context, onSelected: (String) -> Unit){
        //弹出日期选择器
        val calendar  = Calendar.getInstance()
        DatePickerDialog(context,{_,year,month,day ->
            val dateResult = "$year-${month + 1}-$day"
            //弹出时间选择器
            TimePickerDialog(context,{_,hour,minute ->
                val finalResult = String.format("%s %02d:%02d",dateResult,hour,minute)
                onSelected(finalResult)
            }, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show()
    },calendar.get(java.util.Calendar.YEAR,),calendar.get(java.util.Calendar.MONTH),calendar.get(java.util.Calendar.DAY_OF_MONTH)).show()
    }

    //弹出单选列表对话框
    private fun showListDialog(context: Context, item: FieldConfig,onSelected:(String) -> Unit) {
        AlertDialog.Builder(context)
            .setItems(item.options.toTypedArray()){_,which ->
            onSelected(item.options[which])
        }.show()
    }

    //为EditText绑定文字监听器，确保用户输入的内容能够实时同步到values这个map中
    private fun bindTextWatcher(editText: EditText, key: String) {
        editText.setText(values[key] ?: "") // //如果视图滚动导致数据丢失，恢复之前保存的值
    //实时保存用户输入的信息
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            values[key] = s.toString() //将输入的内容存入map,用户后续生成协议
        }
        override fun afterTextChanged(s: Editable?) {}
    })
    }

    override fun getItemCount() = fields.size
}
