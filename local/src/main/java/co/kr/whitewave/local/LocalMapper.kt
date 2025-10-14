package co.kr.whitewave.local


interface LocalMapper<DataModel> {
    fun toEntity(): DataModel
}

fun <LocalModel : LocalMapper<DataModel>, DataModel> List<LocalModel>.toEntity(): List<DataModel> {
    return map { it.toEntity() }
}