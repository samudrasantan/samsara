package org.samsara.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.all_projects.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.samsara.MainViewModel
import org.samsara.R
import org.samsara.SharedViewModel
import org.samsara.Storage
import org.samsara.category.BalanceAdapter
import org.samsara.category.CategoryCollective
import org.samsara.category.CategoryListener
import org.samsara.project.ProjectAdapter


class HomeFragment : Fragment(), CategoryListener {



    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: HomeViewModel
    private  val model: MainViewModel by activityViewModels()
    private lateinit var navController:NavController






    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_home, container, false)






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Storage.log, "Home")



        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]



        //Log.d(Storage.log, "m : $model")




        model.settingsData.observe(viewLifecycleOwner, Observer {

            it?.let {s->
                if(s.isLocalProject)
                {
                    if(it.localProjectId == 0)
                    {
                       // Log.d(Storage.log, "no local project")
                        val action = HomeFragmentDirections.actionNavHomeToAllProjects()
                        navController.navigate(action)
                    }

                }

                else
                {

                    if(it.collectiveProjectId.isEmpty())
                    {
                       // Log.d(Storage.log, "no collective project")
                        val action = HomeFragmentDirections.actionNavHomeToAllProjects()
                        navController.navigate(action)
                    }

                }

            }?:run{
                val action = HomeFragmentDirections.actionNavHomeToAllProjects()
                navController.navigate(action)
               // Log.d(Storage.log, "run")
            }



        })






        val accountAdapter = BalanceAdapter(this)
        val categoryAdapter = BalanceAdapter(this)
        view.accountRv.apply{
                layoutManager = GridLayoutManager(requireContext(),2)
                adapter = accountAdapter
            }
        view.CategoryRv.apply {
                layoutManager = GridLayoutManager(requireContext(),2)
                adapter = categoryAdapter
            }

        model.liveAllCategory.observe(viewLifecycleOwner, Observer {list->


                accountAdapter.submitData(list.filter {c->
                    c.categoryType == 1
                })
                categoryAdapter.submitData(list.filter {c->
                    c.categoryType == 2
                })
                view.accountRv.swapAdapter(accountAdapter, false)
                view.CategoryRv.swapAdapter(categoryAdapter, false)
            })






        view.sumButton.setOnClickListener {
            model.summaryType.value = 1
            val action = HomeFragmentDirections.actionNavHomeToSummary()
            navController.navigate(action)
        }

        view.addButton.setOnClickListener{
            val action = HomeFragmentDirections.actionNavHomeToAddTransaction()
            navController.navigate(action)

        }





    }




    override fun categoryListener(category: CategoryCollective) {
        model.reportCategory.value = category
        val action = HomeFragmentDirections.actionNavHomeToReportCategory()
        navController.navigate(action)
    }

}