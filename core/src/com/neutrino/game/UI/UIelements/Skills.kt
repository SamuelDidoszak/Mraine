package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType
import com.neutrino.game.Constants
import com.neutrino.game.Fonts
import com.neutrino.game.UI.utility.SkillActor
import com.neutrino.game.UI.utility.SkillTreeActor
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.SkillTree
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.isIn
import com.neutrino.game.roundPosition
import ktx.actors.setScrollFocus
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import space.earlygrey.shapedrawer.ShapeDrawer

class Skills(private val uiElements: Map<String, TextureAtlas.AtlasRegion>): Group() {

    private lateinit var border: Image
    val skillTable: ScrollPane = ScrollPane(getSkillTable())
    private val skillTrees: Group = Group()

    private val detailsPane: ScrollPane = ScrollPane(Group())

    private val strengthTree: ScrollPane = ScrollPane(getSkillTree(SkillTree.STRENGTH, 0))
    private val dexterityTree: ScrollPane = ScrollPane(getSkillTree(SkillTree.DEXTERITY, 1))
    private val intelligenceTree: ScrollPane = ScrollPane(getSkillTree(SkillTree.INTELLIGENCE, 2))
    private val summoningTree: ScrollPane = ScrollPane(getSkillTree(SkillTree.INTELLIGENCE, 3))
    private var treeList = listOf(strengthTree, dexterityTree, intelligenceTree, summoningTree)
    private var treeNameList = listOf("[${ColorUtils.toHexadecimal(getTreeColor(0))}]STRENGTH",
        "[${ColorUtils.toHexadecimal(getTreeColor(1))}]DEXTERITY",
        "[${ColorUtils.toHexadecimal(getTreeColor(2))}]INTELLIGENCE",
        "[${ColorUtils.toHexadecimal(getTreeColor(3))}]SUMMONING")

    var currentTab: Actor = skillTrees
        private set

    private var currentlyFocused: Actor? = skillTable

    fun scrollFocus(x: Float, y: Float) {
        fun setCurrentScroll(actor: Actor) {
            if (currentlyFocused == actor)
                return

            actor.setScrollFocus(true)
            currentlyFocused = actor
        }

        val coord: Vector2 = stageToLocalCoordinates(
            Vector2(x, y)
        )

        if (detailsPane.isIn(coord.x, coord.y)) {
            setCurrentScroll(detailsPane)
            return
        }

        if (currentTab == skillTable && skillTable.isIn(coord.x, coord.y)) {
            setCurrentScroll(skillTable)
            return
        }

        if (treeList[currentTree].isIn(coord.x, coord.y)) {
            setCurrentScroll(treeList[currentTree])
            return
        }

        currentlyFocused?.setScrollFocus(false)
        currentlyFocused = null
    }

    fun changeTab() {
        currentTab.isVisible = false
        if (currentTab == skillTable) {
            currentTab = skillTrees
            treeList[currentTree].setScrollFocus(true)
            currentlyFocused = treeList[currentTree]
        }
        else if (currentTab == skillTrees) {
            currentTab = skillTable
            skillTable.setScrollFocus(true)
            currentlyFocused = skillTable
        }
        currentTab.isVisible = true
    }

    private var currentTree = 0

    /**
     * Changes the currently displayed tree
     * @param -1 or 1; -1 changes to the tree on the left, 1 changes it to the tree on the right
     */
    private fun changeTree(direction: Int) {
        treeList[currentTree].isVisible = false
        currentTree += direction
        if (currentTree < 0)
            currentTree = treeList.size - 1
        else if (currentTree >= treeList.size)
            currentTree = 0

        treeList[currentTree].isVisible = true
        treeList[currentTree].setScrollFocus(true)

        val treeChooseGroup = skillTrees.findActor<Table>("treeMoveGroup")
        treeChooseGroup.findActor<TextraLabel>("treeNameText").setText(treeNameList[currentTree])
        treeChooseGroup.invalidate()
        treeChooseGroup.validate()
    }

    private var currentlyViewedSkill: SkillTreeActor? = null

