package org.samsara.reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_transaction.view.*
import org.samsara.R
import org.samsara.transaction.Transaction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReportAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{



    private var transactionList : List<Transaction> = ArrayList()
    private var type: Int = 0



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_transaction, parent, false)
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = transactionList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val v = holder.itemView
        val i = transactionList[position]

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val formattedDate = dateFormat.format(i.transactionDate.toDate())

        v.reverse.text = if (type == 1 )i.transactionCategory  else i.transactionAccount
        v.reportDate.text = formattedDate
        v.reportAmount.text = i.transactionAmount.toString()

    }

    fun submitData(list : List<Transaction>,  t: Int){
        transactionList = list
        type = t
    }

    class ProjectViewHolder(v: View): RecyclerView.ViewHolder(v)
}