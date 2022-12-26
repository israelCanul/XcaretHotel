package com.xcaret.xcaret_hotel.view.config

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Ignore
import com.xcaret.xcaret_hotel.BR
import com.xcaret.xcaret_hotel.R


class GenericAdapter<T : ListItemViewModel>(@LayoutRes val layoutId: Int, val percentageWith: Float = 0f, val percentageHeight: Float = 0f) :
    RecyclerView.Adapter<GenericAdapter.GenericViewHolder<T>>(), Filterable {

    private val items = mutableListOf<T>()
    private val itemsOriginal = mutableListOf<T>()
    private var inflater: LayoutInflater? = null
    private var onListItemViewClickListener: OnListItemViewClickListener? = null
    private var offsetMarginHorizontal = 0f
    private var mFilter: Filter? = null
    var widthPixel = 0
    var heightPixel = 0

    fun addItems(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        this.itemsOriginal.clear()
        this.itemsOriginal.addAll(items)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T? {
        if(position >= this.items.size) return null
        return this.items[position]
    }

    fun getItems() = items

    fun getOriginalItems() = itemsOriginal

    fun addFilter(mFilter: Filter){
        this.mFilter = mFilter
    }

    fun setOffsetHorizontalMargin(margin: Float){
        if(margin > 0)
            this.offsetMarginHorizontal = margin
    }

    fun addFilterResult(items: List<T>){
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: T){
        this.items.add(item)
        this.notifyItemInserted(items.size-1)
    }

    fun removeItem(position: Int) {
        this.items.removeAt(position)
        this.notifyItemRemoved(position)
        notifyItemRangeChanged(position,items.size)
    }

    fun restoreItem(item: T, position: Int) {
        this.items.add(position, item)
        //notifyItemInserted(position)
        notifyItemRangeChanged(position, this.items.size-1)
    }

    fun setOnListItemViewClickListener(onListItemViewClickListener: OnListItemViewClickListener?){
        this.onListItemViewClickListener = onListItemViewClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
        val layoutInflater = inflater ?: LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutId, parent, false)

        return GenericViewHolder(binding, percentageHeight, percentageWith, offsetMarginHorizontal, widthPixel, heightPixel)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        val itemViewModel = items[position]
        itemViewModel.adapterPosition = position
        onListItemViewClickListener?.let { itemViewModel.onListItemViewClickListener = it }
        holder.bind(itemViewModel)
    }

    class GenericViewHolder<T : ListItemViewModel>(private val binding: ViewDataBinding,
                                                   percentageHeight: Float = 0f, percentageWith: Float = 0f, marginHorizonalOffset: Float = 0f,
                                                   widthPixel: Int = 0, heightPixel: Int = 0) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            try {

                apply {
                    var width = 0
                    var height = 0
                    val layoutContent = itemView.findViewById<ViewGroup>(R.id.layoutContent)
                    val params = layoutContent.layoutParams

                    if(widthPixel > 0 && heightPixel > 0){
                        width = widthPixel
                        height = heightPixel
                    }else if(percentageHeight > 0f || percentageWith > 0f) {
                        val screenSize = Utils.getScreenSize(itemView.context)
                        val widthPixels = screenSize.widthPixels - (marginHorizonalOffset * 2)
                        var separator =
                            itemView.resources.getDimensionPixelSize(R.dimen.margin_large) * 2


                        if (params is ViewGroup.MarginLayoutParams) {
                            separator = params.leftMargin * 2
                        }

                        width = ((widthPixels * percentageWith) - separator).toInt()
                        height = (screenSize.heightPixels * percentageHeight).toInt()

                    }

                    if(width > 0 || height > 0) {
                        if (width > 0) params.width = width
                        if (height > 0) params.height = height
                        layoutContent.layoutParams = params
                    }

                }
            }catch (e: Exception) {}
        }

        fun bind(itemViewModel: T) {
            binding.setVariable(BR.itemViewModel, itemViewModel)
            binding.executePendingBindings()
        }

    }

    interface OnListItemViewClickListener{
        fun onClick(view: View, position: Int)
    }

    override fun getFilter(): Filter? {
        return mFilter
    }
}

abstract class ListItemViewModel{
    @Ignore var adapterPosition: Int = -1
    @Ignore var onListItemViewClickListener: GenericAdapter.OnListItemViewClickListener? = null
}