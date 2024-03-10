package com.example.bondoman.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.databinding.ItemTransactionBinding
import com.example.bondoman.models.Transaction

class TransactionAdapter(private val context: Context, private val transactions: List<Transaction>)
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
        holder.bind(transactions)
    }

    override fun getItemCount() = transactions.size

    inner class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.tvName.text = transaction.name
            binding.tvDesc.text = transaction.desc
            binding.tvPrice.text = transaction.price
            binding.tvLocation.text = transaction.location
            binding.tvDate.text = transaction.date
        }
    }
}