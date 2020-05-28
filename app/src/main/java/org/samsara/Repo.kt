package org.samsara

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.samsara.category.CategoryCollective
import org.samsara.category.CategoryListener
import org.samsara.project.Collaborators
import org.samsara.project.ProjectAddListener
import org.samsara.project.ProjectCollective
import org.samsara.project.Projects
import org.samsara.settings.Settings
import org.samsara.transaction.Transaction

object Repo {


    val fireStore = FirebaseFirestore.getInstance()


    fun createProjectInFireStore(project:ProjectCollective, user: FirebaseUser, name: String, dataBase: RoomDataBase, listener:ProjectAddListener){

        fireStore
            .collection(Storage.project)
            .add(project)
            .addOnSuccessListener {p->

               val pathCollaborator = fireStore
                    .collection(Storage.project)
                    .document(p.id)
                    .collection(Storage.collaborators)
                   .document(user.uid)


                val collaborator = Collaborators(
                    userId = user.uid,
                    name = name,
                    creator = true
                )


                val pathCategory =  fireStore
                    .collection(Storage.project)
                    .document(p.id)
                    .collection(Storage.category)
                    .document(name)

                val category = CategoryCollective(
                    categoryId = name,
                    categoryName = name,
                    categoryType = 1,
                    categoryBalance = 0.00
                )

                val pathProjects = fireStore
                    .collection(Storage.project)
                    .document(p.id)

                fireStore.runBatch {w->
                    w.set(pathCollaborator, collaborator)
                    w.set(pathCategory, category)
                    w.update(pathProjects, "projectId", p.id )
                }
                    .addOnSuccessListener {
                        CoroutineScope(IO).launch {
                        dataBase.settingsDao().updateItems(false, p.id)
                            withContext(Main){
                                listener.projectAdded(true)
                            }

                        }
                    }
            }
    }















    fun createTransactionFireStore(transaction: Transaction, projectId: String)
    {
       // Log.d(Storage.log, transaction.toString())

        val transactionPath = fireStore
                .collection(Storage.project)
                .document(projectId)
                .collection(Storage.transaction)
                .document()


        val pathCategory = fireStore
                .collection(Storage.project)
                .document(projectId)
                .collection(Storage.category)
                .document(transaction.transactionCategory)

        val pathAccount = fireStore
            .collection(Storage.project)
            .document(projectId)
            .collection(Storage.category)
            .document(transaction.transactionAccount)



            fireStore
                .runBatch{
                    it.set(transactionPath, transaction )
                    it.update(pathAccount, Storage.categoryBalance, FieldValue.increment(transaction.transactionAmount))
                    it.update(pathCategory, Storage.categoryBalance, FieldValue.increment(transaction.transactionAmount*-1))
                }
    }














    fun createTransferFireStore( transaction: Transaction, projectId: String, receiver:String, sender:String)
    {

        Log.d(Storage.log, "transfer")

        val projectPath = fireStore
            .collection(Storage.project)
            .document(projectId)


        val transactionPath1 = projectPath
            .collection(Storage.transaction)
            .document()

        val transactionPath2 = projectPath
            .collection(Storage.transaction)
            .document()


        val pathCategory = projectPath
            .collection(Storage.category)
            .document(transaction.transactionCategory)


        val pathAccount = projectPath
            .collection(Storage.category)
            .document(transaction.transactionAccount)



        val transaction1 = Transaction(transactionAccount = transaction.transactionAccount, transactionCategory = receiver, transactionAmount = transaction.transactionAmount, transactionDate = transaction.transactionDate)
        val transaction2 = Transaction(transactionAccount = transaction.transactionCategory, transactionCategory = sender, transactionAmount = transaction.transactionAmount*-1, transactionDate = transaction.transactionDate)

        fireStore
            .runBatch{
                it.set(transactionPath1, transaction1)
                it.set(transactionPath2, transaction2)
                it.update(pathAccount, Storage.categoryBalance, FieldValue.increment(transaction.transactionAmount))
                it.update(pathCategory, Storage.categoryBalance, FieldValue.increment(transaction.transactionAmount*-1))
            }

    }











