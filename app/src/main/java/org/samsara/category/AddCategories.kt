package org.samsara.category

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_add_categories.view.*
import org.samsara.MainViewModel
import org.samsara.R


class AddCategories : Fragment(), CategoryListener {

    val model: MainViewModel by activityViewModels()
    private lateinit var viewModel: AddCategoryViewModel
    var categoryType = 0
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_add_categories, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this)[AddCategoryViewModel::class.java]




        model.settingsData.observe(viewLifecycleOwner, Observer {

            it?.let {s->

                if(it.localProjectId == 0 && it.collectiveProjectId.isEmpty())
                {
                    // Log.d(Storage.log, "no local project")
                    val action = AddCategoriesDirections.actionAddCategoryToAllProjects()
                    navController.navigate(action)
                }

                if(it.isLocalProject)
                {
                    // Log.d(Storage.log, "no local project")
                    val action = AddCategoriesDirections.actionAddCategoryToAllProjects()
                    navController.navigate(action)
                }

            }?:run{
                val action = AddCategoriesDirections.actionAddCategoryToAllProjects()
                navController.navigate(action)
                // Log.d(Storage.log, "run")
            }



        })





        model.liveCategoryType.observe(viewLifecycleOwner, Observer {i->
            i?.let {


                if(it == 1)
                {
                    categoryType = 1
                    view.AddAccount.setBackgroundColor(Color.BLACK)
                    view.AddAccount.setTextColor(Color.WHITE)

                    view.AddCategory.setBackgroundColor(Color.WHITE)
                    view.AddCategory.setTextColor(Color.BLACK)

                    view.categoryName.hint = resources.getString(R.string.AccountName)
                }

               if(it == 2)
               {
                   categoryType = 2
                   view.AddAccount.setBackgroundColor(Color.WHITE)
                   view.AddAccount.setTextColor(Color.BLACK)

                   view.AddCategory.setBackgroundColor(Color.BLACK)
                   view.AddCategory.setTextColor(Color.WHITE)


                   view.categoryName.hint = resources.getString(R.string.CategoryName)



               }
            }



        })


        view.AddCategory.setOnClickListener {

            model.categoryType.value = 2
        }


        view.AddAccount.setOnClickListener {
            model.categoryType.value = 1
        }


        view.categorySubmit.setOnClickListener {

            val name = view.categoryName.editText?.text.toString().trim()

            val category = CategoryCollective(categoryId = name, categoryType = categoryType, categoryName = name)

            val error = "Mandatory Field"
            if (name.isEmpty()) {
                view.categoryName.error = error
                return@setOnClickListener
            }

            model.liveProject.value?.let {
                viewModel.addCategory(it.projectId, category, this)

            }



        }
    }

    override fun categoryListener(category: CategoryCollective) {

        if(model.liveDestinationType.value == 2)
        {
            model.category.value = category
        }

        else
        {
            if(category.categoryType == 1)
            {
                model.account.value = category
            }

            if(category.categoryType == 2)
            {
                model.category.value = category
            }
        }


        model.category.value = category
        val action = AddCategoriesDirections.actionAddCategoriesToAddTransaction()
        navController.navigate(action)

    }


}
