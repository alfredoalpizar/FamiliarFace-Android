package alpizar.alfredo.familiarface

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import com.github.salomonbrys.kotson.get
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_cast.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.URL
import android.graphics.Bitmap
import android.os.Parcelable
import android.transition.Fade
import kotlinx.android.synthetic.main.grid_single.*


class cast : AppCompatActivity() {



    val names: MutableList<String> = mutableListOf<String>()
    val ids = mutableListOf<String>()
    val drawables = mutableListOf<Drawable>()
    val adapter = CustomGrid(this@cast, names, drawables)
    override fun onCreate(savedInstanceState: Bundle?) {
        with(window){
            val fade = Fade()
            fade.excludeTarget(android.R.id.statusBarBackground, true)
            fade.excludeTarget(android.R.id.navigationBarBackground, true)
            fade.excludeTarget(shared_image, true)
            fade.excludeTarget(shared_title, true)
            fade.excludeTarget(shared_char, true)


            exitTransition = fade
            enterTransition = fade
        }
        val bitmap = intent.getParcelableExtra<Parcelable>("sharedElemPIC") as Bitmap
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cast)

        shared_image.setImageBitmap(bitmap)
        shared_title.setText(intent.getStringExtra("sharedTitle"))
        grid.adapter = adapter

        UpdateInfoAsyncTask(intent.getStringExtra("id"), intent.getStringExtra("type")).execute()
    }

    inner class UpdateInfoAsyncTask(internal var id: String, internal var type:String) : AsyncTask<Void, Pair<Triple<String,Drawable,String>,String>, Void>() {


        override fun doInBackground(vararg params: Void): Void? {
            val client = OkHttpClient()
            val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.themoviedb.org")
                    .addPathSegments("/3/$type/$id/credits")
                    .addQueryParameter("api_key", "295842393e648bbfdc4b797075d713f5")
                    .build()

            val request = Request.Builder()
                    .url(url)
                    .build()

            val response = client.newCall(request).execute()

            val json = response.body()!!.string()
            val parser = JsonParser()
            val data = parser.parse(json).asJsonObject
            //val names: MutableList<String> = mutableListOf<String>()
            //val ids = mutableListOf<String>()
            //val drawables = mutableListOf<Drawable>()

            for (element in data.getAsJsonArray("cast")) {

                if (element["profile_path"] !is JsonNull) {
                    val io = URL("http://image.tmdb.org/t/p/w185" + element["profile_path"].asString).getContent() as InputStream
                    publishProgress(Pair(Triple(element["name"].asString+"\n"+element["character"].asString, Drawable.createFromStream(io, "src name"),element["id"].asString), type))
                } else publishProgress(Pair(Triple(element["name"].asString+"\n"+element["character"].asString, getResources().getDrawable(R.drawable.notavailable, null),element["id"].asString), type))

            }
            return null
        }

        override fun onProgressUpdate(vararg values: Pair<Triple<String,Drawable,String>,String>) {

           // val adapter = CustomGrid(this@cast, Data.first, Data.second)
           // grid.adapter = adapter
            names.add(values.toList()[0].first.first)
            drawables.add(values.toList()[0].first.second)
            ids.add(values.toList()[0].first.third)
            adapter.notifyDataSetChanged()
            grid.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val myIntent = Intent(view?.getContext(), actor::class.java)
                myIntent.putExtra("id",ids[position])
                myIntent.putExtra("type",type)
                myIntent.putExtra("name",names[position].split("\n")[0])
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@cast).toBundle())
            }


        }
    }
}
