package co.kr.whitewave.data

internal interface DataMapper<DomainModel> {
    fun toDomain(): DomainModel
}

internal fun <EntityModel : DataMapper<DomainModel>, DomainModel> List<EntityModel>.toDomain(): List<DomainModel> {
    return map(DataMapper<DomainModel>::toDomain)
}