package alpizar.alfredo.familiarface

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.util.Log
import com.github.salomonbrys.kotson.get
import kotlinx.android.synthetic.main.activity_actor.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.URL
import android.os.Build
import com.google.gson.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import java.io.StringReader



class actor : AppCompatActivity() {
    private val someVariable= ArrayList<Pair<MutableList<String>, MutableList<Drawable>>>()
    val names: MutableList<String> = mutableListOf<String>()
    val drawables: MutableList<Drawable> = mutableListOf<Drawable>()
    val adapter = CustomGrid(this@actor, names, drawables)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor)
        val intent = getIntent()
        //UpdateInfoAsyncTask(intent.getStringExtra("id"), intent.getStringExtra("type"), intent.getStringExtra("name")).execute()
        //taskB(intent.getStringExtra("id"), intent.getStringExtra("type"), intent.getStringExtra("name")).execute()
            //--post GB use serial executor by default --
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            UpdateInfoAsyncTask(intent.getStringExtra("id"), intent.getStringExtra("type"), intent.getStringExtra("name")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            taskB(intent.getStringExtra("id"), intent.getStringExtra("type"), intent.getStringExtra("name")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }else{
            UpdateInfoAsyncTask(intent.getStringExtra("id"), intent.getStringExtra("type"), intent.getStringExtra("name")).execute()
            taskB(intent.getStringExtra("id"), intent.getStringExtra("type"), intent.getStringExtra("name")).execute()
        }
    }

    inner class UpdateInfoAsyncTask(internal var id: String, internal var type:String, internal var name:String) : AsyncTask<Void,Pair<String, Drawable> , Pair<MutableList<String>, MutableList<Drawable>>>() {

        fun getHalves(arg: JsonArray): Pair<MutableList<JsonElement>, MutableList<JsonElement>> {
            val half1= mutableListOf<JsonElement>()
            val half2= mutableListOf<JsonElement>()

            for ((index,element) in arg.withIndex()){
                val size=index<arg.size()
                if (index<arg.size()/2){
                    half1.add(element)
                }else half2.add(element)

            }

            return Pair(half1,half2)
        }

            // AsyncTasks executed one one thread in Honeycomb+ unless executed in thread pool manually

        override fun onPreExecute() {

            grid.adapter = adapter

        }

        
        override fun doInBackground(vararg params: Void): Pair<MutableList<String>, MutableList<Drawable>> {

            val client = OkHttpClient()
            val urlM = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.themoviedb.org")
                    .addPathSegments("/3/person/$id/movie_credits")
                    .addQueryParameter("api_key", "295842393e648bbfdc4b797075d713f5")
                    .build()

            val requestM = Request.Builder()
                    .url(urlM)
                    .build()



            val responseM = client.newCall(requestM).execute()

            val jsonM = responseM.body()!!.string()
            responseM.body()!!.close()

            val parser = JsonParser()

            val dataM = parser.parse(jsonM).asJsonObject
            for (char in dataM.getAsJsonArray("cast")){
                val charn=char["character"].asString
                val titlem=char["title"].asString
                //names.add(charn+"\n$titlem")

                val client = OkHttpClient()
                val url = HttpUrl.Builder()
                        .scheme("https")
                        .host("www.googleapis.com")
                        .addPathSegments("/customsearch/v1")
                        .addQueryParameter("q", "$name as $charn in $titlem")
                        .addQueryParameter("searchType", "image")
                        .addQueryParameter("cx", "003721926676443398853:l17jfrm7s48")
                        .addQueryParameter("num", "1")
                        .addQueryParameter("key","AIzaSyAo2-l-1NTM8Uj4GXndaWjk549oPkYb40o")
                        .build()

                val request = Request.Builder()
                        .url(url)
                        .build()
                val response = client.newCall(request).execute()
                val json = response.body()!!.string()
                //val parser = JsonParser()
                val data = parser.parse(json).asJsonObject
                val link=data["items"][0]["image"]["thumbnailLink"]

                if (link !is JsonNull && link.asString != null) {
                    val io = URL(link.asString).getContent() as InputStream
                    //drawables.add(Drawable.createFromStream(io, "src name"))
                    publishProgress(Pair(charn+"\n$titlem", Drawable.createFromStream(io, "src name")))

                } else publishProgress(Pair(charn+"\n$titlem", getResources().getDrawable(R.drawable.notavailable, null)))
                //drawables.add(getResources().getDrawable(R.drawable.notavailable, null))
            }
            return Pair(names, drawables)
        }


        override fun onProgressUpdate(vararg values: Pair<String, Drawable>) {
            Log.wtf("onProgressUpdate values", values.toList()[0].first)
            //Log.wtf("onProgressUpdate values", values.frist.toString())
            names.add(values.toList()[0].first)
            drawables.add(values.toList()[0].second)
            adapter.notifyDataSetChanged()


        }
/*
        override fun onPostExecute(Data: Pair<MutableList<String>,MutableList<Drawable>>) {

            someVariable.add(Data)
            //val adapter = CustomGrid(this@actor, Data.first, Data.second)
            //grid.setAdapter(adapter)
            Log.wtf("size",someVariable.size.toString())
            Log.wtf("end", "end1")
            if(someVariable.size==2)wwwwAWSSSSSSSSSSS{
                val namelist= mutableListOf<String>()
                val drawablelist= mutableListOf<Drawable>()
                namelist.addAll(someVariable[0].first)
                namelist.addAll(someVariable[1].first)
                drawablelist.addAll(someVariable[0].second)
                drawablelist.addAll(someVariable[1].second)

                val adapter = CustomGrid(this@actor, namelist, drawablelist)
                //grid.setAdapter(adapter)
            }


        }*/



    }

    inner class taskB(internal var id: String, internal var type:String, internal var name:String) : AsyncTask<Void, Void, Pair<MutableList<String>, MutableList<Drawable>>>() {

        fun getHalves(arg: JsonArray): Pair<MutableList<JsonElement>, MutableList<JsonElement>> {
            val half1= mutableListOf<JsonElement>()
            val half2= mutableListOf<JsonElement>()

            for ((index,element) in arg.withIndex()){
                val size=index<arg.size()
                if (index<arg.size()/2){
                    half1.add(element)
                }else half2.add(element)

            }
            print (half1.size)
            print(half2.size)
            return Pair(half1,half2)
        }

        // AsyncTasks executed one one thread in Honeycomb+ unless executed in thread pool manually



        override fun doInBackground(vararg params: Void): Pair<MutableList<String>, MutableList<Drawable>> {
            Log.wtf("start2", "start2")

            val client = OkHttpClient()
            val urlM = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.themoviedb.org")
                    .addPathSegments("/3/person/$id/tv_credits")
                    .addQueryParameter("api_key", "295842393e648bbfdc4b797075d713f5")
                    .build()

            val requestM = Request.Builder()
                    .url(urlM)
                    .build()



            val responseM = client.newCall(requestM).execute()

            val jsonM = responseM.body()!!.string()
            responseM.body()!!.close()

            val parser = JsonParser()

            val dataM = parser.parse(jsonM).asJsonObject




            /*for (element in data.getAsJsonArray("cast")) {

                if (element["profile_path"] !is JsonNull) {
                    val io = URL("http://image.tmdb.org/t/p/w185" + element["profile_path"].asString).getContent() as InputStream
                    drawables.add(Drawable.createFromStream(io, "src name"))
                } else drawables.add(getResources().getDrawable(R.drawable.notavailable, null))

                names.add(element["name"].asString+"\n"+element["character"].asString)
                ids.add(element["id"].asString)
            }*/
            val names: MutableList<String> = mutableListOf<String>()
            val drawables = mutableListOf<Drawable>()


            for (char in dataM.getAsJsonArray("cast")){
                val charn=char["character"].asString
                val titlem=char["name"].asString
                names.add(charn+"\n$titlem")

                val client = OkHttpClient()
                val url = HttpUrl.Builder()
                        .scheme("https")
                        .host("www.googleapis.com")
                        .addPathSegments("/customsearch/v1")
                        .addQueryParameter("q", "$name as $charn on $titlem")
                        .addQueryParameter("searchType", "image")
                        .addQueryParameter("cx", "003721926676443398853:l17jfrm7s48")
                        .addQueryParameter("num", "1")
                        .addQueryParameter("key","AIzaSyAo2-l-1NTM8Uj4GXndaWjk549oPkYb40o")
                        .build()

                val request = Request.Builder()
                        .url(url)
                        .build()
                val response = client.newCall(request).execute()
                val json = response.body()!!.string()
                //val parser = JsonParser()
                val data = parser.parse(json).asJsonObject

                val link=data["items"][0]["image"]["thumbnailLink"]

                if (link !is JsonNull && link.asString != null) {
                    val io = URL(link.asString).getContent() as InputStream
                    drawables.add(Drawable.createFromStream(io, "src name"))
                } else drawables.add(resources.getDrawable(R.drawable.notavailable, null))

                //val data = parser.parse(json).asJsonObject


            }
            return Pair(names, drawables)
        }

        override fun onPostExecute(Data: Pair<MutableList<String>,MutableList<Drawable>>) {

            //val adapter = CustomGrid(this@actor, Data.first, Data.second)
            //grid.setAdapter(adapter)

            Log.wtf("end", "end2")

            someVariable.add(Data)
            Log.wtf("size",someVariable.size.toString())


            if(someVariable.size==2){
                val namelist= mutableListOf<String>()
                val drawablelist= mutableListOf<Drawable>()
                namelist.addAll(someVariable[0].first)
                namelist.addAll(someVariable[1].first)
                drawablelist.addAll(someVariable[0].second)
                drawablelist.addAll(someVariable[1].second)

                val adapter = CustomGrid(this@actor, namelist, drawablelist)
                grid.setAdapter(adapter)
            }




        }
    }
}

