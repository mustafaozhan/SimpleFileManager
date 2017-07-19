package mustafaozhan.github.com.simplefilemanager.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.row.view.*
import mustafaozhan.github.com.simplefilemanager.model.Item


class FileManagerAdapter(private val c: Context, private val id: Int,
                         private val items: List<Item>) : ArrayAdapter<Item>(c, id, items) {
    override fun getItem(i: Int): Item? {
        return items[i]
    }

    private var mSelection = HashMap<Int, Boolean>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val vi = c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = vi.inflate(id, null)
        }

        val o = items[position]
        if (mSelection[position] != null) {//highlighting according to selection
            view!!.setBackgroundColor(Color.GRAY)
        } else {
            view!!.setBackgroundColor(Color.TRANSPARENT)
        }

        val uri = "drawable/" + o.image
        val imageResource = c.resources.getIdentifier(uri, null, c.packageName)
        val image = c.resources.getDrawable(imageResource)
        view.imgView.setImageDrawable(image)

        view.txtName.text = o.name
        view.txtItem.text = o.data
        view.txtDate.text = o.date

        return view
    }
//functions above for cab selection defining and highlighting
    fun setNewSelection(position: Int, value: Boolean) {
        mSelection.put(position, value)
        notifyDataSetChanged()
    }

    fun isPositionChecked(position: Int): Boolean {
        val result = mSelection[position]
        return result ?: false
    }

    fun getCurrentCheckedPosition(): Set<Int> {
        return mSelection.keys
    }

    fun removeSelection(position: Int) {
        mSelection.remove(position)
        notifyDataSetChanged()
    }

    fun clearSelection() {
        mSelection = HashMap<Int, Boolean>()
        notifyDataSetChanged()
    }

}
