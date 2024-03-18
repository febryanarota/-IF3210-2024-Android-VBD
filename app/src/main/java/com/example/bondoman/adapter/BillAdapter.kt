package com.example.bondoman.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.databinding.BillItemBinding
import com.example.bondoman.databinding.ItemTransactionBinding
import com.example.bondoman.models.BillItem
import com.example.bondoman.models.BillList
import com.example.bondoman.room.models.Transaction

class BillAdapter(private val context: Context, private val bill: BillList)
    : RecyclerView.Adapter<BillAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillAdapter.ViewHolder {
        val binding = BillItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillAdapter.ViewHolder, position: Int) {
        val billItem = bill.items[position]
        holder.bind(billItem)
    }

    override fun getItemCount(): Int = bill.items.size

    inner class ViewHolder(private val binding: BillItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(billItem: BillItem) {
            binding.editTextText.setText(billItem.name)
            binding.editTextNumber.setText(billItem.qty.toString())
            binding.editTextNumberDecimal.setText(billItem.price.toString())

            binding.editTextText.doAfterTextChanged { text ->
                billItem.name = text.toString()
            }
            binding.editTextNumber.doAfterTextChanged { text ->
                billItem.qty = text.toString().toInt()
            }
            binding.editTextNumberDecimal.doAfterTextChanged { text ->
                billItem.price = text.toString().toFloat()
            }
        }
    }
}