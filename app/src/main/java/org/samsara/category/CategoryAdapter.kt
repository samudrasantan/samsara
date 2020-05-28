package org.samsara.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_list.view.*
import kotlinx.android.synthetic.main.project_list.view.*
import org.samsara.R
import org.samsara.Storage

class CategoryAdapter(var listener: CategoryListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{



    private var categoryList : List<CategoryCollective> = ArrayList()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_list, parent, false)
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.categoryText.text = categoryList[position].categoryName

        holder.itemView.setOnClickListener {
            listener.categoryListener(categoryList[position])
        }
    }

    fun submitData(list : List<CategoryCollective>){
        categoryList = list
    }

    class ProjectViewHolder(v: View): RecyclerView.ViewHolder(v)
}