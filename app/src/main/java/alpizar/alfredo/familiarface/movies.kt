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
import android.util.Log
class movies : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)
        val intent = getIntent()

        UpdateInfoAsyncTask(intent.getStringExtra("name"), intent.getStringExtra("year"), intent.getStringExtra("type")).execute()

    }

    inner class UpdateInfoAsyncTask(internal var name: String, internal var year: String, internal var type: String) : AsyncTask<Void, Void, Pair<Triple<MutableList<String>, MutableList<Drawable>, MutableList<String>>, String>>() {


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
            val titles: MutableList<String> = mutableListOf()
            val drawables = mutableListOf<Drawable>()
            val ids = mutableListOf<String>()

            for (element in data.getAsJsonArray("results")) {
                if (type == "movie") titles.add(element["title"].asString) else titles.add(element["name"].asString)

                if (element["poster_path"] !is JsonNull) {
                    val io = URL("http://image.tmdb.org/t/p/w185" + element["poster_path"].asString).getContent() as InputStream
                    drawables.add(Drawable.createFromStream(io, "src name"))
                } else drawables.add(getResources().getDrawable(R.drawable.notavailable, null))

                ids.add(element["id"].asString)

            }
            return Pair(Triple(titles, drawables, ids), type)
        }

        override fun onPostExecute(Data: Pair<Triple<MutableList<String>, MutableList<Drawable>, MutableList<String>>, String>) {

            val adapter = CustomGrid(this@movies, Data.first.first, Data.first.second)
            grid.adapter = adapter
            grid.onItemClickListener = object : android.widget.AdapterView.OnItemClickListener {
                override fun onItemClick(parent: AdapterView<out Adapter?>?, view: View?, position: Int, id: Long) {

                    val myIntent = Intent(view?.context, cast::class.java)
                    myIntent.putExtra("id",Data.first.third[position])
                    myIntent.putExtra("type", Data.second)
                    startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@movies).toBundle())


                }
            }


        }
    }
}
