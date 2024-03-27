package com.example.kibladirection.Activities.Ads
import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kibladirection.Activities.Activities.LanguageSelectionActivity
import com.example.kibladirection.R
import com.example.kibladirection.databinding.CustomLanguageItemBinding

class LanguageAdapter(
    private val languages: Array<LanguageSelectionActivity.Language>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private val sharedPreferencesKey = "MyPrefs"
    private var selectedPosition = -1 // Default no selection

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = CustomLanguageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    inner class LanguageViewHolder(private val binding: CustomLanguageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val language = languages[position]
            binding.text1.text = language.name
            Glide.with(binding.root.context)
                .load(language.flagResourceId)
                .into(binding.imageView)

            val sharedPreferences = itemView.context.getSharedPreferences(sharedPreferencesKey, MODE_PRIVATE)
            val selectedLanguagePosition = sharedPreferences.getInt("selected_language_position", -1)
            val editor = sharedPreferences.edit()

            // Check if the current position is the selected position
            val isSelected = position == selectedLanguagePosition

            // Update the UI based on the selected state
            if (isSelected) {
                binding.root.setBackgroundResource(R.drawable.selected_language_background)
            } else {
                binding.root.setBackgroundResource(android.R.color.transparent)
            }

            binding.root.setOnClickListener {
                // Update the selected position
                val previousSelectedPosition = selectedPosition
                selectedPosition = adapterPosition
                editor.putInt("selected_language_position", selectedPosition)
                editor.apply()
                notifyItemChanged(previousSelectedPosition) // Notify previous selected item to update UI
                notifyItemChanged(selectedPosition) // Notify current selected item to update UI
                listener.onItemClick(selectedPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
