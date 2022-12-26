package com.xcaret.xcaret_hotel.view.adapters

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.domain.FilterMap
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.setDrawableColor

class FilterMapAdapter: BaseExpandableListAdapter() {

    private val items = mutableListOf<FilterMap>()
    private var inflater: LayoutInflater? = null

    fun addItems(items: List<FilterMap>){
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun getItems() = items

    fun getItem(pos: Int): FilterMap = items[pos]

    override fun getGroup(p0: Int): FilterMap = items[p0]
    override fun isChildSelectable(p0: Int, p1: Int): Boolean = true
    override fun hasStableIds(): Boolean = true

    override fun getGroupView(p0: Int, p1: Boolean, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = inflater ?: LayoutInflater.from(parent?.context)
        val mConvertView: View = convertView ?: layoutInflater.inflate(R.layout.item_filter_map_root, null)
        val item = getGroup(p0)
        val iconRes = Utils.getDrawableId(parent?.context, item.icon)
        val txtRoot = mConvertView.findViewById<TextView>(R.id.txtRoot)
        val showMore = mConvertView.findViewById<ImageView>(R.id.showMore)
        val separator = mConvertView.findViewById<View>(R.id.separator)
        val separatorTop = mConvertView.findViewById<View>(R.id.separatorTop)
        iconRes?.let {
            if(it > 0) {
                val drawable = ContextCompat.getDrawable(parent?.context!!, it)
                if(Constants.listConserveColor.contains(item.code))
                    drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(parent.context, R.color.colorIconMedicalService), PorterDuff.Mode.SRC_IN)
                else drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(parent.context, R.color.colorIconFilter), PorterDuff.Mode.SRC_IN)
                txtRoot.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
            }
        }

        txtRoot.text = item.lang?.name
        if(p0 > 0){
            val previus = getGroup(p0-1)
            if(previus.childs.size > 0)
                separatorTop.visibility = View.VISIBLE
            else separatorTop.visibility = View.GONE
        }else{
            separatorTop.visibility = View.GONE
        }

        val visible = if(item.childs.size > 0) View.VISIBLE else View.GONE
        val visibleSeparator = if(item.childs.size > 0) View.INVISIBLE else View.VISIBLE
        separator.visibility = visibleSeparator
        showMore.visibility = visible
        return mConvertView
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, convertView: View?, parentView: ViewGroup?): View {
        val layoutInflater = inflater ?: LayoutInflater.from(parentView?.context)
        val mConvertView: View = convertView ?: layoutInflater.inflate(R.layout.item_filter_map_branch, null)
        val item = getChild(p0, p1)
        val itemGroup = getGroup(p0)
        val iconRes = Utils.getDrawableId(parentView?.context, item.icon)
        val txtBranch = mConvertView.findViewById<TextView>(R.id.txtBranch)
        val separator = mConvertView.findViewById<View>(R.id.separator)

        if(!item.icon.isNullOrEmpty()) {
            iconRes?.let {
                if (it > 0) {
                    val drawable = ContextCompat.getDrawable(parentView?.context!!, it)
                    txtBranch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        drawable, null, null, null
                    )
                }
            }
        }else txtBranch.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, null, null
        )

        if((itemGroup.childs.size-1) == p1){
            separator.visibility = View.VISIBLE
        }else separator.visibility = View.GONE

        txtBranch.text = item.lang?.name
        return mConvertView
    }

    override fun getChildrenCount(p0: Int): Int {
        val size = items[p0].childs.size
        LogHX.i("sizechild", "${size}")
        return size
    }

    override fun getChild(p0: Int, p1: Int): FilterMap = items[p0].childs[p1]
    override fun getGroupId(p0: Int): Long = p0.toLong()

    override fun getChildId(p0: Int, p1: Int): Long = p1.toLong()

    override fun getGroupCount(): Int = items.size
}