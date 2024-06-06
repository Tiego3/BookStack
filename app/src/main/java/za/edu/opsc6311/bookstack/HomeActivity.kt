package za.edu.opsc6311.bookstack

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("MyBooks", MODE_PRIVATE)
        val savedBooks = retrieveSavedBooks()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = BooksAdapter(savedBooks)

        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        val textView = findViewById<TextView>(R.id.textViewCategories)
        textView.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveSavedBooks(): List<Pair<String, String>> {
        val savedImagesSet = sharedPreferences.getStringSet("book", setOf()) ?: setOf()
        return savedImagesSet.map { imageString ->
            val parts = imageString.split("||")
            val imageName = parts[0]
            val imageUrl = parts[1]
            Pair(imageName, imageUrl)
        }
    }

}
