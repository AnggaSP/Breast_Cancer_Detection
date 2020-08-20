package id.ac.esaunggul.breastcancerdetection.ui.main.user.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.ac.esaunggul.breastcancerdetection.data.model.ArticleModel
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentHomeArticleCardBinding
import id.ac.esaunggul.breastcancerdetection.util.binding.ClickListener

class HomeArticleCardAdapter(
    private val clickListener: ClickListener
) : ListAdapter<ArticleModel, HomeArticleCardAdapter.HomeArticleViewHolder>(HomeArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeArticleViewHolder {
        return HomeArticleViewHolder.from(this, parent)
    }

    override fun onBindViewHolder(holder: HomeArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HomeArticleViewHolder
    private constructor(
        private val binding: FragmentHomeArticleCardBinding,
        private val clickListener: ClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.articleCardView.setOnClickListener(this)
        }

        fun bind(article: ArticleModel) {
            binding.article = article
            binding.articleCardView.transitionName =
                "shared_article_container_transition_$bindingAdapterPosition"
            binding.executePendingBindings()
        }

        override fun onClick(v: View?) {
            clickListener.onClick(
                bindingAdapterPosition,
                binding.articleCardView
            )
        }

        companion object {
            fun from(
                articleAdapter: HomeArticleCardAdapter,
                parent: ViewGroup
            ): HomeArticleViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentHomeArticleCardBinding.inflate(inflater, parent, false)
                return HomeArticleViewHolder(binding, articleAdapter.clickListener)
            }
        }
    }

    class HomeArticleDiffCallback : DiffUtil.ItemCallback<ArticleModel>() {
        override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
            return oldItem == newItem
        }
    }
}