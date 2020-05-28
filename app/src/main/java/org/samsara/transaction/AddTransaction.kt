package org.samsara.transaction

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.add_transaction.view.*
import org.samsara.MainViewModel

import org.samsara.R
import org.samsara.Storage
import org.samsara.category.CategoryCollective
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class AddTransaction : Fragment() {

    private var transactionType = 4
   // private val liveInputType: LiveData<Int> = transactionType

    private val model: MainViewModel by activityViewModels()
    private var account = CategoryCollective()
    private var category = CategoryCollective()
    private lateinit var nav :NavController



    private lateinit var viewModel: AddTransactionViewModel



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.add_transaction, container, false)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Storage.log, "Add Transaction")

       //calendar.set(Calendar.ZONE_OFFSET, 0)
       // calendar.set(Calendar.HOUR, 0)



        nav = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this)[AddTransactionViewModel::class.java]




        view.account1.editText?.inputType = InputType.TYPE_NULL
        view.account2.editText?.inputType = InputType.TYPE_NULL
        view.transactionDate.text = Date().toString()







        view.account2.editText?.setOnClickListener {

            if(model.liveTransactionType.value == 2)
                model.categoryType.value = 1
            else
                model.categoryType.value = 2

            model.destinationType.value = 2
            val action = AddTransactionDirections.actionAddTransactionToAllCategories()
            nav.navigate(action)

            //Log.d(Storage.log, "clicked")
        }


        view.account1.editText?.setOnClickListener {
            model.categoryType.value = 1
            model.destinationType.value = 1
            val action = AddTransactionDirections.actionAddTransactionToAllCategories()
            nav.navigate(action)
            //Log.d(Storage.log, "clicked")
        }





        view.income.setOnClickListener {
            model.transactionType.value = 0
        }
        view.expenditure.setOnClickListener {
            model.transactionType.value = 1
        }
        view.transfer.setOnClickListener {
            model.transactionType.value = 2
        }








        model.liveAccount.observe(viewLifecycleOwner, Observer {c->
            c?.let {

                Log.d(Storage.log, it.toString())


            view.account1.editText?.setText(it.categoryName)
            account = it
            }
        })
        model.liveCategory.observe(viewLifecycleOwner, Observer {c->
            c?.let {

                // Log.d(Storage.log, "category ${it.categoryType}")

                if(it.categoryType == 1)
                {
                    model.transactionType.value = 2
                }

                else{
                    if(model.transactionType.value == 0) {
                        model.transactionType.value = 0
                    }

                    else
                    {
                        model.transactionType.value = 1
                    }
                }

                view.account2.editText?.setText(it.categoryName)
                category = it
            }

        })
        model.liveTransactionType.observe(viewLifecycleOwner, Observer {

            // Log.d(Storage.log, "transaction $it")

            if(it==0) {

                view.income.setTextColor(Color.WHITE)
                view.income.setBackgroundColor(Color.rgb(0,191,165))

                view.expenditure.setTextColor(Color.BLACK)
                view.expenditure.setBackgroundColor(Color.WHITE)

                view.transfer.setTextColor(Color.BLACK)
                view.transfer.setBackgroundColor(Color.WHITE)

                view.amount.startIconDrawable = resources.getDrawable(R.drawable.ic_add_box_black_24dp)

                transactionType = 0
                view.account2.hint = "Category"

                if(model.liveCategory.value?.categoryType == 1)
                {
                    model.destinationType.value = 2
                    model.categoryType.value = 2
                    val action = AddTransactionDirections.actionAddTransactionToAllCategories()
                    nav.navigate(action)
                }

            }

            if(it == 1) {


                view.expenditure.setTextColor(Color.WHITE)
                view.expenditure.setBackgroundColor(Color.rgb(255,109,0))

                view.income.setTextColor(Color.BLACK)
                view.income.setBackgroundColor(Color.WHITE)

                view.transfer.setTextColor(Color.BLACK)
                view.transfer.setBackgroundColor(Color.WHITE)

                view.amount.startIconDrawable = resources.getDrawable(R.drawable.ic_indeterminate_check_box_black_24dp)
                transactionType = 1
                view.account2.hint = "Category"

                if(model.liveCategory.value?.categoryType == 1)
                {
                    model.destinationType.value = 2
                    model.categoryType.value = 2
                    val action = AddTransactionDirections.actionAddTransactionToAllCategories()
                    nav.navigate(action)
                }

            }

            if(it == 2) {

                view.transfer.setTextColor(Color.WHITE)
                view.transfer.setBackgroundColor(Color.rgb(197,17,98))

                view.expenditure.setTextColor(Color.BLACK)
                view.expenditure.setBackgroundColor(Color.WHITE)

                view.income.setTextColor(Color.BLACK)
                view.income.setBackgroundColor(Color.WHITE)

                view.amount.startIconDrawable =
                    resources.getDrawable(R.drawable.ic_swap_horiz_black_24dp)

                view.account2.hint = "Account"
                transactionType = 2


                if(model.liveCategory.value?.categoryType == 2)
                {
                    model.destinationType.value = 2
                    model.categoryType.value = 1
                    val action = AddTransactionDirections.actionAddTransactionToAllCategories()
                    nav.navigate(action)
                }

            }
        })
        model.settingsData.observe(viewLifecycleOwner, Observer {
            it?.let { s->

                if(s.localProjectId ==0 && s.collectiveProjectId.isEmpty())
                {
                    val action = AddTransactionDirections.actionAddTransactionToAllProjects()
                    nav.navigate(action)
                }

                if(s.isLocalProject)
                {
                    val action = AddTransactionDirections.actionAddTransactionToAllProjects()
                    nav.navigate(action)
                }
            }

        })
















        view.transactionSubmit.setOnClickListener {

            if(view.amount.editText?.text.isNullOrEmpty())
            {
                return@setOnClickListener
            }


            if(view.account1.editText?.text.isNullOrEmpty())
            {
                return@setOnClickListener
            }


            if(view.account2.editText?.text.isNullOrEmpty())
            {
                return@setOnClickListener
            }


            val amount = view.amount.editText?.text.toString().toDouble()
            val modifier = if (transactionType == 0){1} else (-1)
            val transaction = Transaction(
                transactionAmount = amount * modifier,
                transactionCategory = category.categoryId,
                transactionAccount = account.categoryId,
                transactionDate = Timestamp(Date())
            )

            model.liveProject.value?.let {
                if(model.liveTransactionType.value == 2)
                {

                    model.liveCategory.value?.let {c->
                        model.liveAccount.value?.let {a->
                            viewModel.addTransfer(transaction, it.projectId, c.categoryName, a.categoryName)
                        }

                    }

                }

                else {
                    viewModel.addTransaction(transaction, it.projectId)
                }

            }

            view.amount.editText?.setText("")


        }




    }



}
