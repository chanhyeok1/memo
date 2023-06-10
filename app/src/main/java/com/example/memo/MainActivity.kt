package com.example.memo

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memo.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val preferences by lazy {
        this.getPreferences(0)
    }
    private var savedDate: String
        get() = preferences.getString("key_saved_data", "") ?: ""
        set(value){
            val editor = preferences.edit()
            editor.putString("key_saved_data", value)
            editor.apply()
//            val gson = Gson()
//            val convertedJsonString = gson.toJson()
        }
    private val memoListFlow = MutableStateFlow(arrayListOf<MemoItem>())
    private val adapter = MemoAdapter()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvMemo.adapter = adapter
        binding.rvMemo.layoutManager = LinearLayoutManager(this)

        if(savedDate.isNotEmpty()){
            val type = object : TypeToken<ArrayList<MemoItem>>() {}.type
            val saverArrayList = Gson().fromJson<ArrayList<MemoItem>>(savedDate, type)
            memoListFlow.value = saverArrayList
            adapter.submitList(memoListFlow.value)
        }

        binding.btnSubmit.setOnClickListener {
            saveMemo()
        }
    }
    private  fun saveMemo(){
        if(binding.edtInput.text.isNotEmpty()){
            val newItem = MemoItem(
                wroteTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                content = binding.edtInput.text.toString()
            )
            val newList = memoListFlow.value
            newList.add(newItem)
            memoListFlow.value = newList
            adapter.submitList(memoListFlow.value)
            adapter.notifyDataSetChanged()
            val jsonString = Gson().toJson(newList)
            savedDate = jsonString
        }
    }

    companion object{
        private const val APP_PREF = "APP_PREF"
    }

}