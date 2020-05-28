package org.samsara.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import org.samsara.Repo
import org.samsara.transaction.Transaction

class ReportViewModel:ViewModel() {

    fun categoryDetails(projectId: String, categoryId: String, categoryType:Int): LiveData<List<Transaction>> = Repo.categoryDetails(projectId, categoryId, categoryType)
}