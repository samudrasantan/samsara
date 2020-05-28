package org.samsara.summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.balance_item.view.*
import org.samsara.R
import org.samsara.category.CategoryCollective
import kotlin.math.round

class SummaryAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var categoryList : List<CategoryCollective> = ArrayList()
    var due: Double = 0.00



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.balance_item, parent, false)
        return BalanceViewHolder(view)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = categoryList[position]
        val name = item.categoryName
        val spend = round(item.categoryBalance)
        val sumDue = round((due+item.categoryBalance))
        val progress = (((spend*-1)/due)*100).toInt()

        holder.itemView.sumName.text = name
        holder.itemView.sumSpent.text = spend.toString()
        holder.itemView.sumActual.text = if(item.categoryId == "total") "" else round(due).toString()
        holder.itemView.sumDue.text = if(item.categoryId == "total") "" else sumDue.toString()
        holder.itemView.progressBar.progress = progress






    }

    fun submitData(list : List<CategoryCollective>, d: Double){
        categoryList = list
        due = d
    }

    class BalanceViewHolder(v: View): RecyclerView.ViewHolder(v)
}