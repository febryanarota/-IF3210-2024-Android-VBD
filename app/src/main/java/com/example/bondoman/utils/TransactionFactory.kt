package com.example.bondoman.utils

import android.location.Location
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.bondoman.room.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Locale

class TransactionFactory(owner: LifecycleOwner) {
    private var transaction: Transaction = Transaction()
    private var ready = MutableLiveData<Boolean>(true)
    private var action: ((transaction: Transaction) -> Unit)? = null
    private val doAction = Observer<Boolean> {
        if (it) {
            action?.let { it1 -> it1(transaction) }
            action = null
        }
    }

    init {
        owner.lifecycleScope.launch(Dispatchers.Main) {
            ready.observe(owner, doAction)
        }
    }

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
    fun setLocationAutomatic(caller: Fragment) {
        ready.value = false
        LocationUtils.getLocation(caller) {location: Location ->
            transaction.location = location.toString()
            ready.value = true
        }
    }

    fun doWhenReady(action: ((transaction: Transaction) -> Unit)) {
        this.action = action
        if (ready.value == true) {
            action(transaction)
            this.action = null
        }
    }

    fun build(): Transaction {
        return transaction
    }


}