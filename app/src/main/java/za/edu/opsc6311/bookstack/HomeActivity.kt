package za.edu.opsc6311.bookstack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var booksAdapter: BooksAdapter
    private lateinit var addBookBtn: Button
    private lateinit var txtViewCate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        addBookBtn = findViewById(R.id.buttonAdd)
        auth = Firebase.auth
        recyclerView = findViewById(R.id.recyclerView)

        // Initialize RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        booksAdapter = BooksAdapter(emptyList())
        recyclerView.adapter = booksAdapter

        // Retrieve books from Firebase Realtime Database
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().reference.child("books").child(userId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val booksList = mutableListOf<Pair<String, String>>()
                    for (bookSnapshot in snapshot.children) {
                        val book = bookSnapshot.getValue(Book::class.java)
                        book?.let {
                            // Add book name and image URL to the list
                            booksList.add(Pair(it.name, it.imageUrl))
                        }
                    }
                    // Update RecyclerView with the retrieved books
                    booksAdapter.updateData(booksList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
        }

        txtViewCate = findViewById<TextView>(R.id.textViewCategories)
        txtViewCate.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }
        addBookBtn.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }


    }
}
