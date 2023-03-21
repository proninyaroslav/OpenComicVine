package org.proninyaroslav.opencomicvine.data.item.volume

import org.proninyaroslav.opencomicvine.data.item.BaseItem

interface VolumeItem : BaseItem {
    val countOfAppearances: Int
}