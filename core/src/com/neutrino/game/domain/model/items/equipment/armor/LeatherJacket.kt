package com.neutrino.game.domain.model.items.equipment.armor

//class LeatherJacket: EquipmentItem(), ItemType.EQUIPMENT.TORSO {
//    override val name: String = "Leather jacket"
//    override val description: String = "Makes you cool. And protected"
//
//    override val textureNames: List<String> = listOf("leatherJacket")
//    override var texture: TextureAtlas.AtlasRegion = setTexture()
//
//    override var goldValueOg: Int = 60
//
//    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))
//
//    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
//        OnOffEvent(EventModifyStat(StatsEnum.DEFENCE, 15f)),
//        // example
////        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
//    )
//    init {
////        statRandomization(1f)
//
//        goldValue = goldValueOg
//        realValue = (goldValue * 1.2).roundToInt()
//    }
//}