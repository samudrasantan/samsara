package org.samsara.summary

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.summary.view.*
import org.samsara.MainViewModel
import org.samsara.R
import org.samsara.category.CategoryCollective
import org.samsara.ui.home.HomeFragmentDirections

class Summary : Fragment() {

    private val model: MainViewModel by activityViewModels()
    private lateinit var summaryAdapter: SummaryAdapter
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    = inflater.inflate(R.layout.summary, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)




        summaryAdapter = SummaryAdapter()
        view.summeryRv.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = summaryAdapter
        }

        model.liveSummaryType.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it == 1)
                {
                    view.sumAccount.setBackgroundColor(Color.rgb(1,37,29))
                    view.sumAccount.setTextColor(Color.WHITE)

                    view.sumCategory.setBackgroundColor(Color.WHITE)
                    view.sumCategory.setTextColor(Color.rgb(1,37,29))

                    view.summeryRv.visibility = View.VISIBLE

                }

                if(it == 2)
                {
                    view.sumAccount.setBackgroundColor(Color.WHITE)
                    view.sumAccount.setTextColor(Color.rgb(1,37,29))

                    view.sumCategory.setBackgroundColor(Color.rgb(1,37,29))
                    view.sumCategory.setTextColor(Color.WHITE)


                    view.summeryRv.visibility = View.GONE

                }
            }


        })










        model.settingsData.observe(viewLifecycleOwner, Observer {set->
            set?.let {s->

                if(s.localProjectId == 0 && s.collectiveProjectId.isEmpty())
                {
                    val action = SummaryDirections.actionSummaryToAllProjects()
                    navController.navigate(action)
                }


                if(set.isLocalProject)

                {
                    view.summeryRv.adapter = null

                }


                else

                {

                    model.liveAllCategory.observe(viewLifecycleOwner, Observer {
                        it?.let {list->
                            var balance = 0.00
                            val accounts = list.filter { c->  c.categoryType == 1 }.toMutableList()
                            accounts.forEach {c->
                                balance+= c.categoryBalance
                            }

                            accounts.add(CategoryCollective("total", balance, "Total"))

                            summaryAdapter.submitData(accounts, (balance/(accounts.size-1))*-1)
                            view.summeryRv.swapAdapter(summaryAdapter, false)

                        }

                    })


                }


            }?:run{
                val action = SummaryDirections.actionSummaryToAllProjects()
                navController.navigate(action)
                // Log.d(Storage.log, "run")
            }

        })











        view.sumAccount.setOnClickListener {

            model.summaryType.value = 1
        }
        view.sumCategory.setOnClickListener {

            model.summaryType.value = 2
        }


    }


}
