package ma.glasnost.orika.test.community.issue339

import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.test.MappingUtil

data class Dog5(var name: String?, val age: Int?, var height: Int?)

data class Cat5(var name: String?, val age: Int?, var dog: List<Dog5>?)

data class DogDTO5(var name: String? = null, var age: Int? = null, var height_new: Int? = null)

data class CatDTO5(var name: String? = null, var age: Int? = null, var dog: List<DogDTO5>? = null)

fun main() {
    val dogs = mutableListOf(Dog5("wang", 3, 56), Dog5("hong", 5, 60))
    val cat = Cat5("wang", 3, dogs)

    val mapperFactory = MappingUtil.getMapperFactory()
    configureClassMaps(mapperFactory)

    val result = mapperFactory.getMapperFacade(Cat5::class.java, CatDTO5::class.java).map(cat)

    println(cat)
    println(result)
}

private fun configureClassMaps(mapperFactory: MapperFactory) {
    mapperFactory.classMap(Dog5::class.java, DogDTO5::class.java)
            .field("height", "height_new")
            .byDefault()
            .register()
    mapperFactory.classMap(Cat5::class.java, CatDTO5::class.java)
            .byDefault()
            .register()
}

private fun configureClassMaps1(mapperFactory: MapperFactory) {
    mapperFactory.classMap(Cat5::class.java, CatDTO5::class.java)
            .field("dog{height}", "dog{height_new}")
            .field("dog{name}", "dog{name}")
            .field("dog{age}", "dog{age}")
            .byDefault()
            .register()
}