    fun parseClick(xClick: Float, yClick: Float) {
        val coord: Vector2 = stageToLocalCoordinates(
            Vector2(xClick, yClick)
        )
        if (!skillTrees.isIn(coord.x, coord.y))
            return

        val menuCoord = localToActorCoordinates(skillTrees.findActor("treeMoveGroup"), coord)

        if (skillTrees.findActor<TextButton>("leftButton").isIn(menuCoord.x, menuCoord.y)) {
            changeTree(-1)
            return
        }
        if (skillTrees.findActor<TextButton>("rightButton").isIn(menuCoord.x, menuCoord.y)) {
            changeTree(1)
            return
        }

        val skillAt = getSkillAt(xClick, yClick, treeList[currentTree])
        if (skillAt != null && skillAt is SkillTreeActor) {
            showSkillDetails(skillAt.skill)
            currentlyViewedSkill?.setHighlight(false)
            if (currentlyViewedSkill == skillAt) {
                currentlyViewedSkill = null
                return
            }
            skillAt.setHighlight(true)
            currentlyViewedSkill = skillAt
        }
        if (skillAt == null) {
            currentlyViewedSkill?.setHighlight(false)
            currentlyViewedSkill = null
            detailsPane.actor = null
        }
    }

    fun onHover(x: Float, y: Float) {
        if (currentlyViewedSkill != null)
            return
        val skillAt = getSkillAt(x, y, treeList[currentTree])
        if (skillAt != null && skillAt is SkillTreeActor)
            showSkillDetails(skillAt.skill)
        else
            detailsPane.actor = null
    }

    fun showSkillDetails(skill: Skill?) {
        if (skill == null) {
            detailsPane.actor = null
            return
        }

        val skillDetailsImage = Image(TextureRegion(Constants.DefaultIconTexture.findRegion(skill.textureName)))
        val skillName = TextraLabel(skill.name, Fonts.EQUIPMENT, getTreeColor(skill.skillType))
        skillName.wrap = true
        skillName.alignment = Align.center
        val description = TextraLabel(skill.description, Fonts.MATCHUP, Color.BLACK)
        description.wrap = true
        description.alignment = Align.left

        val skillDetailsTable: Table = scene2d.table {
            add(skillDetailsImage).size(80f).colspan(10).padTop(8f)
            row().space(16f)
            add(skillName).growX().center().colspan(10)
            row().space(16f)
            add(description).growX().padLeft(16f).padRight(16f).colspan(10)

            row().padTop(24f)
            row().space(8f).padBottom(0f)

            for (data in skill.printableData) {
                val dataLabel = TextraLabel(data.first, Fonts.MATCHUP, Color.BLACK)
                dataLabel.alignment = Align.left
                dataLabel.wrap = true
                val valueLabel = TextraLabel(data.second.toString(), Fonts.MATCHUP, Color.BLACK)
                add(dataLabel).padLeft(16f).growX()
                add(valueLabel).spaceRight(16f)
                row().space(8f)
            }

            if (skill !is Skill.PassiveSkill)
                return@table

            row().padTop(16f)
            row().space(8f).padBottom(0f)

            add(TextraLabel("Requirements", Fonts.EQUIPMENT, Color.BLACK)).expandX().center().colspan(10)

            row().padTop(16f)
            row().space(8f).padBottom(0f)

            for (data in skill.requirement.getPrintable(true)) {
                val dataLabel = TextraLabel(data.first, Fonts.MATCHUP, Color.BLACK)
                dataLabel.alignment = Align.left
                val valueLabel = TextraLabel(data.second, Fonts.MATCHUP, Color.BLACK)
                add(dataLabel).padLeft(16f).growX()
                add(valueLabel).spaceRight(16f)
                row().space(8f)
            }
        }
        skillDetailsTable.top()
        skillDetailsTable.pack()
        skillDetailsTable.layout()
        skillDetailsTable.setSize(detailsPane.width, detailsPane.height)
        detailsPane.actor = skillDetailsTable
        detailsPane.scrollTo(0f, 10000f, 0f, 0f)
    }

    /**
     * Returns the passive skill under the provided x y
     * Provide parent stage coordinates
     */
    private fun getSkillAt(x: Float, y: Float, pane: ScrollPane): Actor? {
        val coord = pane.actor.parentToLocalCoordinates(
            pane.stageToLocalCoordinates(Vector2(x, y))
        )

        for (child in (pane.actor as Group).children.reversed()) {
            if (child.isIn(coord.x, coord.y))
                return child
        }
        return null
    }

