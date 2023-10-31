package io.github.jaknndiius.schoolapp.view

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlin.collections.ArrayList

abstract class ListViewAdapter<T>: BaseAdapter() {

    private var listViewItemList = ArrayList<T>()

    fun getList() = listViewItemList.toList()
    fun modifyItem(index: Int, value: T) { listViewItemList[index] = value }
    fun removeItem(index: Int) { listViewItemList.removeAt(index) }
    fun addItem(value: T) {listViewItemList.add(value)}

    override fun getCount(): Int {
        return listViewItemList.size
    }

    abstract override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): T {
        return listViewItemList[position]
    }
}