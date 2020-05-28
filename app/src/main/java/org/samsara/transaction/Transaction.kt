package org.samsara.transaction

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.util.*

data class Transaction (

    @Exclude var transactionId:String = "",
    var transactionAmount:Double = 0.00,
    var transactionCategory:String = "",
    var transactionAccount:String = "",
    var transactionDate:Timestamp = Timestamp(Date())

)