package com.neutrino.game.map.generation

import com.neutrino.game.map.generation.util.GenerationParams

object Generators {

    private val generatorMap: HashMap<String, Generator> = HashMap()
    private val associationMap: HashMap<String, ArrayList<String>> = HashMap()
    val DEFAULT_PRIORITY = 1

    fun add(name: String, generator: Generator, associations: List<String>? = null) {
        generatorMap[name] = generator
        if (associations != null)
            addAssociations(name, associations)
        else
            addOthersAssociation(name)
    }

    fun add(name: String, associations: List<String>? = null, generator: (params: GenerationParams) -> Unit) {
        generatorMap[name] = Generator(false, DEFAULT_PRIORITY, generator)
        if (associations != null)
            addAssociations(name, associations)
        else
            addOthersAssociation(name)
    }

    fun get(name: String): Generator {
        return generatorMap[name]!!
    }

    fun getAssociations(): HashMap<String, ArrayList<String>> {
        return associationMap
    }

    fun getAssociationNames(association: String): List<String>? {
        return associationMap[association]
    }

    fun getAssociationGenerators(association: String): List<Generator> {
        val generatorList = ArrayList<Generator>()
        if (associationMap[association] == null)
            return generatorList
        for (generator in associationMap[association]!!) {
            generatorList.add(generatorMap[generator]!!)
        }
        return generatorList
    }

    private fun addAssociations(name: String, associations: List<String>) {
        for (association in associations) {
            if (associationMap[association] == null)
                associationMap[association] = ArrayList()
            associationMap[association]!!.add(name)
        }
    }

    private fun addOthersAssociation(name: String) {
        if (associationMap["others"] == null)
            associationMap["others"] = ArrayList()
        associationMap["others"]!!.add(name)
    }
}
