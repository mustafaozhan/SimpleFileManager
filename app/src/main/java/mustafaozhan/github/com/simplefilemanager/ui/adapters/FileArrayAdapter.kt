package mustafaozhan.github.com.simplefilemanager.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.row.view.*
import mustafaozhan.github.com.simplefilemanager.model.Item


class FileArrayAdapter(private val c: Context, private val id: Int,
                       private val items: List<Item>) : ArrayAdapter<Item>(c, id, items) {
    override fun getItem(i: Int): Item? {
        return items[i]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val vi = c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(id, null)
        }


        val o = items[position]


        val uri = "drawable/" + o.image
        val imageResource = c.resources.getIdentifier(uri, null, c.packageName)
        val image = c.resources.getDrawable(imageResource)
        v!!.imgView.setImageDrawable(image)

        v.txtName.text = o.name
        v.txtItem.text = o.data
        v.txtDate.text = o.date


        return v
    }

}
