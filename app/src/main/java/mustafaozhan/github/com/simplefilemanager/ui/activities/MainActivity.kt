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

    var doubleBackToExitPressedOnce = false
    val FILE_MANAGER = "filemanager"
    val PREFERENCE = "preference"

    companion object {
        val PERMISSIONS_REQUEST_CODE = 0
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
                if (supportFragmentManager.findFragmentById(R.id.myFrame) != null) {
                    supportFragmentManager
                            .beginTransaction().
                            remove(supportFragmentManager.findFragmentById(R.id.myFrame)).commit()
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.myFrame, MyPreferenceFragment(), PREFERENCE)
                        .commit()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissionsAndOpenFilePicker() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf<String>(permission), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            openFileManager()
        }
    }

    private fun showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        val myFragment = fragmentManager.findFragmentByTag(PREFERENCE)
        if (myFragment != null && myFragment.isVisible) {

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.myFrame, FileManagerFragment(), FILE_MANAGER)
                    .commit()
            // openFileManager()
        } else {
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }
    }


}
