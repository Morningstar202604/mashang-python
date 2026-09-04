package com.mashang.python

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mashang.python.databinding.ActivityMainBinding
import kotlinx.parcelize.Parcelize

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var catalog: Catalog? = null
    private lateinit var adapter: CourseAdapter
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    finish()
                } else {
                    backPressedTime = System.currentTimeMillis()
                    Toast.makeText(this@MainActivity, R.string.msg_back_press_again, Toast.LENGTH_SHORT).show()
                }
            }
        })

        loadCatalog()
        setupRecyclerView()
        setupClickListeners()
        setupFilters()

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_search -> {
                    startActivity(Intent(this, com.mashang.python.ui.SearchActivity::class.java))
                    true
                }
                R.id.nav_progress -> {
                    val progressDialog = ProgressDialog()
                    progressDialog.show(supportFragmentManager, "progress")
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, com.mashang.python.ui.ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadCatalog() {
        try {
            val json = assets.open("catalog.json").bufferedReader().use { it.readText() }
            catalog = Gson().fromJson(json, Catalog::class.java)

            binding.tvTotalUnits.text = getString(R.string.tv_total_units, catalog?.packs?.size ?: 0)
            binding.tvTotalXp.text = getString(R.string.tv_total_xp, catalog?.totalXp ?: 0)
            binding.tvDifficulty.text = getString(R.string.tv_difficulty, catalog?.packs?.map { it.difficulty }?.distinct()?.size ?: 0)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.msg_course_load_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val packs = catalog?.packs ?: emptyList()
        adapter = CourseAdapter(packs) { unit ->
            showCourseDetail(unit)
        }

        binding.rvCourses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnStartLearning.setOnClickListener {
            val packs = catalog?.packs
            if (packs != null && packs.isNotEmpty()) {
                showCourseDetail(packs[0])
            }
        }

        binding.btnViewProgress.setOnClickListener {
            showProgress()
        }
    }

    private fun setupFilters() {
        binding.chipAll.setOnClickListener { filterCourses("all") }
        binding.chipBeginner.setOnClickListener { filterCourses("beginner") }
        binding.chipIntermediate.setOnClickListener { filterCourses("intermediate") }
        binding.chipAdvanced.setOnClickListener { filterCourses("advanced") }
        binding.chipExpert.setOnClickListener { filterCourses("expert") }
    }

    private fun filterCourses(difficulty: String) {
        val packs = catalog?.packs ?: return
        val filtered = if (difficulty == "all") {
            packs
        } else {
            packs.filter { it.difficulty == difficulty }
        }
        adapter.updateCourses(filtered)
    }

    private fun showCourseDetail(unit: Unit) {
        try {
            val fileName = "content_packs/${unit.id}.json"
            val json = assets.open(fileName).bufferedReader().use { it.readText() }
            val exercises = Gson().fromJson(json, Array<Exercise>::class.java)

            val detailDialog = CourseDetailDialog.newInstance(unit, exercises)
            detailDialog.show(supportFragmentManager, "course_detail")
        } catch (e: Exception) {
            Toast.makeText(this, R.string.msg_course_load_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgress() {
        val progressDialog = ProgressDialog()
        progressDialog.show(supportFragmentManager, "progress")
    }

    @Parcelize
    data class Catalog(
        val updatedAt: String,
        val totalUnits: Int,
        val totalXp: Int,
        val packs: List<Unit>
    ) : Parcelable

    @Parcelize
    data class Unit(
        val id: String,
        val name: String,
        val version: Int,
        val difficulty: String,
        val xp: Int
    ) : Parcelable

    @Parcelize
    data class Exercise(
        val id: String,
        val order: Int,
        val chapter: Int,
        val title: String,
        val subtitle: String,
        val version: Int,
        val difficulty: String,
        val xp: Int,
        val blocks: List<Block>,
        val tests: List<String>,
        val hint: String
    ) : Parcelable

    @Parcelize
    data class Block(
        val type: String,
        val text: String? = null,
        val code: String? = null,
        val output: String? = null,
        val language: String? = null,
        val question: String? = null,
        val options: List<String>? = null,
        val answer: Int? = null,
        val explain: String? = null,
        val title: String? = null,
        val goal: String? = null
    ) : Parcelable
}
