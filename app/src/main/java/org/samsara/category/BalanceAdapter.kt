package org.samsara.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_balance.view.*
import org.samsara.R
import kotlin.math.round

class BalanceAdapter(var listener: CategoryListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var categoryList : List<CategoryCollective> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_balance, parent, false)
        return BalanceViewHolder(view)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val i = categoryList[position]
        val v = holder.itemView

        v.categoryName.text = i.categoryName
        v.categoryBalance.text = round(i.categoryBalance).toString()
        v.setOnClickListener {
            listener.categoryListener(i)
        }
    }

    fun submitData(list : List<CategoryCollective>){
        categoryList = list.filter {
            round(it.categoryBalance) > 0 || round(it.categoryBalance)<0
        }
    }

    class BalanceViewHolder(v: View): RecyclerView.ViewHolder(v)
}