package com.coscos.ifthenplanner

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val context: Context,
                      private val ifList: MutableList<String>,
                      private val thenList: MutableList<String>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.let {
            it.ifStatement.text = ifList.get(position)
            it.thenStatement.text= thenList.get(position)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.list_item, parent, false)

        val holder = ViewHolder(mView)

        return holder
    }

    override fun getItemCount(): Int {
        return ifList.size
    }

    var onItemClick: ((pos: Int, view: View) -> Unit)? = null
    var onLongItemClick: ((pos: Int, view: View) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener{
        override fun onLongClick(v: View): Boolean {
            onLongItemClick?.invoke(adapterPosition, v)
            return true
        }

        override fun onClick(v: View) {
            onItemClick?.invoke(adapterPosition, v)
        }
        val ifStatement: TextView = itemView.findViewById(R.id.if_statement)
        val thenStatement: TextView = itemView.findViewById(R.id.then_statement)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

    }

}