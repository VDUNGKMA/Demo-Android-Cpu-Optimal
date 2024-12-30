package com.example.cpuperfdemo_optimal

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private val pageSize = 100 // Số phần tử hiển thị mỗi lần
    private var currentPage = 0 // Trang hiện tại
    private val items = mutableListOf<Item>() // Danh sách hiển thị
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Sample1Activity", "onCreate")
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ItemAdapter(items)

        swipeRefreshLayout.setOnRefreshListener(::refreshData)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // Tải thêm dữ liệu khi cuộn đến cuối danh sách
                if (lastVisibleItemPosition == items.size - 1) {
                    currentPage++
                    loadPage(currentPage)
                }
            }
        })

        // Tải trang đầu tiên
        loadPage(currentPage)
    }

    private fun loadPage(page: Int) {
        val start = page * pageSize
        val end = (start + pageSize).coerceAtMost(500_000) // Đảm bảo không vượt quá tổng số phần tử
        val now = LocalDateTime.now()

        val newItems = (start until end).map { createItem(now, it + 1) }
        items.addAll(newItems)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun refreshData() {
        items.clear()
        currentPage = 0
        loadPage(currentPage)
        swipeRefreshLayout.isRefreshing = false
    }
}

private fun createItem(now: LocalDateTime, offset: Int): Item {
    val date = now.plusDays(offset.toLong()).toLocalDate().atStartOfDay()
    return Item(
        formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        remainingTime = getRemainingTime(now, date)
    )
}

private fun getRemainingTime(start: LocalDateTime, end: LocalDateTime): String {
    val duration = java.time.Duration.between(start, end)
    val days = duration.toDays()
    val hours = duration.minusDays(days).toHours()
    val minutes = duration.minusDays(days).minusHours(hours).toMinutes()
    val seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).seconds
    return buildString {
        if (days > 0) append("$days d")
        if (hours > 0) append(" $hours h")
        if (minutes > 0) append(" $minutes min")
        if (seconds > 0) append(" $seconds s")
    }.trim()
}
