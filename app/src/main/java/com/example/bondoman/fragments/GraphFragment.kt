package com.example.bondoman.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bondoman.databinding.FragmentGraphBinding
import com.example.bondoman.viewmodels.DashboardViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import okhttp3.internal.notify

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null

    private val binding get() = _binding!!

    val values = ArrayList<PieEntry>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        dataListing()

        return root
    }

    fun dataListing() {
        values.add(PieEntry(30f, "Pembelian"))
        values.add(PieEntry(80f, "Pemasukan"))
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
            pieDataSetter = PieDataSet(values, "")
            pieDataSetter.setColors(*ColorTemplate.VORDIPLOM_COLORS)
            pieDataSetter.setDrawValues(true)
            pieDataSetter.sliceSpace = 3f
            pieDataSetter.iconsOffset = MPPointF(10f, 10f)
            pieDataSetter.selectionShift = 10f

            val dataSets = ArrayList<IPieDataSet>()
            dataSets.add(pieDataSetter)

            val data = PieData(pieDataSetter)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(12f)
            data.setValueTextColor(Color.BLACK)

            binding.pieChart.data = data
            binding.pieChart.invalidate() // Redraw the chart
            binding.pieChart.description.isEnabled = false

            binding.pieChart.animateY(1400, Easing.EaseInOutQuad)
            binding.pieChart.setEntryLabelColor(Color.BLACK)
            binding.pieChart.holeRadius = 13f

            binding.pieChart.setTransparentCircleColor(Color.BLUE)
            binding.pieChart.setTransparentCircleAlpha(110)
            binding.pieChart.transparentCircleRadius = 7f

            binding.pieChart.isDrawHoleEnabled = false

            binding.pieChart.setUsePercentValues(true)

            val legend: Legend = binding.pieChart.legend
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)
            legend.xEntrySpace = 7f
            legend.yEntrySpace = 7f
            legend.yOffset = 0f
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}