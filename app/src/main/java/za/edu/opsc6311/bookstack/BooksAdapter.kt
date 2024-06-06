package za.edu.opsc6311.bookstack
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BooksAdapter(private val imageList: List<Pair<String, String>>) : RecyclerView.Adapter<BooksAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val (imageName, imageUrl) = imageList[position]
        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.imageView)
        holder.imageNameTextView.text = imageName
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val imageNameTextView: TextView = itemView.findViewById(R.id.imageNameTextView)
    }
}
