package com.jay.galanto

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.galanto.databinding.FragmentPIPBinding
import com.jay.galanto.utils.DateValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PIPFragment : Fragment() {

    private var _binding: FragmentPIPBinding? = null
    private var currentProgress = 0
    private val maxProgress = 20
    private val totalDurationMillis = 2000L
    private val STORAGE_CODE = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPIPBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchDataFromFirestore()
        fetchDataFromFirestoreOfHistory()
        val delay = totalDurationMillis / maxProgress
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                currentProgress += 1
                _binding?.circularProgressBar?.setProgress(currentProgress)

                if (currentProgress < maxProgress) {
                    handler.postDelayed(this, delay)
                }
            }
        }, delay)
    }

    private fun fetchDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("MovementData")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val entries = ArrayList<Entry>()
                val querySnapshot = collectionRef.get().await()
                var index = 0f
                for (document in querySnapshot.documents) {
                    val angle = document.getDouble("Angle")?.toFloat() ?: 0f
                    val time = document.getDouble("Time")?.toFloat() ?: 0f
                    entries.add(Entry(time, angle))
                    index++
                }
                displayChart(entries)
            } catch (e: Exception) {
                Log.e("Firestore", "Error getting documents.", e)
            }
        }
    }


    private fun displayChart(entries: ArrayList<Entry>) {
        val dataSet = LineDataSet(entries, "Angle Data")
        val data = LineData(dataSet)
        _binding?.chart?.data = data


        val descriptionText = "Movement Data of Angle(o) x Time(s)"
        val description = Description()
        description.text = descriptionText
        _binding?.chart?.description = description


        _binding?.chart?.post {
            _binding?.chart?.animateX(1000)
            _binding?.chart?.invalidate()
        }
    }

    private fun fetchDataFromFirestoreOfHistory() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("History")
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val entries = ArrayList<Entry>()
                val querySnapshot = collectionRef.get().await()
                for (document in querySnapshot.documents) {
                    val rom = document.getDouble("ROM")?.toFloat() ?: 0f
                    val dateString = document.getString("Date") ?: ""
                    val date = parseDate(dateString)
                    entries.add(Entry(date.time.toFloat(), rom))
                }

                displayChartHistory(entries)
            } catch (e: Exception) {
                Log.e("Firestore", "Error getting documents.", e)
            }
        }
    }

    private fun displayChartHistory(entries: ArrayList<Entry>) {
        if (entries.isNotEmpty()) {
            _binding?.chartHistory.let { chart ->
                val dataSet = LineDataSet(entries, "ROM Data")
                val data = LineData(dataSet)

                chart?.data = data

                val descriptionText = "ROM Data x Date"
                val description = Description()
                description.text = descriptionText
                chart?.description = description

                val xAxis = chart?.xAxis
                xAxis?.valueFormatter = DateValueFormatter()
            }

            _binding?.chartHistory?.post {
                _binding?.chartHistory?.animateX(2000)
                _binding?.chartHistory?.invalidate()
            }
        } else {
            Toast.makeText(requireContext(), "Entries Empty", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchDataFromFirestore()
        fetchDataFromFirestoreOfHistory()

    }


    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return format.parse(dateString) ?: Date()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}