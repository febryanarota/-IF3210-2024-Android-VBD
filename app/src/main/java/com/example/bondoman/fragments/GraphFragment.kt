package com.example.bondoman.fragments

import java.text.DecimalFormat
import java.util.Locale
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bondoman.databinding.FragmentGraphBinding
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.data.LineDataSet

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null

    private val binding get() = _binding!!

    val values = ArrayList<PieEntry>()
    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this, ViewModelFactory(
            TransactionRepository(
                TransactionDatabase.getDatabaseInstance(requireContext()))
        )).get(TransactionViewModel::class.java)

        val transactions = mutableListOf<Transaction>()
        viewModel.getAllTransaction().observe(viewLifecycleOwner, Observer {transactionSnapshot ->
            transactions.clear()
            transactions.addAll(transactionSnapshot)
            for (transaction in transactions) {
                Log.i("GRAPH FRAGMENT", setPriceBack(transaction.price).toString() + " " + transaction.category)
            }
            dataListing(transactions)
        })
        return root
    }

    private fun setPriceBack(price: String): Float {
        val format = DecimalFormat.getInstance(Locale("id", "ID"))
        format.apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
            isGroupingUsed = true
        }
        val price = format.parse(price)
        return price.toFloat()
    }

    fun dataListing(transactions: MutableList<Transaction>) {
        var pembelianTotal = 0f
        var pemasukanTotal = 0f

        for (transaction in transactions) {
            if (transaction.category == "Pembelian") {
                pembelianTotal += setPriceBack(transaction.price) ?: 0f
            } else if (transaction.category == "Pemasukan") {
                pemasukanTotal += setPriceBack(transaction.price) ?: 0f
            }
        }

        var total = pembelianTotal + pemasukanTotal
        var pembelianPercentage = pembelianTotal / total
        var pemasukanPercentage = pemasukanTotal / total

        Log.i("PEMBELIAN", pembelianTotal.toString())
        values.clear()
        if (pembelianTotal > 0) {
            values.add(PieEntry(pembelianPercentage, "Pembelian"))
        }
        if (pemasukanTotal > 0) {
            values.add(PieEntry(pemasukanPercentage, "Pemasukan"))
        }
        setChart()
    }

    fun setChart() {
        val pieDataSetter: PieDataSet

        // Clear any existing data sets
        binding.pieChart.clear()

        if (binding.pieChart.data != null && binding.pieChart.data.dataSetCount > 0) {
            pieDataSetter = binding.pieChart.data.getDataSetByIndex(0) as PieDataSet
            pieDataSetter.values = values
            binding.pieChart.data.notifyDataChanged()
            binding.pieChart.notifyDataSetChanged()
        } else {
            val lineDataSet = LineDataSet(null, "Leader Line")
            lineDataSet.setDrawIcons(false)
            lineDataSet.color = Color.BLACK
            lineDataSet.lineWidth = 2f

            val lineData = LineData(lineDataSet)

            pieDataSetter = PieDataSet(values, "")

            val colors = mutableListOf<Int>()

            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    colors.add(Color.parseColor("#FF4198D7"))
                    colors.add(Color.parseColor("#FF99D1FB"))
                } else -> {
                colors.add(Color.parseColor("#7A4198D7"))
                colors.add(Color.parseColor("#454198D7"))
            }
            }

            pieDataSetter.setColors(colors)
            pieDataSetter.setDrawValues(true)
            pieDataSetter.sliceSpace = 3f
            pieDataSetter.iconsOffset = MPPointF(10f, 10f)
            pieDataSetter.selectionShift = 10f

            val dataSets = ArrayList<IPieDataSet>()
            dataSets.add(pieDataSetter)

            val data = PieData(pieDataSetter)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(12f)
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES ->
                    data.setValueTextColor(Color.WHITE)
                else ->
                    data.setValueTextColor(Color.BLACK)
            }

            binding.pieChart.holeRadius = 13f

            binding.pieChart.data = data
            binding.pieChart.invalidate() // Redraw the chart
            binding.pieChart.description.isEnabled = false

            binding.pieChart.animateY(1400, Easing.EaseInOutQuad)
            binding.pieChart.holeRadius = 13f

            binding.pieChart.setTransparentCircleColor(Color.BLUE)
            binding.pieChart.setTransparentCircleAlpha(110)
            binding.pieChart.transparentCircleRadius = 7f

            binding.pieChart.isDrawHoleEnabled = false
            binding.pieChart.setUsePercentValues(true)


            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES ->
                    binding.pieChart.setEntryLabelColor(Color.WHITE)
                else ->
                    binding.pieChart.setEntryLabelColor(Color.BLACK)
            }


            val legend: Legend = binding.pieChart.legend
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)
            legend.xEntrySpace = 7f
            legend.yEntrySpace = 7f
            legend.yOffset = 0f
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES ->
                    legend.textColor = Color.WHITE
                else ->
                    legend.textColor = Color.BLACK
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


