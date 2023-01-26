package com.wings.mile.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

import com.wings.mile.R
import com.wings.mile.databinding.SchemeCellBinding
import com.wings.mile.model.getdriver
import kotlin.collections.ArrayList


class AlldriversAdapter(var context: Context, var dataSource1: TextView, var dataSource: List<getdriver>, var onItemClicked: OnItemClicked, value: Int) : RecyclerView.Adapter<AlldriversAdapter.AllAdapterViewHolder>(), Filterable {
    var followModel: ArrayList<getdriver> = ArrayList()
    var filterList: ArrayList<getdriver>? = ArrayList()
    var id = value

    init {
        //followModel = dataSource as ArrayList<getdriver>
       // filterList = dataSource as ArrayList<getdriver>
        followModel.addAll(dataSource)
        filterList!!.addAll(dataSource)
    }

    interface OnItemClicked {
        fun onDriverdetailsclick(driversItem: getdriver)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllAdapterViewHolder {
        val binding: SchemeCellBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.scheme_cell, parent, false)
        return AllAdapterViewHolder(binding, onItemClicked, id)
    }

    override fun onBindViewHolder(holder: AllAdapterViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(context, followModel, position)

    }

    override fun getItemCount(): Int {
        //Toast.makeText(context,followModel.size,Toast.LENGTH_LONG).show()
        Log.e("data--->",""+followModel.size)
        return followModel.size
    }

    class AllAdapterViewHolder(var binding: SchemeCellBinding, var onItemClicked: OnItemClicked, var value: Int) : RecyclerView.ViewHolder(binding.root) {

        var id = value
        fun bind(context: Context, followModel: ArrayList<getdriver>, position: Int) {

            followModel[position].let {
                if (followModel.size == 0) {
                    binding.textViewNoRecordFound2.visibility = View.VISIBLE
                    binding.rlayout.visibility = View.GONE
                    binding.rvLayout.visibility = View.GONE
                } else {
                    binding.textViewNoRecordFound2.visibility = View.GONE
                    binding.rlayout.visibility = View.VISIBLE
                    binding.rvLayout.visibility = View.VISIBLE
                    binding.textViewName.text = followModel.get(position).name.toString()
                    binding.textViewApplicationNumber.text =
                        followModel.get(position).phone_num.toString()
                    binding.textViewCropName.text =
                        followModel.get(position).insurance_no.toString()
                    binding.textViewTillSchemename.text =
                        followModel.get(position).license_no.toString()
                    binding.rlayout.setOnClickListener {

                        onItemClicked.onDriverdetailsclick(followModel[position])
                    }



                }
            }
        }
    }

    fun setData(data: ArrayList<getdriver>?) {
        this.followModel.clear()
        data?.let { this.followModel.addAll(it) }
        data?.let { this.filterList?.addAll(it) }
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    followModel = filterList as ArrayList<getdriver>
                } else {
                   // dataSource1.visibility=View.GONE
                    val resultList = ArrayList<getdriver>()
                    resultList.clear()

                    for ((index, values) in filterList!!.withIndex()) {
                        if (values.insurance_no?.toLowerCase()!!.contains(charSearch.toLowerCase())) {
                            resultList.add(values)
                        } else if (values.license_no?.toLowerCase()!!.contains(
                                charSearch.toLowerCase()
                            )) {
                            resultList.add(values)
                        } else if (values.phone_num.toString().contains(charSearch)) {
                            resultList.add(values)
                        } else if (values.name?.toLowerCase()!!.contains(charSearch.toLowerCase())) {
                            resultList.add(values)
                        }

                    }
                    followModel = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = followModel
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                followModel = results?.values as ArrayList<getdriver>
                // onItemClicked.filterList(listTargetApproval!!.size)
                if (followModel.size == 0) {
                    dataSource1.visibility=View.VISIBLE

                 //  CustomToast.warningToast(context, context.getString(R.string.no_record_found))
                    //val toast = Toast.makeText(context, context.getString(R.string.no_record_found), Toast.LENGTH_SHORT)
                    //toast.setGravity(Gravity.CENTER, 0, 0)
                    //toast.show()
                }else{
                    dataSource1.visibility=View.GONE

                }
                notifyDataSetChanged()
            }
        }
    }
}
