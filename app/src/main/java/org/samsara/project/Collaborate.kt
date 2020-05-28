package org.samsara.project

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.collaborate.view.*

import org.samsara.R
import org.samsara.Storage

class Collaborate : Fragment() {



    private lateinit var viewModel: CollaborateViewModel

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle? ): View?
       = inflater.inflate(R.layout.collaborate, container, false)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CollaborateViewModel::class.java]


        view.collaborateSubmit.setOnClickListener{


            val collabText = view.collaborateId.editText?.text.toString().trim()
            val error = "Mandatory Field"
            if(collabText.isEmpty())
            {
                view.collaborateId.error = error
                return@setOnClickListener
            }

            Storage.auth.currentUser?.let {
                val collaborator = Collaborators(it.uid, it.displayName.toString())
                //viewModel.collaborate(collaborator, collabText)

            }

        }
    }

}
