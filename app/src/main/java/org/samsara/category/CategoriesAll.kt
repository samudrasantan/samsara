package org.samsara.category

import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.all_category.view.*
import org.samsara.MainViewModel
import org.samsara.R
import org.samsara.Storage
import org.samsara.summary.SummaryDirections

class CategoriesAll : Fragment(), CategoryListener {




    private lateinit var galleryViewModel: CategoriesAllViewModel
    private val model:MainViewModel by activityViewModels()
    private lateinit var categoryAdapter:CategoryAdapter
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.all_category,container, false)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Storage.log, "All Categories")



        galleryViewModel = ViewModelProvider(this)[CategoriesAllViewModel::class.java]
        categoryAdapter = CategoryAdapter(this)
        navController = Navigation.findNavController(view)


        view.categoryAllRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }



        model.settingsData.observe(viewLifecycleOwner, Observer {set->
            set?.let {s->

                if(s.localProjectId == 0 && s.collectiveProjectId.isEmpty())
                {
                    // Log.d(Storage.log, "no local project")
                    val action = CategoriesAllDirections.actionAllCategoriesToAllProjects()
                    navController.navigate(action)
                }


                model.liveCategoryType.observe(viewLifecycleOwner, Observer { cat->


                    if(cat == 1)
                    {
                        view.categoryList.setBackgroundColor(Color.WHITE)
                        view.categoryList.setTextColor(Color.rgb(1,37,29))

                        view.accountList.setBackgroundColor(Color.rgb(1,37,29))
                        view.accountList.setTextColor(Color.WHITE)
                    }

                    if(cat == 2)
                    {
                        view.categoryList.setBackgroundColor(Color.rgb(1,37,29))
                        view.categoryList.setTextColor(Color.WHITE)

                        view.accountList.setBackgroundColor(Color.WHITE)
                        view.accountList.setTextColor(Color.rgb(1,37,29))

                    }

                    model.liveAllCategory.observe(viewLifecycleOwner, Observer {l->
                        l?.let {list->

                            val c = list.filter { it.categoryType == 2 }.toMutableList()
                            val a = list.filter { it.categoryType == 1 }.toMutableList()


                            if(cat == 1 && !set.isLocalProject)
                            {

                                if(a.isNotEmpty()) {

                                    a.add(CategoryCollective(categoryId = "addNew", categoryName = "Add New Account"))
                                    categoryAdapter.submitData(a)
                                    view.categoryAllRV.swapAdapter(categoryAdapter, false)

                                }


                                else

                                {

                                    //model.categoryType.value = 1
                                    val action = CategoriesAllDirections.actionAllCategoriesToAddCategories()
                                    navController.navigate(action)
                                }
                            }


                            else if(cat == 2 && !set.isLocalProject)
                            {

                                if(c.isNotEmpty())

                                {
                                    c.add(CategoryCollective(categoryId = "addNew", categoryName = "Add New Category"))
                                    categoryAdapter.submitData(c)
                                    view.categoryAllRV.swapAdapter(categoryAdapter, false)
                                }

                                else
                                {
                                    //model.categoryType.value = 2
                                    val action = CategoriesAllDirections.actionAllCategoriesToAddCategories()
                                    navController.navigate(action)
                                }

                            }


                            else
                            {
                                view.categoryAllRV.adapter = null
                            }






/*
                            model.liveCategoryType.observe(viewLifecycleOwner, Observer {i->
                                i?.let { bool->
                                    val c = list.filter { it.categoryType == 2 }.toMutableList()
                                    val a = list.filter { it.categoryType == 1 }.toMutableList()



                                    if(bool == 2){

                                        if(c.isNotEmpty())

                                        {
                                            c.add(CategoryCollective(categoryId = "addNew", categoryName = "Add New Category"))
                                            categoryAdapter.submitData(c)
                                            view.categoryAllRV.swapAdapter(categoryAdapter, false)
                                        }


                                        else
                                        {
                                            //model.categoryType.value = 2
                                            val action = CategoriesAllDirections.actionAllCategoriesToAddCategories()
                                            navController.navigate(action)
                                        }

                                    }




                                    if(bool == 1)
                                    {

                                        if(a.isNotEmpty()) {

                                            a.add(CategoryCollective(categoryId = "addNew", categoryName = "Add New Account"))
                                            categoryAdapter.submitData(a)
                                            view.categoryAllRV.swapAdapter(categoryAdapter, false)

                                        }


                                        else

                                        {

                                            //model.categoryType.value = 1
                                            val action = CategoriesAllDirections.actionAllCategoriesToAddCategories()
                                            navController.navigate(action)
                                        }
                                    }
                                }
                            })

 */
                        }
                    })


                })





            }?:run{
                val action = CategoriesAllDirections.actionAllCategoriesToAllProjects()
                navController.navigate(action)
                // Log.d(Storage.log, "run")
            }



        })



        view.categoryList.setOnClickListener {
            model.categoryType.value = 2
        }

        view.accountList.setOnClickListener {
            model.categoryType.value = 1
        }
    }

    override fun categoryListener(category: CategoryCollective) {

        if(category.categoryId == "addNew" )
        {
            val action = CategoriesAllDirections.actionAllCategoriesToAddCategories()
            navController.navigate(action)
        }

        else
        {

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

            val action = CategoriesAllDirections.actionAllCategoriesToAddTransaction()
            navController.navigate(action)
            model.destinationType.value = 0
        }

    }
}
