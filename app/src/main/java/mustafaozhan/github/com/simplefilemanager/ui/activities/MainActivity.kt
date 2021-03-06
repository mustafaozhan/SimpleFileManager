package mustafaozhan.github.com.simplefilemanager.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import mustafaozhan.github.com.simplefilemanager.R
import mustafaozhan.github.com.simplefilemanager.ui.fragments.FileManagerFragment
import mustafaozhan.github.com.simplefilemanager.ui.fragments.MyPreferenceFragment


class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    companion object {
        val PERMISSIONS_REQUEST_CODE = 0

        private val FILE_MANAGER = "filemanager"
        private val PREFERENCE = "preference"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        checkPermissionsAndOpenFilePicker()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                if (supportFragmentManager.findFragmentById(R.id.myFrame) != null) {//checking if any fragment is open
                    supportFragmentManager
                            .beginTransaction().
                            remove(supportFragmentManager.findFragmentById(R.id.myFrame)).commit()
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.myFrame, MyPreferenceFragment(), PREFERENCE)//opening preference fragment
                        .commit()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissionsAndOpenFilePicker() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) { //checking if we  have permissions
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            openFileManager() //opening filemanager fragment
        }
    }

    private fun showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) { // geting result from permissinon request
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileManager()
                } else {
                    showError()
                }
            }
        }
    }

    private fun openFileManager() {
        fragmentManager.beginTransaction()
                .add(R.id.myFrame, FileManagerFragment(), FILE_MANAGER).commit()
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) { //this is for blocking quit from application with single pressing back
            super.onBackPressed()
            return
        }

        val myFragment = fragmentManager.findFragmentByTag(PREFERENCE)
        if (myFragment != null && myFragment.isVisible) {// if we are in preference fragment we need to close it and go back to file manager fragment

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.myFrame, FileManagerFragment(), FILE_MANAGER)
                    .commit()
            // openFileManager()
        } else {
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000) //limiting double check time
        }
    }


}
