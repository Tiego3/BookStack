package za.edu.opsc6311.bookstack

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class AddCategoryActivity : AppCompatActivity() {

    private val SELECT_IMAGE_REQUEST = 1
    private lateinit var sharedPreferences: SharedPreferences
    private var imageUri: Uri? = null
    private lateinit var catEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var otherCategoryEdit: EditText
    private lateinit var setGoalBtn: Button
    private lateinit var goalEditText: EditText
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcategory)

        sharedPreferences = getSharedPreferences("Category", MODE_PRIVATE)
        catEditText = findViewById(R.id.otherCategoryEdit)
        categorySpinner = findViewById(R.id.categorySpinner)
        otherCategoryEdit = findViewById(R.id.otherCategoryEdit)
        setGoalBtn = findViewById(R.id.setGoalBtn)
        goalEditText = findViewById(R.id.goalEditText)

        // Set up spinner item selected listener
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                if (selectedCategory == "Custom") {
                    otherCategoryEdit.visibility = View.VISIBLE
                } else {
                    otherCategoryEdit.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        setGoalBtn.setOnClickListener {
            goalEditText.visibility = View.VISIBLE
        }

        val selectImageButton = findViewById<Button>(R.id.selectCategoryImage)
        val saveButton = findViewById<Button>(R.id.saveCatBtn)


        selectImageButton.setOnClickListener {
            selectImage()
        }

        saveButton.setOnClickListener {
            val selectedCategory = if (categorySpinner.selectedItem.toString() == "Other") {
                otherCategoryEdit.text.toString()
            } else {
                categorySpinner.selectedItem.toString()
            }
            val goal = goalEditText.text.toString().toIntOrNull() ?: 0
            saveImage(imageUri, selectedCategory, goal)
            imageUri = null
            goalEditText.visibility = View.GONE
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        val textView = findViewById<TextView>(R.id.textViewBooks)
        textView.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_IMAGE_REQUEST)
    }

    private fun saveImage(imageUri: Uri?, categoryName: String, goal: Int) {
        val savedImagesJson = sharedPreferences.getStringSet("cat", mutableSetOf()) ?: mutableSetOf()
        var size = savedImagesJson.size
        var num = ""
        if (size > 0) {
            size++
            num = size.toString()
        }
        var imageString = ""
        if (imageUri != null) {
            imageString = imageUri.toString()
        } else {
            imageString = Uri.parse("android.resource://${packageName}/${R.drawable.ic_default_cat}").toString()
        }

        // Convert the goal to a string
        val goalString = goal.toString()

        // Construct the string with category, image URL, and goal
        val dataString = "$categoryName||$imageString||$goalString"

        savedImagesJson.add(dataString)
        sharedPreferences.edit().putStringSet("cat$num", savedImagesJson).apply()

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
        }
    }
}
