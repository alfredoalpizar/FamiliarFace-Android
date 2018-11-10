package alpizar.alfredo.familiarface

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_movies.*
import okhttp3.*
import okhttp3.Request
import com.github.salomonbrys.kotson.*
import android.os.AsyncTask
import android.view.View
import org.json.JSONObject
import alpizar.alfredo.familiarface.movies.UpdateInfoAsyncTask
import android.app.ActivityOptions
import java.io.InputStream
import android.graphics.drawable.Drawable
import java.net.URL
import android.widget.Adapter
import android.widget.AdapterView
import com.google.gson.*
import android.util.Pair as UtilPair
import android.util.Log
import android.graphics.Bitmap
import alpizar.alfredo.familiarface.R.mipmap.ic_launcher
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import android.graphics.drawable.BitmapDrawable
import android.transition.Explode
import kotlinx.android.synthetic.main.grid_single.*
import android.transition.Fade




class movies : AppCompatActivity() {
    val titles: MutableList<String> = mutableListOf<String>()
    val drawables: MutableList<Drawable> = mutableListOf<Drawable>()
    val adapter = CustomGrid(this@movies, titles, drawables)
    val ids = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        with(window){
            val fade = Fade()
            fade.excludeTarget(android.R.id.statusBarBackground, true)
            fade.excludeTarget(android.R.id.navigationBarBackground, true)
            fade.excludeTarget(grid_image, true)
            fade.excludeTarget(grid_text, true)
            fade.excludeTarget(grid_char, true)
            exitTransition = fade
            enterTransition = fade
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)
        val intent = getIntent()
        grid.adapter = adapter


        UpdateInfoAsyncTask(intent.getStringExtra("name"), intent.getStringExtra("year"), intent.getStringExtra("type")).execute()

    }

    inner class UpdateInfoAsyncTask(internal var name: String, internal var year: String, internal var type: String) : AsyncTask<Void, Pair<Triple<String, Drawable,String>, String>, Pair<Triple<MutableList<String>, MutableList<Drawable>, MutableList<String>>, String>>() {


        override fun doInBackground(vararg params: Void): Pair<Triple<MutableList<String>, MutableList<Drawable>, MutableList<String>>, String> {
            val q = if (type == "movie") "primary_release_year" else "first_air_date_year"
            val client = OkHttpClient()
            val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.themoviedb.org")
                    .addPathSegments("/3/search/$type")
                    .addQueryParameter("api_key", "295842393e648bbfdc4b797075d713f5")
                    .addQueryParameter("query", name)
                    .addQueryParameter(q, year)
                    .build()

            val request = Request.Builder()
                    .url(url)
                    .build()

            val response = client.newCall(request).execute()

            val json = response.body()!!.string()
            val parser = JsonParser()
            val data = parser.parse(json).asJsonObject
            //val titles: MutableList<String> = mutableListOf()
            //val drawables = mutableListOf<Drawable>()
            //val ids = mutableListOf<String>()

            for (element in data.getAsJsonArray("results")) {
                if (type == "movie") {
                    if (element["poster_path"] !is JsonNull) {
                        val io = URL("http://image.tmdb.org/t/p/w185" + element["poster_path"].asString).getContent() as InputStream
                        publishProgress(Pair(Triple(element["title"].asString, Drawable.createFromStream(io, "src name"),element["id"].asString), type))
                    } else publishProgress(Pair(Triple(element["title"].asString, getResources().getDrawable(R.drawable.notavailable, null),element["id"].asString), type))

                }else {
                    if (element["poster_path"] !is JsonNull) {
                        val io = URL("http://image.tmdb.org/t/p/w185" + element["poster_path"].asString).getContent() as InputStream
                        publishProgress(Pair(Triple(element["name"].asString, Drawable.createFromStream(io, "src name"),element["id"].asString), type))
                    } else publishProgress(Pair(Triple(element["name"].asString, getResources().getDrawable(R.drawable.notavailable, null),element["id"].asString), type))
                }


            }
            return Pair(Triple(titles, drawables, ids), type)
        }

        override fun onProgressUpdate(vararg values: Pair<Triple<String, Drawable, String>, String>) {
            //Log.wtf("onProgressUpdate values", values.toList()[0].first)
            //Log.wtf("onProgressUpdate values", values.frist.toString())
            titles.add(values.toList()[0].first.first)
            drawables.add(values.toList()[0].first.second)
            ids.add(values.toList()[0].first.third)
            adapter.notifyDataSetChanged()
            grid.onItemClickListener = object : android.widget.AdapterView.OnItemClickListener {
                override fun onItemClick(parent: AdapterView<out Adapter?>?, view: View?, position: Int, id: Long) {
                    val bitmap = (drawables[position] as BitmapDrawable).bitmap

                    val myIntent = Intent(view?.context, cast::class.java)
                    myIntent.putExtra("id", ids[position])
                    myIntent.putExtra("type", type)
                    myIntent.putExtra("sharedElemPIC", bitmap)
                    myIntent.putExtra("sharedTitle",titles[position])

                    val options = ActivityOptions.makeSceneTransitionAnimation(this@movies, UtilPair.create(grid_image, "image_transition"),
                                    UtilPair.create(grid_text, "title_transition"),
                                    UtilPair.create(grid_char, "char_transition"))
                    startActivity(myIntent,options.toBundle())


                }
            }


        }


        /*override fun onPostExecute(Data: Pair<Triple<MutableList<String>, MutableList<Drawable>, MutableList<String>>, String>) {

            val adapter = CustomGrid(this@movies, Data.first.first, Data.first.second)
            grid.adapter = adapter



        }*/
    }
}
