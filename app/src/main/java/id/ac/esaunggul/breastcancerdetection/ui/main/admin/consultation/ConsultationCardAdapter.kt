package id.ac.esaunggul.breastcancerdetection.ui.main.admin.consultation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ac.esaunggul.breastcancerdetection.data.model.ConsultationModel
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentConsultationAdminCardBinding
import id.ac.esaunggul.breastcancerdetection.util.binding.ClickListener

class ConsultationCardAdapter(
    private val clickListener: ClickListener
) : ListAdapter<ConsultationModel, ConsultationCardAdapter.ConsultationViewHolder>(
    ConsultationDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        return ConsultationViewHolder.from(this, parent)
    }

    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ConsultationViewHolder
    private constructor(
        private val binding: FragmentConsultationAdminCardBinding,
        private val clickListener: ClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.consultationAdminCardView.setOnClickListener(this)
        }

        fun bind(consultation: ConsultationModel) {
            binding.consultation = consultation
            binding.executePendingBindings()
        }

        override fun onClick(v: View?) {
            clickListener.onClick(
                bindingAdapterPosition
            )
        }

        companion object {
            fun from(
                consultationCardAdapter: ConsultationCardAdapter,
                parent: ViewGroup
            ): ConsultationViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentConsultationAdminCardBinding.inflate(inflater, parent, false)
                return ConsultationViewHolder(binding, consultationCardAdapter.clickListener)
            }
        }
    }

    class ConsultationDiffCallback : DiffUtil.ItemCallback<ConsultationModel>() {

        override fun areItemsTheSame(
            oldItem: ConsultationModel,
            newItem: ConsultationModel
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: ConsultationModel,
            newItem: ConsultationModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}