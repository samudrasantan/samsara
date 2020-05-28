package org.samsara.reports

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.report_category.*
import kotlinx.android.synthetic.main.report_category.view.*
import org.samsara.MainViewModel
import org.samsara.R
import org.samsara.Storage
import kotlin.math.round


class ReportCategory : Fragment() {

    private val model: MainViewModel by activityViewModels()
    private lateinit var viewModel: ReportViewModel
    private lateinit var adapter:ReportAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.report_category, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ReportViewModel::class.java]
        adapter = ReportAdapter()

        reportRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter
        }


        model.liveReportCategory.observe(viewLifecycleOwner, Observer {
            it?.let {c->

                //Log.d(Storage.log, c.toString())

                view.reportCategoryName.text = c.categoryName
                view.reportBalance.text = round(c.categoryBalance).toString()

                Storage.auth.currentUser?.let {
                    model.liveProject.value?.let {p->
                    viewModel.categoryDetails(p.projectId, c.categoryId, c.categoryType).observe(viewLifecycleOwner, Observer {l->
                        l.let {list->

                            adapter.submitData(list, c.categoryType)
                            reportRv.swapAdapter(adapter, false)

                        }

                    })

                    }

                }

            }

        })
    }

}
