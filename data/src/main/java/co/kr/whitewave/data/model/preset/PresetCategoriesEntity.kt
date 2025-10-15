package co.kr.whitewave.data.model.preset

import co.kr.whitewave.data.DataMapper
import co.kr.whitewave.domain.model.preset.PresetCategories

enum class PresetCategoriesEntity(
    val category: String
) : DataMapper<PresetCategories> {
    ALL("모두"),
    CUSTOM("커스텀"),
    SLEEP("수면"),
    RAIN("비"),
    RELAX("휴식"),
    MEDITATION("명상"),
    WORK("업무");

    override fun toDomain(): PresetCategories =
        when (this) {
            ALL -> PresetCategories.ALL
            CUSTOM -> PresetCategories.CUSTOM
            SLEEP -> PresetCategories.SLEEP
            RAIN -> PresetCategories.RAIN
            RELAX -> PresetCategories.RELAX
            MEDITATION -> PresetCategories.MEDITATION
            WORK -> PresetCategories.WORK
        }
}