package org.samsara.category

import com.google.firebase.firestore.Exclude

data class CategoryCollective (

    @Exclude var categoryId:String = "",
    var categoryBalance: Double = 0.00,
    var categoryName: String = "",
    var categoryType: Int = 0
)