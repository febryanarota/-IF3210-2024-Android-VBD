package com.example.bondoman.viewmodels

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

//class TransactionViewModel : ViewModel() {
//
//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text
//}


private const val TAG = "TransactionViewModel"
class TransactionViewModel(private val transactionRepository: TransactionRepository): ViewModel() {
    //    private val transactionsLiveData: MutableLiveData<List<Transaction>>
    private val isRefreshingLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun getAllTransaction() = transactionRepository.getAllTransactions().asLiveData(viewModelScope.coroutineContext)

    fun addTransaction(place: String) = viewModelScope.launch {
        val transactionObject = Transaction(place = place)
        transactionRepository.insertTransaction(transactionObject)
    }

    fun deleteAll() = viewModelScope.launch {
        transactionRepository.deleteAll()
    }
    fun addTransactions(transactions: MutableList<Transaction>) = viewModelScope.launch {
        transactionRepository.insertTransactions(transactions)
    }
    //
//    init {
//        Log.i(TAG, "init")
//        transactionsLiveData = MutableLiveData()
//        transactionsLiveData.value = getAllTransaction()
//        isRefreshingLiveData = MutableLiveData()
//        isRefreshingLiveData.value = false
//    }
//
//
////    fun getTransactions(): LiveData<MutableList<Transaction>> {
////        return transactionsLiveData
////    }
////    private fun createTransaction(): MutableList<Transaction> {
////        val transactions = mutableListOf<Transaction>()
////        for (i in 1..150) transactions.add(Transaction(place = "Transaction $i", category = "Pembelian $i", price = "IDR 15.000", date = Date(), location = "Ganyang"))
////        return transactions
////    }
////
    fun getIsRefreshingData(): LiveData<Boolean> {
        return isRefreshingLiveData
    }
    //
    fun fetchNewTransaction() {
        Log.i(TAG, "fetchNewTransaction")
        isRefreshingLiveData.value = true
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                addTransaction("Hiha")
                isRefreshingLiveData.value = false
            }
        },1_000)
    }

    fun convertTransactionsToExcelFile(fileName: String): Boolean {
        viewModelScope.launch() {
            try {
                transactionRepository.getAllTransactions().collect { transactions ->

                    val workbook: HSSFWorkbook = HSSFWorkbook()
                    val sheet = workbook.createSheet("Transactions")
                    val headerRow = sheet.createRow(0)
                    headerRow.createCell(0).setCellValue("ID")
                    headerRow.createCell(1).setCellValue("Place")
                    headerRow.createCell(2).setCellValue("Category")
                    headerRow.createCell(3).setCellValue("Price")
                    headerRow.createCell(4).setCellValue("Date")
                    headerRow.createCell(5).setCellValue("Location")

                    var rowNum = 1
                    for (transaction in transactions) {
                        val row = sheet.createRow(rowNum++)
                        row.createCell(0).setCellValue(transaction.id.toDouble())
                        row.createCell(1).setCellValue(transaction.place)
                        Log.i(TAG, transaction.place)
                        row.createCell(2).setCellValue(transaction.category)
                        Log.i(TAG, transaction.category)
                        row.createCell(3).setCellValue(transaction.price)
                        Log.i(TAG, transaction.price)
                        row.createCell(4).setCellValue(transaction.date.toString())
                        row.createCell(5).setCellValue(transaction.location)
                        Log.i(TAG, "6")
                    }
                    val dateFormat = SimpleDateFormat("dd-MM-yy")
                    val currentDate = Date()
                    saveWorkbook(workbook, "${dateFormat.format(currentDate)}.xls")
                    workbook.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.i("TAG", "failed ${e.message}")
            }
        }
        return true
    }

    fun saveWorkbook(hssfWorkbook: HSSFWorkbook, fileName: String): Boolean {
        return try {
            val downloadDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
            Log.i(TAG, "${downloadDir}")
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
                Log.i(TAG, "downloadDir doesn't exist")
            }
            val file = File(downloadDir, fileName)
            val fileOutputStream = FileOutputStream(file)
            hssfWorkbook.write(fileOutputStream)
            fileOutputStream.close()
            Log.i(TAG, "file saved")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "failed to save the file")
            false
        }
    }
}