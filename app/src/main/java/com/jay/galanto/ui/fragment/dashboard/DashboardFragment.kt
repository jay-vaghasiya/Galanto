package com.jay.galanto.ui.fragment.dashboard

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.jay.galanto.databinding.FragmentDashboardBinding
import com.jay.galanto.utils.ViewPagerAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var adapter: ViewPagerAdapter
    private val STORAGE_CODE = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        setupProgressbar()
        buttonClick()
    }

    private fun buttonClick() {
        _binding?.igMonsoon?.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PermissionChecker.PERMISSION_GRANTED
                ) {
                    val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, STORAGE_CODE)
                } else {
                    fetchDataFromFirestore(requireContext(),"MovementData")
                    fetchDataFromFirestore(requireContext(),"History")
                }
            } else {
                fetchDataFromFirestore(requireContext(),"MovementData")
                fetchDataFromFirestore(requireContext(),"History")
            }

        }
    }



    private fun setupProgressbar() {
        _binding?.cpb1?.updateMaxTimeMillis(2000L)
        _binding?.cpb2?.updateMaxTimeMillis(8000L)
        _binding?.cpb3?.updateMaxTimeMillis(88000L)

        val progressPercentage = 0.88f
        val progressMillis =
            ((_binding?.cpb3?.maxTimeMillisValue ?: (0L * progressPercentage))).toLong()
        _binding?.cpb3?.updateProgress(progressMillis)
    }


    private fun setupTabs() {
        adapter = ViewPagerAdapter(requireActivity())
        _binding?.viewPager2?.adapter = adapter
        _binding?.tabLayout?.let {
            _binding?.viewPager2?.let { it1 ->
                TabLayoutMediator(it, it1) { tab, position ->
                    val tabNames = listOf("MCP", "PIP")
                    tab.text = tabNames[position]
                }.attach()
                it.getTabAt(1)?.select()
            }
        }
    }


    fun fetchDataFromFirestore(context: Context,collectionPath:String) {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection(collectionPath)

        collectionRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val data = document.data
                        Log.d("FirestoreData", "Document data: $data")
                    }

                    generatePdf(context, documents)
                } else {
                    Log.d("FirestoreData", "No documents found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }
    }

    fun generatePdf(context: Context, data: Any) {
        val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "sample.pdf")
        val document = Document()

        try {
            PdfWriter.getInstance(document, FileOutputStream(pdfFile))
            document.open()


            document.add(Paragraph(data.toString()))


            Log.d("PDFGeneration", "PDF created successfully at: ${pdfFile.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("PDFGeneration", "Error creating PDF: ${e.message}")
        } finally {
            document.close()
        }
    }


}