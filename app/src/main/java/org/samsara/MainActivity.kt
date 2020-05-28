package org.samsara

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.samsara.category.CategoryCollective
import org.samsara.project.ProjectCollective
import org.samsara.settings.Settings

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavController
    private val requestCode = 1000
    private var log = "LOG"

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.insertSetting(Settings())
        viewModel.categoryType.value = 2
        viewModel.transactionType.value = 1
        viewModel.destinationType.value = 0
        viewModel.summaryType.value = 1









        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val drawer: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_slideshow), drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        drawer.setupWithNavController(navController)





        viewModel.settingsData.observe(this, Observer {s->
          s?.let{set->

              if(set.isLocalProject)
              {
                  drawer.getHeaderView(0).currentProjectName.text = ""
              }

              else
              {

                  if(set.collectiveProjectId.isNotEmpty())
                  {
                      viewModel.currentProject(set.collectiveProjectId).observe(this, Observer {p->
                          p?.let {
                                  viewModel.currentProject.value = p
                                  drawer.getHeaderView(0).currentProjectName.text = p.projectName

                                  findAllFireStoreList()
                          }
                      })
                  }
              }

          }

        })


















    }









    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
       // Log.d("LOG", "menu created")
        menuInflater.inflate(R.menu.main, menu)

        return true
    }








    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        if(auth.currentUser == null)
        {
            menu?.findItem(R.id.menuLogOut)?.isVisible = false
            menu?.findItem(R.id.menuLogIn)?.isVisible = true
            //Log.d("LOG", menu?.getItem(0)?.isEnabled.toString())
        }

        else
        {
            menu?.findItem(R.id.menuLogOut)?.isVisible = true
            menu?.findItem(R.id.menuLogIn)?.isVisible = false
        }

        return true
    }






    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.menuLogIn -> firebaseAuthUi()
            R.id.menuLogOut -> AuthUI.getInstance().signOut(this)
        }
        return true
    }








    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }






    override fun onAuthStateChanged(p0: FirebaseAuth) {
        this@MainActivity.invalidateOptionsMenu()
        Storage.auth = p0
        if(p0.currentUser == null){
            viewModel.settingsData.value?.let { set->

                viewModel.updateSetting(Settings(1, set.localProjectId,set.collectiveProjectId,true))
            }
        }

        else{

            viewModel.settingsData.value?.let {set->
                viewModel.updateSetting(Settings(1, set.localProjectId,set.collectiveProjectId,false))
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
                if(auth.currentUser?.metadata?.creationTimestamp ?: "" == auth.currentUser?.metadata?.lastSignInTimestamp ?: "")
                    log = "Welcome to Samsara"
                else
                    log = "Glad to have you back"

                Toast.makeText(this, log, Toast.LENGTH_SHORT).show()
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
                    log = "Network Error   : "+ IdpResponse.fromResultIntent(data)?.error

                }

                Toast.makeText(this, log, Toast.LENGTH_SHORT).show()
            }

        } // if(requestCode == 1000)
        else
        {
            log = "Check Point 2  : LogInOrRegister.kt : Request Code Problem"
            Toast.makeText(this, log, Toast.LENGTH_SHORT).show()
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






    private fun findAllFireStoreList()
    {

        viewModel.liveProject.observe(this, Observer {p->
            p?.let {  project->

                if (project.projectType == 0)
                {

                }

                else{

                    Storage.auth.currentUser?.let {user->
                        viewModel.myAccount(user, project).observe(this, Observer {cat->
                            cat?.let {
                            //Log.d(Storage.log, cat.toString())
                            viewModel.account.value = it
                            }
                        })
                    }

                    viewModel.allCategory(project).observe(this, Observer {l->
                        l?.let {list->
                            viewModel.allCategory.value = list

                            val v = list.filter {c-> c.categoryType == 2 }
                            if(v.isNotEmpty()) {
                                viewModel.category.value = v[0]
                            }

                            else
                            {
                                viewModel.category.value = CategoryCollective()
                            }
                        }


                    })

                }

            }

        })

    }

}
