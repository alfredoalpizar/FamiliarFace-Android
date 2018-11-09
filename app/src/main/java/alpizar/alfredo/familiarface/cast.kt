package alpizar.alfredo.familiarface

import android.app.ActivityOptions
import android.content.Intent
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

class cast : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cast)
        val intent = getIntent()
        UpdateInfoAsyncTask(intent.getStringExtra("id"), intent.getStringExtra("type")).execute()
    }

    inner class UpdateInfoAsyncTask(internal var id: String, internal var type:String) : AsyncTask<Void, Void, Triple<MutableList<String>,MutableList<Drawable>, Pair<MutableList<String>,String>>>() {


        override fun doInBackground(vararg params: Void): Triple<MutableList<String>,MutableList<Drawable>, Pair<MutableList<String>,String>>? {
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
            val names: MutableList<String> = mutableListOf<String>()
            val ids = mutableListOf<String>()
            val drawables = mutableListOf<Drawable>()

            for (element in data.getAsJsonArray("cast")) {

                if (element["profile_path"] !is JsonNull) {
                    val io = URL("http://image.tmdb.org/t/p/w185" + element["profile_path"].asString).getContent() as InputStream
                    drawables.add(Drawable.createFromStream(io, "src name"))
                } else drawables.add(getResources().getDrawable(R.drawable.notavailable, null))

                names.add(element["name"].asString+"\n"+element["character"].asString)
                ids.add(element["id"].asString)
            }
            return Triple(names, drawables, Pair(ids,type))
        }

        override fun onPostExecute(Data: Triple<MutableList<String>,MutableList<Drawable>, Pair<MutableList<String>,String>>) {

            val adapter = CustomGrid(this@cast, Data.first, Data.second)
            grid.adapter = adapter
            grid.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val myIntent = Intent(view?.getContext(), actor::class.java)
                myIntent.putExtra("id",Data.third.first[position])
                myIntent.putExtra("type",Data.third.second)
                myIntent.putExtra("name",Data.first[position].split("\n")[0])
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@cast).toBundle())
            }


        }
    }
}
