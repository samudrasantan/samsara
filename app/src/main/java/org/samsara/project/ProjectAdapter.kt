package org.samsara.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_project.view.*
import kotlinx.android.synthetic.main.project_list.view.*
import org.samsara.R
import org.samsara.Storage

class ProjectAdapter(var listener: ProjectListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{



    private var projectList : List<ProjectCollective> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_list, parent, false)
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = projectList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.projectNameText.text = projectList[position].projectName
        holder.itemView.projectTypeText.text = if(projectList[position].members[0] == Storage.auth.currentUser?.uid) "Creator" else "collaborator"

        holder.itemView.projectSelect.setOnClickListener {
            listener.projectListener(projectList [position], 1)
        }

        holder.itemView.projectEdit.setOnClickListener {
            listener.projectListener(projectList [position], 2)
        }
    }

    fun submitData(list : List<ProjectCollective>){
        projectList = list
    }


    class ProjectViewHolder(v: View): RecyclerView.ViewHolder(v)


}