package com.example.bondoman.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.models.Transaction
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "TransactionViewModel"
class TransactionViewModel(private val transactionRepository: TransactionRepository): ViewModel() {
    private val isRefreshingLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isSendEmail = MutableLiveData<Boolean>(false)

    fun getAllTransaction() = transactionRepository.getAllTransactions().asLiveData(viewModelScope.coroutineContext)

    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.insertTransaction(transaction)
    }

    fun addTransactions(transactions: MutableList<Transaction>) = viewModelScope.launch {
        transactionRepository.insertTransactions(transactions)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.deleteTransaction(transaction)
    }

    fun deleteAll() = viewModelScope.launch {
        transactionRepository.deleteAll()
    }

    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.updateTransaction(transaction)
    }

    fun getIsRefreshingData(): LiveData<Boolean> {
        return isRefreshingLiveData
    }
    //
    fun fetchNewTransaction() {
        Log.i(TAG, "fetchNewTransaction")
        isRefreshingLiveData.value = true
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                isRefreshingLiveData.value = false
            }
        },1_000)
    }
    private fun createWorkbook(transactions: List<Transaction>): HSSFWorkbook {
        val workbook = HSSFWorkbook()
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
            row.createCell(2).setCellValue(transaction.category)
            row.createCell(3).setCellValue(transaction.price)
            row.createCell(4).setCellValue(transaction.date.toString())
            row.createCell(5).setCellValue(transaction.location)
        }
        return workbook
    }

    fun downloadTransaction(): Boolean {
        viewModelScope.launch() {
            try {
                transactionRepository.getAllTransactions().collect { transactions ->
                    val workbook = createWorkbook(transactions)
                    try {
                        val downloadDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
                        Log.i(TAG, "$downloadDir")
                        if (!downloadDir.exists()) {
                            downloadDir.mkdirs()
                            Log.i(TAG, "downloadDir doesn't exist")
                        }
                        val dateFormat = SimpleDateFormat("dd-MM-yy")
                        val currentDate = Date()
                        val file = File(downloadDir, "${dateFormat.format(currentDate)}.xls")
                        val fileOutputStream = FileOutputStream(file)
                        workbook.write(fileOutputStream)
                        fileOutputStream.close()
                        Log.i(TAG, "file saved")
                        true
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.e(TAG, "failed to save the file")
                        false
                    }
                    workbook.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("TAG", "failed ${e.message}")
            }
        }
        return true
    }

    fun sendEmail(context: Context, userEmail: String) {
        viewModelScope.launch() {

            try {
                transactionRepository.getAllTransactions().collect { transactions ->
                    val workbook = createWorkbook(transactions)
                    val file = File(context.cacheDir, "transaction.xls")
                    val outputStream = FileOutputStream(file)
                    workbook.write(outputStream)

                    // create email intent
                    if (isSendEmail.value === true) {
                        val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.type = "application/vnd.ms-excel"
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Transaction Data")
                        emailIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Please find attached the transaction data file."
                        )
                        emailIntent.putExtra(
                            Intent.EXTRA_STREAM,
                            FileProvider.getUriForFile(
                                context,
                                context.packageName + ".provider",
                                file
                            )
                        )
                        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        // Start email intent
                        context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                        Log.i(TAG, "send email intent")
                    }
                    isSendEmail.value = false
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: ${e.message}")
            }

            isSendEmail.value = false
        }


    }
}