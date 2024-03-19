package com.example.bondoman.utils

import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.bondoman.room.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.Locale

/*
* Contoh penggunaan (TODO buat requirePermission yang bisa encapsulate sisanya):
*
val transactionFactory = TransactionFactory(this@ScanFragment).apply {
    applyToTransaction{
        category = "Pembelian"
        place = "Scan Bill"
    }
    setPriceIDR(totalPrice)
}

locationRequest.requirePermissions(this@ScanFragment, LocationUtils.PERMISSIONS_REQUIRED)
transactionFactory.setLocationAutomatic(this@ScanFragment)

transactionFactory.doWhenReady { newTransaction: Transaction ->
    viewModel.addTransaction(newTransaction)
}
*
* */
class TransactionFactory(private val owner: LifecycleOwner) {
    private var transaction: Transaction = Transaction()
    private var ready = MutableLiveData<Boolean>(true)
    private var action: ((transaction: Transaction) -> Unit)? = null

    // Edit transaction manually
    fun applyToTransaction(t: Transaction.() -> Unit) {
        transaction.apply(t)
    }

    // Edit price string with format IDR X.XXX.XXX,XX
    fun setPriceIDR(price: Float) {
        val format = DecimalFormat.getNumberInstance(Locale("id", "ID"))
        format.apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
            isGroupingUsed = true
        }
        transaction.price = "IDR ${format.format(price)}"
    }

    // Edit location with current location, if possible
    @Throws(NullPointerException::class)
    fun setLocationAutomatic(caller: Fragment) {
        transaction.location = LocationUtils.locationString
    }

    fun setLocationWithMaps(caller: Fragment) {

    }

    // use this function if there is blocking function used in factory
    suspend fun doWhenReady(action: ((transaction: Transaction) -> Unit)) {
        this.action = action
        if (ready.value == true) {
            action(transaction)
            this.action = null
        } else {
            withContext(Dispatchers.Main) {
                ready.observe(owner) {
                    if (it) {
                        action(transaction)
                    }
                }
            }

        }
    }

    fun build(): Transaction {
        return transaction
    }


}