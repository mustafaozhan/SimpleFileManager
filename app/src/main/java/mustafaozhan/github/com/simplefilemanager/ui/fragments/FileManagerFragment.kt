package mustafaozhan.github.com.simplefilemanager.ui.fragments

import android.app.Fragment
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment_filemanager.*
import mustafaozhan.github.com.simplefilemanager.R
import mustafaozhan.github.com.simplefilemanager.model.Item
import mustafaozhan.github.com.simplefilemanager.ui.adapters.FileManagerAdapter
import mustafaozhan.github.com.simplefilemanager.util.FileOpen
import java.io.File
import java.sql.Date
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class FileManagerFragment : Fragment(), AbsListView.MultiChoiceModeListener {

    var toDelete: ArrayList<Item>? = null
    private var currentDir: File? = null
    var mListView: ListView? = null
    private var adapter: FileManagerAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_filemanager, container, false)

        bindViews(fragmentView)
        setHasOptionsMenu(true)
        PreferenceManager.setDefaultValues(activity, R.xml.fragment_preference, false)
        val appPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val PATH = appPreferences.getString("defaultFolder", "/sdcard/")//getting home directory if first time it will be "/sdcard/"
        currentDir = File(PATH)
        setUi(File(PATH))//setting user interface
        return fragmentView
    }

    private fun bindViews(view: View) {
        mListView = view.findViewById(R.id.myListView)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {

        mListView!!.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        mListView!!.setMultiChoiceModeListener(this)
        toDelete = ArrayList<Item>()

        myListView.setOnItemClickListener { adapterView, view, i, l ->

            val o = adapter!!.getItem(i)
            if (o!!.image.equals("directory_icon", ignoreCase = true) || o.image.equals("directory_up", ignoreCase = true)) {//if it is a file it will not change path
                currentDir = File(o.path)//changing path according to clicked item
                setUi(currentDir!!)//refreshing user interface
                animate()
            } else
                FileOpen.openFile(activity, File(o.path))// if it is a file opening file
        }

    }


    fun setUi(file: File) {

        val dirs = file.listFiles()

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
        Collections.sort(dir)//firstly directories
        Collections.sort(fls)//than files
        dir.addAll(fls)
        if (!file.name.equals("sdcard", ignoreCase = true))
            dir.add(0, Item("build/generated/source/aidl/androidTest", "Parent Directory", "", file.parent, "directory_up"))
        adapter = FileManagerAdapter(activity, R.layout.row, dir)
        mListView!!.adapter = adapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sync -> {
                setUi(currentDir!!)//refreshing user interface
                animate()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode!!.title = "Select Items"
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menuItem: MenuItem?): Boolean {
        when (menuItem!!.itemId) {
            R.id.action_delete -> {
//                for (item in toDelete!!) {
//                    //listView.remove(item)
//                }
                if (actionMode != null)
                    actionMode.finish()
                adapter!!.clearSelection()
                return true
            }
            else ->
                return true

        }
    }

    override fun onItemCheckedStateChanged(actionMode: ActionMode?, position: Int, id: Long, checked: Boolean) {
        if (checked) {
            toDelete!!.add(adapter?.getItem(position)!!)
            adapter!!.setNewSelection(position, checked)

        } else {
            toDelete!!.remove(adapter!!.getItem(position))
            adapter!!.removeSelection(position)

        }
        adapter!!.notifyDataSetChanged()
        val checkedItems = mListView!!.checkedItemCount
        actionMode!!.title = checkedItems.toString() + " Selected" //showing how many items checked
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        val menuInflater = activity.menuInflater
        menuInflater.inflate(R.menu.toolbar_cab, menu)
        return true
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        toDelete!!.clear()
        adapter!!.clearSelection()
    }

    fun animate() {//custom animation method
        val animation = AnimationUtils.loadAnimation(activity,
                R.anim.myanimation)
        view!!.startAnimation(animation)
    }

}
