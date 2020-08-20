package id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ac.esaunggul.breastcancerdetection.data.model.DiagnosisModel
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisAdminCardBinding
import id.ac.esaunggul.breastcancerdetection.util.binding.ClickListener

class DiagnosisCardAdapter(
    private val clickListener: ClickListener
) : ListAdapter<DiagnosisModel, DiagnosisCardAdapter.DiagnosisViewHolder>(
    DiagnosisDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        return DiagnosisViewHolder.from(
            this,
            parent
        )
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiagnosisViewHolder
    private constructor(
        private val binding: FragmentDiagnosisAdminCardBinding,
        private val clickListener: ClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.diagnosisAdminCardView.setOnClickListener(this)
        }

        fun bind(diagnosis: DiagnosisModel) {
            binding.diagnosis = diagnosis
            binding.executePendingBindings()
        }

        override fun onClick(v: View?) {
            clickListener.onClick(
                bindingAdapterPosition
            )
        }

        companion object {
            fun from(
                diagnosisCardAdapter: DiagnosisCardAdapter,
                parent: ViewGroup
            ): DiagnosisViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentDiagnosisAdminCardBinding.inflate(inflater, parent, false)
                return DiagnosisViewHolder(
                    binding,
                    diagnosisCardAdapter.clickListener
                )
            }
        }
    }

    class DiagnosisDiffCallback : DiffUtil.ItemCallback<DiagnosisModel>() {

        override fun areItemsTheSame(
            oldItem: DiagnosisModel,
            newItem: DiagnosisModel
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: DiagnosisModel,
            newItem: DiagnosisModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}