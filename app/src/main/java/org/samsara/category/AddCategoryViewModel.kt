package org.samsara.category

import androidx.lifecycle.ViewModel
import org.samsara.Repo

class AddCategoryViewModel: ViewModel() {
    fun addCategory(projectId: String, category: CategoryCollective, listener: CategoryListener) = Repo.addCategory(projectId, category, listener)
}