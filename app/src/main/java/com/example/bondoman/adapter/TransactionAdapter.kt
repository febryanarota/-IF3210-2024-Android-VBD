package com.example.bondoman.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.databinding.ItemTransactionBinding
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.viewmodels.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(private val context: Context, private val transactions: List<Transaction>, private val viewModel: TransactionViewModel, private val clickListener: TransactionClickListener)
    : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionAdapter.ViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactions = transactions[position]
        holder.bind(transactions, position)
    }

    override fun getItemCount() = transactions.size

    inner class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction, position: Int) {
            binding.tvName.text = transaction.place
            binding.tvDesc.text = transaction.category
            var price = "- IDR " + transaction.price
            if (transaction.category == "Pemasukan") {
                price = "+ IDR " + transaction.price
            }
            binding.tvPrice.text = price
            binding.tvLocation.text = transaction.location
            binding.tvDate.text = formatDateToString(transaction.date)
            binding.bttnTrash.setOnClickListener {
                viewModel.deleteTransaction(transaction)
                notifyItemRemoved(position)
            }
            binding.bttnEdit.setOnClickListener {
                clickListener.onEditTransaction(transaction)
            }
            binding.bttnLocation.setOnClickListener {
                clickListener.onLocationClicked(transaction)
            }
        }
    }

    fun formatDateToString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    interface TransactionClickListener {
        fun onEditTransaction(transaction: Transaction)
        fun onLocationClicked(transaction: Transaction)
    }
}