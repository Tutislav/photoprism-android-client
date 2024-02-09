package ua.com.radiokot.photoprism.features.memories.view.model

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.squareup.picasso.Picasso
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import ua.com.radiokot.photoprism.R
import ua.com.radiokot.photoprism.databinding.ListItemMemoryBinding
import ua.com.radiokot.photoprism.di.DI_SCOPE_SESSION
import ua.com.radiokot.photoprism.extension.hardwareConfigIfAvailable
import ua.com.radiokot.photoprism.features.memories.data.model.Memory

class MemoryListItem(
    val title: String,
    val thumbnailUrl: String,
    val source: Memory?,
) : AbstractItem<MemoryListItem.ViewHolder>() {
    override val layoutRes: Int =
        R.layout.list_item_memory

    override val type: Int =
        R.id.list_item_memory

    override var identifier: Long =
        source?.hashCode()?.toLong() ?: -1L

    constructor(source: Memory) : this(
        title = when (source) {
            is Memory.ThisDayInThePast ->
                // TODO use resources.
                "This day in ${source.year}"
        },
        thumbnailUrl = source.getThumbnailUrl(500),
        source = source,
    )

    override fun getViewHolder(v: View): ViewHolder =
        ViewHolder(v)

    class ViewHolder(
        itemView: View
    ) : FastAdapter.ViewHolder<MemoryListItem>(itemView), KoinScopeComponent {
        override val scope: Scope
            get() = getKoin().getScope(DI_SCOPE_SESSION)

        private val view = ListItemMemoryBinding.bind(itemView)
        private val picasso: Picasso by inject()

        override fun bindView(item: MemoryListItem, payloads: List<Any>) {
            view.titleTextView.text = item.title
            view.imageView.contentDescription = item.title

            picasso
                .load(item.thumbnailUrl)
                .hardwareConfigIfAvailable()
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
                .into(view.imageView)
        }

        override fun unbindView(item: MemoryListItem) {
            picasso.cancelRequest(view.imageView)
        }
    }
}