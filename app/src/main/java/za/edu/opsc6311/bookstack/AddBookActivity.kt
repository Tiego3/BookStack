package za.edu.opsc6311.bookstack
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class AddBookActivity : AppCompatActivity() {

    private val SELECT_IMAGE_REQUEST = 1
    private val TAKE_PHOTO_REQUEST = 2
    private lateinit var sharedPreferences: SharedPreferences
    private var imageUri: Uri? = null
    private lateinit var imageNameEditText: EditText
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbook)

        sharedPreferences = getSharedPreferences("MyBooks", MODE_PRIVATE)
        imageNameEditText = findViewById(R.id.bookNameEditText)

        val selectImageButton = findViewById<Button>(R.id.uploadImageButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        selectImageButton.setOnClickListener {
            selectImage()
        }

        saveButton.setOnClickListener {

            if (imageUri != null) {
                saveImage(imageUri!!, imageNameEditText.text.toString())
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your book cover")

        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, TAKE_PHOTO_REQUEST)
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhoto, SELECT_IMAGE_REQUEST)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun saveImage(imageUri: Uri, imageName: String) {
        val savedImagesJson = sharedPreferences.getStringSet("book", mutableSetOf()) ?: mutableSetOf()
        var size = savedImagesJson.size
        var num = ""
        if (size > 0) {
            size++
            num = size.toString()
        }

        savedImagesJson.add("$imageName||${imageUri.toString()}")
        sharedPreferences.edit().putStringSet("book$num", savedImagesJson).apply()
        Toast.makeText(this, "Book saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_IMAGE_REQUEST -> {
                    if (data != null) {
                        imageUri = data.data
                    }
                }
                TAKE_PHOTO_REQUEST -> {
                    if (data != null && data.extras != null) {
                        val imageBitmap = data.extras!!.get("data") as Bitmap
                        imageUri = getImageUri(imageBitmap)
                    }
                }
            }
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}
