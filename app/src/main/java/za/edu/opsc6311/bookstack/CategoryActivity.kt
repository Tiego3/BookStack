package za.edu.opsc6311.bookstack

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var categoryAdapter: CategoryAdapter

   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        sharedPreferences = getSharedPreferences("Category", MODE_PRIVATE)
        val savedImages = retrieveSavedImages()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(savedImages)
        recyclerView.adapter = categoryAdapter

       /* categoryAdapter.setOnGoalSetListener { categoryName, newGoal ->
            updateGoal(categoryName, newGoal)
        }*/





        val btnAddCategories = findViewById<TextView>(R.id.buttonAddCategories)
        btnAddCategories.setOnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            startActivity(intent)
        }
    }*/
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_category)

       sharedPreferences = getSharedPreferences("Category", MODE_PRIVATE)
       val savedImages = retrieveSavedImages()

       val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
       recyclerView.layoutManager = LinearLayoutManager(this)
       categoryAdapter = CategoryAdapter(savedImages)
       recyclerView.adapter = categoryAdapter

       categoryAdapter.setOnGoalSetListener { categoryName, newGoal ->
           updateGoal(categoryName, newGoal)
       }

       categoryAdapter.setOnItemDoubleClickListener { categoryName, position ->
           // Show dialog to set goal
           showSetGoalDialog(categoryName)
       }

       val btnAddCategories = findViewById<TextView>(R.id.buttonAddCategories)
       btnAddCategories.setOnClickListener {
           val intent = Intent(this, AddCategoryActivity::class.java)
           startActivity(intent)
       }
   }
    private fun showSetGoalDialog(categoryName: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_goal, null)
        val goalEditText = dialogView.findViewById<EditText>(R.id.goalEditText)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Set Goal")
        dialogBuilder.setPositiveButton("Set") { dialog, _ ->
            val newGoal = goalEditText.text.toString().toIntOrNull() ?: 0
            if (newGoal > 0) {
                updateGoal(categoryName, newGoal)
            }
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.show()
    }

    /*private fun showSetGoalDialog(categoryName: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_goal, null)
        val goalEditText = dialogView.findViewById<EditText>(R.id.goalEditText)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Set Goal")
        dialogBuilder.setPositiveButton("Set") { dialog, _ ->
            val newGoal = goalEditText.text.toString().toIntOrNull() ?: 0
            if (newGoal > 0) {
                updateGoal(categoryName, newGoal)
            }
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.show()
    }*/


    private fun retrieveSavedImages(): List<Pair<String, String>> {
        val savedImagesSet = sharedPreferences.getStringSet("cat", setOf()) ?: setOf()
        return savedImagesSet.mapNotNull { imageString ->
            val parts = imageString.split("||")
            val categoryName = parts[0]
            val imageUrl = parts[1]
            val goal = parts.getOrNull(2)?.toIntOrNull() ?: 0
            // Concatenate the category name and goal into a single string
            val categoryNameWithGoal = if (goal == 0) {
                categoryName
            } else {
                "$categoryName (Goal: $goal)"
            }
            Pair(categoryNameWithGoal, imageUrl)
        }
    }

    private fun updateGoal(categoryName: String, newGoal: Int) {
        val savedImagesSet = sharedPreferences.getStringSet("cat", mutableSetOf()) ?: mutableSetOf()
        val updatedSet = savedImagesSet.map { imageString ->
            val parts = imageString.split("||")
            val currentCategoryParts = parts[0].split(" (Goal: ")
            val currentCategoryName = currentCategoryParts[0]
            val imageUrl = parts[1]

            val targetCategoryParts = categoryName.split(" (Goal: ")
            val targetCategoryName = targetCategoryParts[0]

            if (currentCategoryName == targetCategoryName) {
                "$currentCategoryName||$imageUrl||$newGoal"
            } else {
                imageString
            }
        }.toMutableSet()

        sharedPreferences.edit().putStringSet("cat", updatedSet).apply()
        categoryAdapter.notifyDataSetChanged()
        startActivity(Intent(this, CategoryActivity::class.java))
    }


}