    fun initialize(border: Image) {
        this.border = border
        name = "skills"
        addActor(Image(uiElements["Background"]))

        addSkills()

        addSkillTreeTop()
        prepareSkillTree(strengthTree, "strengthTreePane")
        prepareSkillTree(dexterityTree, "dexterityTreePane")
        prepareSkillTree(intelligenceTree, "intelligenceTreePane")
        prepareSkillTree(summoningTree, "summoningTreePane")
        strengthTree.isVisible = true
        addActor(skillTrees)

        detailsPane.setPosition(skillTrees.width + 12f, 0f)
        detailsPane.width = border.width - skillTrees.width - 24f
        detailsPane.height = border.height - 24f
        detailsPane.y = 12f
        addActor(detailsPane)

        width = border.width
        height = border.height
        roundPosition()

        refreshSkills()
        changeTab()

        GlobalData.registerObserver(object : GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERNEWSKILL
            override fun update(data: Any?): Boolean {
                refreshSkillTable()
                return true
            }
        })
    }

    private fun getSkillTable(): Table {
        var rows = Player.skillList.size / 10 + if (Player.skillList.size % 10 != 0) 1 else 0
        rows = if (rows < 6) 6 else rows

        val table = scene2d.table {
            this.setFillParent(false)
            clip(true)
            for (n in 0 until rows) {
                for (i in 0 until 6) {
                    add(container {
                        val cellNumber = n * 6 + i
                        name = (cellNumber).toString()
                        background = getSkillsCellDrawable(cellNumber, cellNumber)
                        align(Align.bottomLeft)
                    }).size(84f, 84f).space(0f)
                }
                row().space(0f)
            }
        }
        table.pack()
        return table
    }


    fun refreshSkillTable() {
        if (Player.skillList.size > (skillTable.actor as Table).children.size)
            skillTable.actor = getSkillTable()

        for (i in 0 until Player.skillList.size) {
            ((skillTable.actor as Table).children[i] as Container<*>).actor = SkillActor(Player.skillList[i])
        }
    }

    private fun addSkills() {
        skillTable.name = "skillTable"
        skillTable.setScrollingDisabled(true, false)
        skillTable.setOverscroll(false, false)
        skillTable.setScrollbarsVisible(false)
        skillTable.layout()

        addActor(skillTable)
        skillTable.width = border.width / 2 + 84 - 10
        skillTable.height = border.height - 2 * 12 + 4
        skillTable.setPosition(x + 12, y + 12)
        skillTable.isVisible = false
    }

    private fun addSkillTreeTop() {
        skillTrees.width = border.width / 2 + 84 - 10
        skillTrees.height = border.height - 2 * 12
        skillTrees.setPosition(x + 12, y + 12)

        val leftButton = scene2d.textButton("<-")
        val rightButton = scene2d.textButton("->")
        leftButton.name = "leftButton"
        rightButton.name = "rightButton"
        val treeNameText = TextraLabel(treeNameList[currentTree], Fonts.EQUIPMENT, Color.BLACK)
        treeNameText.alignment = Align.center
        treeNameText.name = "treeNameText"

        val treeMoveGroup: Table = scene2d.table {
            pad(0f)
            add(leftButton).left()
            add(treeNameText).growX().center()
            add(rightButton).padLeft(10f).padRight(10f)
        }
        treeMoveGroup.pack()
        treeMoveGroup.layout()
        treeMoveGroup.width = skillTrees.width
        treeMoveGroup.height = 34f
        treeMoveGroup.y = skillTrees.height - treeMoveGroup.height
        skillTrees.addActor(treeMoveGroup)

        treeMoveGroup.name = "treeMoveGroup"
    }

    private fun getSkillTree(skillTreeType: SkillTree, index: Int): Group {
        val tree = Group()
        var treeY = 4f
        var treeX = 0f

        for (list in skillTreeType.skills.reversed()) {
            for (passive in list) {
                if (passive is SkillTree.Pad)
                    treeX += passive.get()
                else {
                    val actor = SkillTreeActor(passive)
                    actor.setSize(84f, 84f)
                    actor.name = passive::class.simpleName
                    tree.addActor(actor)
                    actor.setPosition(treeX, treeY)
                    treeX += 84f
                }
            }
            treeY += 84f + 42f
            treeX = 0f
        }
        tree.height = treeY - 42f

        val treeLines = ArrayList<LinePoints>()

        for (i in 1 until skillTreeType.skills.size) {
            for (passive in skillTreeType.skills[i]) {
                if (passive is SkillTree.Pad)
                    continue

                val actorTo = tree.findActor<SkillTreeActor>(passive::class.simpleName)
                for (requirement in passive.playerRequirements) {
                    val actorFrom = tree.findActor<SkillTreeActor>(requirement.first.simpleName)
                    treeLines.add(LinePoints(actorFrom.fromX(), actorFrom.fromY(), actorTo.toX(), actorTo.toY() - 2f))
                }
            }
        }

        val treeLineActor = TreeLineActor(treeLines, getTreeColor(index))
        treeLineActor.width = skillTrees.width
        treeLineActor.height = tree.height
        tree.addActor(treeLineActor)
        treeLineActor.zIndex = 0

        return tree
    }

    private fun getTreeColor(currentTree: Int): Color {
        when (currentTree) {
            0 -> return ColorUtils.STRENGTH
            1 -> return ColorUtils.DEXTERITY
            2 -> return ColorUtils.INTELLIGENCE
            3 -> return ColorUtils.SUMMONING
        }
        return Color.WHITE
    }

    private fun getTreeColor(skillType: SkillType): Color {
        return when (skillType) {
            SkillType.STRENGTH -> ColorUtils.STRENGTH
            SkillType.DEXTERITY -> ColorUtils.DEXTERITY
            SkillType.INTELLIGENCE -> ColorUtils.INTELLIGENCE
            SkillType.SUMMONING -> ColorUtils.SUMMONING
        }
    }


    private fun prepareSkillTree(skillTreeScroll: ScrollPane, name: String) {
        skillTreeScroll.name = name
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        skillTreeScroll.setScrollingDisabled(true, false)
        skillTreeScroll.setOverscroll(false, false)
        skillTreeScroll.setScrollbarsVisible(false)
        skillTreeScroll.layout()

        skillTreeScroll.width = skillTrees.width
        skillTreeScroll.height = skillTrees.height - 34f
        skillTrees.addActor(skillTreeScroll)
        skillTreeScroll.layout()
        skillTreeScroll.scrollTo(0f, 10000f, 0f, 0f)
        skillTreeScroll.isVisible = false
    }

    fun refreshSkills() {
        (skillTable.actor as Table).children.forEach {
            (it as Container<*>).actor = null
            val cellNumber = it.name.toInt()
            if (cellNumber < Player.skillList.size)
                it.actor = SkillActor(Player.skillList[cellNumber])
        }
    }

    private fun getSkillsCellDrawable(cellNumber: Int, rows: Int): Drawable {
        if (cellNumber == 0)
            return TextureRegionDrawable(uiElements["cellTopLeft"])
        if (cellNumber < 6)
            return TextureRegionDrawable(uiElements["cellTop"])
        val bottomRowNumber = cellNumber - (rows - 1) * 6
        if (bottomRowNumber == 0 )
            return TextureRegionDrawable(uiElements["cellBottomLeft"])
        if (bottomRowNumber in 1..5 )
            return TextureRegionDrawable(uiElements["cellBottom"])
        if (cellNumber % 6 == 0)
            return TextureRegionDrawable(uiElements["cellLeft"])
        return TextureRegionDrawable(uiElements["cellMiddle"])
    }

    private class TreeLineActor(private val lineList: ArrayList<LinePoints>, private val lineColor: Color): Actor() {
        private val textureRegion: TextureRegion = TextureRegion(Constants.WhitePixel, 0, 0, 1, 1)
        private var drawer: ShapeDrawer? = null
        init {
            name = "treeLines"
        }

        override fun draw(batch: Batch?, parentAlpha: Float) {
            if (drawer == null) {
                drawer = ShapeDrawer(batch, textureRegion)
                drawer!!.setColor(lineColor)
            }

            for (points in lineList) {
                drawer!!.line(points.from, points.to, 8f)
            }
        }
    }

    private class LinePoints(
        private val fromX: Float,
        private val fromY: Float,
        private val toX: Float,
        private val toY: Float
    ) {
        val from = Vector2(fromX, fromY)
        val to = Vector2(toX, toY)
    }
}