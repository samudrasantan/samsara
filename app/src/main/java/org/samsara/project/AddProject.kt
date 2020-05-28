package org.samsara.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_project.view.*
import kotlinx.android.synthetic.main.collaborate.view.*
import org.samsara.R
import org.samsara.Storage
import org.samsara.category.CategoryCollective


class AddProject : Fragment(), ProjectAddListener {


    private lateinit var viewModel: ProjectViewModel
    private lateinit var navController: NavController
    private var requestCode = 1000
    private var log = Storage.log





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.add_project, container, false)





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        viewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        navController = Navigation.findNavController(view)





        val projectType = view.projectType?.editText
        projectType?.inputType = 0
        view.nickNameAdd.editText?.setText(Storage.auth.currentUser?.displayName.toString())

        val projectTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_selectable_list_item, resources.getStringArray(
            R.array.projectTypeArray
        ))
        (projectType as AutoCompleteTextView).setAdapter(projectTypeAdapter)






        view.projectSubmit.setOnClickListener{

            val projectNameText = view.projectName.editText?.text.toString().trim()
            val projectTypeText = projectType.text.toString().trim()
            val nickNameAdd = view.nickNameAdd.editText?.text.toString().trim()
            val error = "Mandatory Field"
            if(projectNameText.isEmpty())
            {
                view.projectName.error = error
                return@setOnClickListener
            }

            if(nickNameAdd.isEmpty())
            {
                view.nickNameAdd.error = error
                return@setOnClickListener
            }


            if(projectTypeText.isEmpty())
            {
                view.projectType.error = error
                return@setOnClickListener
            }

            val projectTypeInt = resources.getStringArray(R.array.projectTypeArray).indexOf(projectTypeText)


            Storage.auth.currentUser?.let {
                val x =  arrayListOf<String>()
                x.add(it.uid)
                val projectCollective = ProjectCollective(projectName = projectNameText, projectType = projectTypeInt,
                    members = x
                )
                when(projectTypeInt)
                {
                    0->Log.d(log, "Not ready Yet") //viewModel.insertProjects(project)
                    1-> viewModel.createProjectInFireStore(projectCollective, it, nickNameAdd, this)
                    else-> Log.d(log, "Not ready Yet"
                    )
                }

            }?: run {

                if(projectTypeInt == 1 || projectTypeInt == 2)
                {
                    Snackbar.make(requireView(), "Sign In to create $projectTypeInt project", Snackbar.LENGTH_INDEFINITE).setAction(

                        "Sign In", View.OnClickListener {
                            firebaseAuthUi()

                        }
                    ).show()

                }

                else
                {
                    Log.d(log, "Not ready Yet") //viewModel.insertProjects(project)
                }

            }

        }





        view.collaborateSubmit2.setOnClickListener{


            Storage.auth.currentUser?.let {


                val collabText = view.collab.editText?.text.toString().trim()
                val nickname = view.nickNameCollab.editText?.text.toString().trim()
                val error = "Mandatory Field"
                if (collabText.isEmpty()) {
                    view.collaborateId.error = error
                    return@setOnClickListener
                }

                if (nickname.isEmpty()) {
                    view.nickNameCollab.error = error
                    return@setOnClickListener
                }


                val collaborator = Collaborators(it.uid, it.displayName.toString())
                val category = CategoryCollective(nickname, 0.00, nickname, 1)
                viewModel.collaborate(collaborator, collabText, category, this)

            } ?:run{
                Snackbar.make(requireView(), "Sign In to collaborate in project", Snackbar.LENGTH_INDEFINITE).setAction(

                    "Sign In", View.OnClickListener {
                        firebaseAuthUi()


                    }
                ).show()


            }

        }






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








    override fun projectAdded(isProjectAdded: Boolean) {
        if(isProjectAdded) {
            val action = AddProjectDirections.actionAddProjectToAddTransaction()
            navController.navigate(action)
        }

    }


}
