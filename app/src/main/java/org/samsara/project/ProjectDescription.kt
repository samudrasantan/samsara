package org.samsara.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.project_description.view.*
import org.samsara.MainViewModel
import org.samsara.R
import org.samsara.Storage


class ProjectDescription : Fragment() {

    private val model:MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.project_description, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.liveProjectDescription.observe(viewLifecycleOwner, Observer {
            it?.let { project ->

                view.desProjectId.text = project.projectId
                view.desProjectName.text = project.projectName
                Storage.auth.currentUser?.let {user->
                    view.desCreator. text = if(project.members[0] == user.uid) "Creator" else "Contributor"
                }

            }



        })
    }


}
