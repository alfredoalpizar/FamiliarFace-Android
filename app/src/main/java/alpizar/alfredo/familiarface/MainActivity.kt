package alpizar.alfredo.familiarface

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.text.*
import android.content.Intent
import android.view.View
import android.app.ActivityOptions


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        movieName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sButton.isEnabled = (radio_movie.isChecked || radio_show.isChecked) && !movieName.text.isEmpty()
            }
        })

        radioG?.setOnCheckedChangeListener { group, checkedId ->
            sButton.isEnabled = (radio_movie.isChecked || radio_show.isChecked) && !movieName.text.isEmpty()
        }


        sButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val myIntent = Intent(view.getContext(), movies::class.java)
                myIntent.putExtra("name", movieName.getText().toString())
                if(year.text.isEmpty())
                    myIntent.putExtra("year", "")
                else
                    myIntent.putExtra("year", year.text.toString())
                if (radio_movie.isChecked) myIntent.putExtra("type", "movie") else if (radio_show.isChecked) myIntent.putExtra("type", "tv")
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@MainActivity).toBundle())

            }

        });

    }
}
