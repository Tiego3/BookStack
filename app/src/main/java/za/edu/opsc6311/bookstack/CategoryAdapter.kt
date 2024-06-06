package za.edu.opsc6311.bookstack

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CategoryAdapter(private val catList: List<Pair<String, String>>) : RecyclerView.Adapter<CategoryAdapter.ImageViewHolder>() {

    private var onGoalSetListener: ((String, Int) -> Unit)? = null
    private var itemDoubleClickListener: ((String, Int) -> Unit)? = null

    fun setOnGoalSetListener(listener: (String, Int) -> Unit) {
        onGoalSetListener = listener
    }

    fun setOnItemDoubleClickListener(listener: (String, Int) -> Unit) {
        itemDoubleClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val (imageName, imageUrl) = catList[position]
        //val imageUrl = imageList[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.catView)
        holder.catNameTextView.text = imageName

        holder.itemView.setOnTouchListener(null) // Clearing any previous touch listeners

        // Double click listener
        val gestureDetector = GestureDetectorCompat(holder.itemView.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                itemDoubleClickListener?.invoke(imageName, holder.adapterPosition)
                return true
            }
        })

        holder.itemView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    override fun getItemCount(): Int {
        return catList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catView: ImageView = itemView.findViewById(R.id.catView)
        val catNameTextView: TextView = itemView.findViewById(R.id.catNameTextView)
    }
}

/*class CategoryAdapter(private val catList: List<Pair<String, String>>) : RecyclerView.Adapter<CategoryAdapter.ImageViewHolder>() {

    private var onGoalSetListener: ((String, Int) -> Unit)? = null
    private var itemDoubleClickListener: ((String, Int) -> Unit)? = null

    fun setOnGoalSetListener(listener: (String, Int) -> Unit) {
        onGoalSetListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val (imageName, imageUrl) = catList[position]
        //val imageUrl = imageList[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.catView)
        holder.catNameTextView.text = imageName

        holder.itemView.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_set_goal, null)
            val goalEditText = dialogView.findViewById<EditText>(R.id.goalEditText)
            dialogBuilder.setView(dialogView)
            dialogBuilder.setTitle("Set Goal")
            dialogBuilder.setPositiveButton("Set") { dialog, _ ->
                val newGoal = goalEditText.text.toString().toIntOrNull() ?: 0
                if (newGoal > 0) {
                    onGoalSetListener?.invoke(imageName, newGoal)
                }
                dialog.dismiss()
            }
            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            dialogBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return catList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catView: ImageView = itemView.findViewById(R.id.catView)
        val catNameTextView: TextView = itemView.findViewById(R.id.catNameTextView)
    }

    fun setOnItemClickListener(listener: (String, Int) -> Unit) {
        itemDoubleClickListener = listener
    }
}
*/