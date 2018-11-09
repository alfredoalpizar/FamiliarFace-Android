package alpizar.alfredo.familiarface

/**
 * Created by alfredoalpizar on 11/16/17.
 */
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class CustomGrid(private val mContext: Context, private val title: MutableList<String>, private val imgs: MutableList<Drawable>) : BaseAdapter() {

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return title.size
    }

    override fun getItem(position: Int): Any? {
        // TODO Auto-generated method stub
        return null
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // TODO Auto-generated method stub
        var g: View
        val inflater = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            g = inflater.inflate(R.layout.grid_single, parent, false)

            //g = View(mContext)
            //g = inflater.inflate(R.layout.grid_single, null)

        } else {
            g = convertView
        }
        val textView = g.findViewById<View>(R.id.grid_text) as TextView
        val textView2 = g.findViewById<View>(R.id.grid_char) as TextView
        val imageView = g.findViewById<View>(R.id.grid_image) as ImageView
        imageView.setImageDrawable(imgs[position])
        if (title[position].split("\n").size>1){
            textView2.text = title[position].split("\n")[0]
            textView.text = title[position].split("\n")[1]
        } else {
            textView.text = title[position]
        }





        return g
    }
}