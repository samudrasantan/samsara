package org.samsara.project

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.all_projects.view.*
import org.samsara.MainViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.add_project.view.*
import kotlinx.android.synthetic.main.all_projects.*
import org.samsara.R
import org.samsara.Storage
import org.samsara.category.CategoryCollective
import org.samsara.settings.Settings

class AllProjects : Fragment(), ProjectListener {





    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var viewModel: AllProjectsViewModel
    private lateinit var navController: NavController
    private val model: MainViewModel by activityViewModels()
    private val requestCode = 1121
    private var log = ""






    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.all_projects, container, false)







    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Storage.log, "All Projects")






        model.settingsData.observe(viewLifecycleOwner, Observer {


            if(it.isLocalProject)
            {
                model.projectType.value = 0
                projectsRv.adapter = null
            }


            else {

                model.projectType.value = 1
                Storage.auth.currentUser?.let {user->

                    viewModel.projectList(user).observe(viewLifecycleOwner, Observer {i->

                    i?.let {list->

                        if(list.isEmpty()){
                            val action = AllProjectsDirections.actionAllProjectsToAddProject()
                            navController.navigate(action)
                        }

                        projectAdapter.submitData(list)
                        view.projectsRv.swapAdapter(projectAdapter, false)

                    }?:run{

                        //val action = AllProjectsDirections.actionAllProjectsToAddProject()
                       // navController.navigate(action)
                    }
                })

                }

            }


        })






        viewModel = ViewModelProvider(this)[AllProjectsViewModel::class.java]
        navController = Navigation.findNavController(view)
        projectAdapter = ProjectAdapter(this)



        model.liveProjectType.observe(viewLifecycleOwner, Observer {
            it?.let {projectType->

                when(projectType)
                {
                    2 -> {
                        view.projectsRv.visibility = View.GONE
                        view.projectsInfo.visibility = View.VISIBLE

                        view.projectsSmall.setBackgroundColor(Color.rgb(1,37,29))
                        view.projectsSmall.smallText.setTextColor(Color.WHITE)

                        view.projectsCollective.setBackgroundColor(Color.WHITE)
                        view.projectsCollective.collectiveText.setTextColor(Color.BLACK)

                        view.projectsLocal.setBackgroundColor(Color.WHITE)
                        view.projectsLocal.localText.setTextColor(Color.BLACK)
                    }

                    0 -> {
                        view.projectsRv.visibility = View.GONE
                        view.projectsInfo.visibility = View.VISIBLE

                        view.projectsSmall.setBackgroundColor(Color.WHITE)
                        view.projectsSmall.smallText.setTextColor(Color.BLACK)

                        view.projectsCollective.setBackgroundColor(Color.WHITE)
                        view.projectsCollective.collectiveText.setTextColor(Color.BLACK)

                        view.projectsLocal.setBackgroundColor(Color.rgb(1,37,29))
                        view.projectsLocal.localText.setTextColor(Color.WHITE)
                    }

                    else ->{
                        view.projectsRv.visibility = View.VISIBLE
                        view.projectsInfo.visibility = View.GONE


                        view.projectsSmall.setBackgroundColor(Color.WHITE)
                        view.projectsSmall.smallText.setTextColor(Color.BLACK)

                        view.projectsCollective.setBackgroundColor(Color.rgb(1,37,29))
                        view.projectsCollective.collectiveText.setTextColor(Color.WHITE)

                        view.projectsLocal.setBackgroundColor(Color.WHITE)
                        view.projectsLocal.localText.setTextColor(Color.BLACK)

                        Storage.auth.currentUser?.let {

                        }?:run{

                            Snackbar.make(requireView(), "You need to sign In to use this product", Snackbar.LENGTH_INDEFINITE).setAction(

                                "Sign In", View.OnClickListener {
                                    firebaseAuthUi()


                                }
                            ).show()
                        }
                    }
                }

            }

        })




        view.projectsLocal.setOnClickListener {

            model.projectType.value = 0
        }
        view.projectsCollective.setOnClickListener {

            model.projectType.value = 1
        }
        view.projectsSmall.setOnClickListener {

            model.projectType.value = 2
        }







        view.projectsRv.apply{
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = projectAdapter
        }







/*

        viewModel.projectList().observe(viewLifecycleOwner, Observer
        {i->

            //Log.d(Storage.log, i.toString())

            i?.let {

                if(it.isEmpty()){
                    val action = AllProjectsDirections.actionAllProjectsToAddProject()
                    navController.navigate(action)
                }

                projectAdapter.submitData(it)
                view.projectsRv.swapAdapter(projectAdapter, false)

            }?:run{

                val action = AllProjectsDirections.actionAllProjectsToAddProject()
                navController.navigate(action)
            }
        })

 */




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        // callbackManager.onActivityResult(requestCode, resultCode, data)

        if(requestCode == requestCode)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                //we can have either new user or old user, so we need to check that
                if(Storage.auth.currentUser?.metadata?.creationTimestamp ?: "" == Storage.auth.currentUser?.metadata?.lastSignInTimestamp ?: "")
                    log = "Welcome to Samsara"
                else
                    log = "Glad to have you back"

                Toast.makeText(requireContext(), log, Toast.LENGTH_SHORT).show()
                //startActivity(Intent(context, MainActivity::class.java))

            } // if result code

            else
            {
                if(IdpResponse.fromResultIntent(data) == null)
                {
                    log = "PLEASE COME BACK AGAIN"
                }

                else
                {
                    log = "Network Error   :: "+ IdpResponse.fromResultIntent(data)?.error ?: ""

                }

                Toast.makeText(requireContext(), log, Toast.LENGTH_SHORT).show()
            }

        } // if(requestCode == 1000)
        else
        {
            log = "Check Point 2  : LogInOrRegister.kt : Request Code Problem"
            Toast.makeText(requireContext(), log, Toast.LENGTH_SHORT).show()
        } //else
    } //fun onActivityResult

    private fun firebaseAuthUi()
    {
        val providers = arrayListOf<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build()//,
            // AuthUI.IdpConfig.GoogleBuilder().build(),
            //AuthUI.IdpConfig.PhoneBuilder().build(),
            // AuthUI.IdpConfig.FacebookBuilder().build()
        )

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTosAndPrivacyPolicyUrls("https://google.com", "https://google.com") //we need to update it later
            //.setLogo(R.drawable.bn_fill)
            //.setTheme(R.style.whiteTheme)
            .build()

        startActivityForResult(intent, requestCode)

    } //fun handleLoginRegisterButton



    override fun projectListener(project: ProjectCollective, type: Int) {

        if (type == 1) {
            val s = Settings(localProjectId = model.settingsData.value?.localProjectId?:0,collectiveProjectId = project.projectId, isLocalProject = false)
            viewModel.updateSetting(s)
            model.category.value = CategoryCollective()
            val action = AllProjectsDirections.actionAllProjectsToAddTransaction()
            navController.navigate(action)
        }
        else
        {

            model.projectDescription.value = project
            val action = AllProjectsDirections.actionAllProjectsToProjectDescription()
            navController.navigate(action)

        }

    }

}
