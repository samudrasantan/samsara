package org.samsara.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import org.samsara.Repo
import org.samsara.category.CategoryCollective
import org.samsara.project.ProjectCollective

class AddTransactionViewModel : ViewModel() {
    fun myAccount(user: FirebaseUser, project: ProjectCollective): LiveData<CategoryCollective> = Repo.myAccount(user, project)
    fun allCategory(project: ProjectCollective): LiveData<List<CategoryCollective>> = Repo.allCategory(project)
    fun addTransaction(transaction:Transaction, projectId: String) = Repo.createTransactionFireStore(transaction, projectId)
    fun addTransfer(transaction: Transaction, projectId: String, receiver: String, sender:String) = Repo.createTransferFireStore(transaction, projectId, receiver, sender)
}
