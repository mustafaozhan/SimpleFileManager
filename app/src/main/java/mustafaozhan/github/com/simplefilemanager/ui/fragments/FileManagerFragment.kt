package mustafaozhan.github.com.simplefilemanager.ui.fragments

import android.app.ListFragment
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ListView
import mustafaozhan.github.com.simplefilemanager.R
import mustafaozhan.github.com.simplefilemanager.model.Item
import mustafaozhan.github.com.simplefilemanager.ui.adapters.FileManagerAdapter
import mustafaozhan.github.com.simplefilemanager.util.FileOpen
import java.io.File
import java.sql.Date
import java.text.DateFormat
import java.util.*
import android.content.SharedPreferences




class FileManagerFragment : ListFragment() {


    private var currentDir: File? = null
    private var adapter: FileManagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        PreferenceManager.setDefaultValues(activity, R.xml.fragment_preference, false)


        val appPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val PATH = appPreferences.getString("defaultFolder", "/sdcard/")


        setUi(File(PATH))


    }


    fun setUi(f: File) {

        val dirs = f.listFiles()

        val dir = ArrayList<Item>()
        val fls = ArrayList<Item>()
        try {
            for (ff in dirs) {
                val lastModDate = Date(ff.lastModified())
                val formater = DateFormat.getDateTimeInstance()
                val date_modify = formater.format(lastModDate)
                if (ff.isDirectory) {

                    val fbuf = ff.listFiles()
                    var buf = 0
                    if (fbuf != null) {
                        buf = fbuf.size
                    } else
                        buf = 0
                    var num_item = buf.toString()
                    if (buf == 0)
                        num_item += " item"
                    else
                        num_item += " items"

                    dir.add(Item(ff.name, num_item, date_modify, ff.absolutePath, "directory_icon"))
                } else
                    fls.add(Item(ff.name, ff.length().toString() + " Byte", date_modify, ff.absolutePath, "file_icon"))

            }
        } catch (e: Exception) {

        }

        Collections.sort(dir)
        Collections.sort(fls)
        dir.addAll(fls)
        if (!f.name.equals("sdcard", ignoreCase = true))
            dir.add(0, Item("build/generated/source/aidl/androidTest", "Parent Directory", "", f.parent, "directory_up"))
        adapter = FileManagerAdapter(activity, R.layout.row, dir)

        this.listAdapter = adapter

    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {

        super.onListItemClick(l, v, position, id)
        val o = adapter!!.getItem(position)
        if (o!!.image.equals("directory_icon", ignoreCase = true) || o.image.equals("directory_up", ignoreCase = true)) {
            currentDir = File(o.path)

            setUi(currentDir!!)
            animate()
        } else
            FileOpen.openFile(activity, File(o.path))

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sync -> {
                setUi(currentDir!!)
                animate()
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }

    fun animate() {
        val animation = AnimationUtils.loadAnimation(activity,
                R.anim.myanimation)
        view!!.startAnimation(animation)
    }

}
