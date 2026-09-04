package com.mashang.python.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mashang.python.CourseAdapter
import com.mashang.python.R
import com.mashang.python.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: CourseAdapter
    private var allCourses: List<com.mashang.python.MainActivity.Unit> = emptyList()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        loadCourses()
        setupRecyclerView()
        setupSearch()
        setupClickListeners()
    }
    
    private fun loadCourses() {
        try {
            val json = assets.open("catalog.json").bufferedReader().use { it.readText() }
            val catalog = com.google.gson.Gson().fromJson(json, com.mashang.python.MainActivity.Catalog::class.java)
            allCourses = catalog.packs
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, R.string.msg_course_load_failed, android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = CourseAdapter(allCourses.toMutableList()) { unit ->
            openCourseDetail(unit)
        }
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = adapter
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCourses(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun filterCourses(query: String) {
        if (query.isEmpty()) {
            adapter.updateCourses(allCourses)
            return
        }
        
        val filtered = allCourses.filter { unit ->
            unit.name.contains(query, ignoreCase = true) ||
            unit.id.contains(query, ignoreCase = true) ||
            unit.difficulty.contains(query, ignoreCase = true)
        }
        adapter.updateCourses(filtered)
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnClear.setOnClickListener {
            binding.etSearch.text?.clear()
        }
    }

    private fun openCourseDetail(unit: com.mashang.python.MainActivity.Unit) {
        try {
            val fileName = "content_packs/${unit.id}.json"
            val json = assets.open(fileName).bufferedReader().use { it.readText() }
            val exercises = com.google.gson.Gson().fromJson(json, Array<com.mashang.python.MainActivity.Exercise>::class.java)
            
            val detailDialog = com.mashang.python.CourseDetailDialog.newInstance(unit, exercises)
            detailDialog.show(supportFragmentManager, "course_detail")
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, R.string.msg_course_load_failed, android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
