package mustafaozhan.github.com.simplefilemanager.ui.fragments

import android.app.AlertDialog
import android.app.Fragment
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.GridView
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment_filemanager.*
import mustafaozhan.github.com.simplefilemanager.R
import mustafaozhan.github.com.simplefilemanager.model.Item
import mustafaozhan.github.com.simplefilemanager.ui.adapters.FileManagerAdapter
import mustafaozhan.github.com.simplefilemanager.util.FileOpen
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class FileManagerFragment : Fragment(), AbsListView.MultiChoiceModeListener {

    private var currentDir: File? = null
    val removeList = ArrayList<String>()
    var myGridView: GridView? = null
    private var adapter: FileManagerAdapter? = null
    var fistTime: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_filemanager, container, false)

        bindViews(fragmentView)
        setHasOptionsMenu(true)
        PreferenceManager.setDefaultValues(activity, R.xml.fragment_preference, false)
        val appPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val PATH = appPreferences.getString("defaultFolder", "/sdcard/")//getting home directory if first time it will be "/sdcard/"

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) //fetching orientation and configuring grid acording to it
            myGridView!!.numColumns = 1
        else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            myGridView!!.numColumns = 3

        currentDir = File(PATH)
        if (fistTime) {//only startup ui is setting by separate thread
            Thread().run {
                setUi(File(PATH))
                fistTime = false
            }
        } else
            setUi(File(PATH))//setting user interface
        return fragmentView
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {//if app orientation change running time setting our grid dynamically
        super.onConfigurationChanged(newConfig)
        if (newConfig!!.orientation == Configuration.ORIENTATION_PORTRAIT)
            myGridView!!.numColumns = 1
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            myGridView!!.numColumns = 3
    }

    private fun bindViews(view: View) {
        myGridView = view.findViewById(R.id.myListView)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        myGridView!!.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        myGridView!!.setMultiChoiceModeListener(this)
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

                    dir.add(Item(ff.name, num_item, ff.absolutePath, "directory_icon"))
                } else
                    fls.add(Item(ff.name, ff.length().toString() + " Byte", ff.absolutePath, "file_icon"))
            }
        } catch (e: Exception) {

        }
        Collections.sort(dir)//firstly directories
        Collections.sort(fls)//than files
        dir.addAll(fls)
        if (!file.name.equals("sdcard", ignoreCase = true))
            dir.add(0, Item("build/generated/source/aidl/androidTest", "Parent Directory", file.parent, "directory_up"))
        adapter = FileManagerAdapter(activity, R.layout.row, dir)
        myGridView!!.adapter = adapter

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


                val builder: AlertDialog.Builder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    builder = AlertDialog.Builder(activity)
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, { dialog, which ->
                            actionMode?.finish()
                            adapter!!.clearSelection()
                            for (i in 0..removeList.size - 1)
                                deleteRecursive(File(removeList[i]))

                            setUi(currentDir!!)
                            animate()
                            adapter!!.notifyDataSetChanged()
                        })
                        .setNegativeButton(android.R.string.no, { dialog, which ->

                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()









                return true
            }
            else -> return true

        }
    }

    override fun onItemCheckedStateChanged(actionMode: ActionMode?, position: Int, id: Long, checked: Boolean) {
        var checkedItems = myGridView!!.checkedItemCount

        if (checked) {
            if (adapter!!.getItem(position)!!.image.equals("directory_up", ignoreCase = true)) {//not tab select for move up item
                checkedItems--
                if (checkedItems == 0) {
                    adapter!!.clearSelection()
                    actionMode!!.finish()
                }

            } else {
                adapter!!.setNewSelection(position, checked)
                val temp_path = adapter!!.getItem(position)!!.path
                removeList.add(temp_path)

            }

        } else {
            val temp_path = adapter!!.getItem(position)!!.path
            removeList.remove(temp_path)
        }
        adapter!!.notifyDataSetChanged()
        actionMode!!.title = checkedItems.toString() + " Selected" //showing how many items checked
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        val menuInflater = activity.menuInflater
        menuInflater.inflate(R.menu.toolbar_cab, menu)
        return true
    }

    override fun onDestroyActionMode(p0: ActionMode?) {

        adapter!!.clearSelection()
    }

    fun animate() {//custom animation method
        val animation = AnimationUtils.loadAnimation(activity,
                R.anim.myanimation)
        view!!.startAnimation(animation)
    }

    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()!!)
                deleteRecursive(child)
        fileOrDirectory.delete()
    }

}