    fun collaborate(collaborator: Collaborators, projectId:String, category: CategoryCollective, dataBase: RoomDataBase, listener: ProjectAddListener)
    {
        val path = fireStore.collection(Storage.project).document(projectId)
        fireStore
            .runBatch {

                it.update( path, "members", FieldValue.arrayUnion(collaborator.userId))
                it.set(path.collection(Storage.collaborators).document(collaborator.userId), collaborator)
                it.set(path.collection(Storage.category).document(category.categoryId), category)
            }.addOnSuccessListener {
                CoroutineScope(IO).launch {
                    dataBase.settingsDao().update(Settings(collectiveProjectId = projectId, isLocalProject = false))

                    withContext(Main){
                        listener.projectAdded(true)
                    }

                }
            }
            .addOnFailureListener {
                Log.d(Storage.log, it.toString())
            }
    }












    fun projectList(user:FirebaseUser):LiveData<List<ProjectCollective>>
    {

        return object :LiveData<List<ProjectCollective>>()
        {

            override fun onActive() {
                super.onActive()



                    fireStore
                        .collection(Storage.project)
                        .whereArrayContains("members", user.uid)
                        .addSnapshotListener { q, e ->
                            e?.let {
                                Log.d(Storage.log, it.toString())
                            }

                            q?.let {
                             value =   it.toObjects(ProjectCollective::class.java)
                            }
                        }
            }
        }
    }













    fun currentProject(projectId: String):LiveData<ProjectCollective>
    {

        return object :LiveData<ProjectCollective>()
        {

            override fun onActive()
            { super.onActive()

                    fireStore
                        .collection(Storage.project)
                        .document(projectId)
                        .addSnapshotListener { data, e ->

                            e?.let {
                                Log.d(Storage.log, it.toString())
                            }

                            data?.let {d->

                                val p = d.toObject(ProjectCollective::class.java)
                                p?.let {project->
                                    project.projectId = d.id
                                    value = project
                                }
                            }
                        }

            }
        }
    }











    fun myAccount(user: FirebaseUser, project: ProjectCollective):LiveData<CategoryCollective>
    {
        return object :LiveData<CategoryCollective>()
        {
            override fun onActive() {
                super.onActive()
                   // Log.d(Storage.log, u.uid)
                    //  Log.d(Storage.log, ": ${Storage.project}/${p.projectId}/${Storage.category}/${u.uid}")
                    fireStore
                        .collection(Storage.project)
                        .document(project.projectId)
                        .collection(Storage.collaborators)
                        .document(user.uid)
                        .addSnapshotListener{ s, e ->

                            e?.let {
                            Log.d(Storage.log, it.toString())
                            }

                            s?.let {d->
                                val name = d.getString("name").toString()

                                fireStore
                                    .collection(Storage.project)
                                    .document(project.projectId)
                                    .collection(Storage.category)
                                    .document(name)
                                    .addSnapshotListener { ss, exp ->

                                        exp?.let {
                                            Log.d(Storage.log, it.toString())
                                        }

                                        ss?.let {snap->
                                            value  = snap.toObject(CategoryCollective::class.java)
                                        }
                                    }
                            }
                        }

            }
        }
    }










    fun allCategory(project: ProjectCollective):LiveData<List<CategoryCollective>>
    {

        return object :LiveData<List<CategoryCollective>>()
        {
            override fun onActive() {

                super.onActive()
                        fireStore
                            .collection(Storage.project)
                            .document(project.projectId)
                            .collection(Storage.category)
                            .addSnapshotListener{ s, e ->
                                e?.let {
                                  Log.d(Storage.log, it.toString())
                                }

                                s?.let {q->
                                   value = q.toObjects(CategoryCollective::class.java)


                                }
                            }
            }
        }

    }











    fun addCategory(projectId: String, category: CategoryCollective, listener:CategoryListener)
    {
        fireStore
            .collection(Storage.project)
            .document(projectId)
            .collection(Storage.category)
            .document(category.categoryId)
            .set(category)
            .addOnSuccessListener {
                listener.categoryListener(category)
            }
    }











    fun categoryDetails(projectId: String, categoryId: String, projectType: Int):LiveData<List<Transaction>>{

        return object : LiveData<List<Transaction>>()
        {
            override fun onActive() {

                val field = if(projectType == 1) "transactionAccount" else "transactionCategory"

                fireStore
                    .collection(Storage.project)
                    .document(projectId)
                    .collection(Storage.transaction)
                    .whereEqualTo(field, categoryId)
                    .orderBy("transactionDate")
                    .addSnapshotListener { q, e ->

                        e?.let {
                            Log.d(Storage.log, it.toString())
                        }

                        q?.let {t->
                            value = t.toObjects(Transaction::class.java)
                        }
                    }
            }
        }

    }


}