package com.neutrino

object GlobalData {
    private val observerList: ArrayList<GlobalDataObserver> = ArrayList()
    private val dataList: HashMap<GlobalDataType, ArrayList<Any?>> = hashMapOf(
        Pair(GlobalDataType.PLAYERHP, ArrayList()),
        Pair(GlobalDataType.PLAYERMANA, ArrayList()),
        Pair(GlobalDataType.PICKUP, ArrayList()),
        Pair(GlobalDataType.CHANGELEVEL, ArrayList())
    )
    fun registerObserver(globalDataObserver: GlobalDataObserver) {
        observerList.add(globalDataObserver)
    }
    fun registerObserverGetData(globalDataObserver: GlobalDataObserver) {
        observerList.add(globalDataObserver)

        val iterator = dataList[globalDataObserver.dataType]!!.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            val unregister = globalDataObserver.update(data)
            if (unregister)
                iterator.remove()
        }
    }
    fun unregisterObserver(globalDataObserver: GlobalDataObserver) {
        observerList.remove(globalDataObserver)
    }
    fun registerData(dataType: GlobalDataType, data: Any?) {
        try {
            dataList[dataType]!!.add(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun unregisterData(dataType: GlobalDataType, data: Any?) {
        dataList[dataType]?.remove(data)
    }
    private fun getData(dataType: GlobalDataType): ArrayList<Any?> {
        return dataList[dataType]!!
    }

    fun notifyObservers(dataType: GlobalDataType, data: Any? = null) {
        for (observer in observerList) {
            if (observer.dataType == dataType) {
                val unregister = observer.update(data)
                if (unregister)
                    unregisterData(dataType, data)
            }
        }
    }
}

enum class GlobalDataType {
    PLAYERHP,
    PLAYERMANA,
    PICKUP,
    EQUIPMENT,
    CHANGELEVEL
}