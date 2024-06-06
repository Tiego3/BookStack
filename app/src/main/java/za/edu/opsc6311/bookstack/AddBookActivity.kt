package za.edu.opsc6311.bookstack

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddBookActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private lateinit var bookNameEditText: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var coverImageView: ImageView
    private lateinit var saveButton: Button

    private var selectedImageUri: Uri? = null
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbook)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        bookNameEditText = findViewById(R.id.bookNameEditText)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        coverImageView = findViewById(R.id.coverImageView)
        saveButton = findViewById(R.id.saveButton)

        uploadImageButton.setOnClickListener {
            showImagePickerDialog()
        }

        saveButton.setOnClickListener {
            saveBook()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> dispatchTakePictureIntent()
                "Choose from Gallery" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, 1000)
                }
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoFile: File? = createImageFile()
                if (photoFile != null) {
                    val photoURI: Uri = FileProvider.getUriForFile(this, "za.edu.opsc6311.bookstack.fileprovider", photoFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 1001)
                }
            } catch (ex: IOException) {
                // Handle error
                Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            coverImageView.setImageURI(selectedImageUri)
            coverImageView.visibility = ImageView.VISIBLE
        } else if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            selectedImageUri = Uri.fromFile(file)
            coverImageView.setImageURI(selectedImageUri)
            coverImageView.visibility = ImageView.VISIBLE
        }
    }

    private fun saveBook() {
        val bookName = bookNameEditText.text.toString()
        if (bookName.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Book name or cover image is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val bookId = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("book_covers/$userId/$bookId.jpg")

        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val book = Book(bookName, uri.toString())
                    database.child("books").child(userId).child(bookId).setValue(book)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Book saved successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to save book", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

}
