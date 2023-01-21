package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.*
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.SkillTree
import com.neutrino.game.domain.model.systems.skills.SkillActor
import ktx.actors.setScrollFocus
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

internal class Skills(private val uiElements: Map<String, TextureAtlas.AtlasRegion>): Group() {

    private lateinit var border: Image
    private val skillTable: ScrollPane = ScrollPane(getSkillTable())
    private val skillTrees: Group = Group()

    private val strengthTree: ScrollPane = ScrollPane(getSkillTree(SkillTree.STRENGTH))
    private val dexterityTree: ScrollPane = ScrollPane(getSkillTree(SkillTree.DEXTERITY))
    private val intelligenceTree: ScrollPane = ScrollPane(getSkillTree(SkillTree.INTELLIGENCE))
    var treeList = listOf(strengthTree, dexterityTree, intelligenceTree)
    var treeNameList = listOf("STRENGTH", "DEXTERITY", "INTELLIGENCE")

    var currentTab: Actor = skillTable
        private set

    fun scrollFocus() {
        if (currentTab == skillTable) {
            skillTable.setScrollFocus(true)
            return
        }
        treeList[currentTree].setScrollFocus(true)
    }

    fun changeTab() {
        currentTab.isVisible = false
        if (currentTab == skillTable) {
            currentTab = skillTrees
            treeList[currentTree].setScrollFocus(true)
        }
        else if (currentTab == skillTrees) {
            currentTab = skillTable
            skillTable.setScrollFocus(true)
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

    fun parseClick(xClick: Float, yClick: Float) {
        val coord: Vector2 = stageToLocalCoordinates(
            Vector2(xClick, yClick)
        )
        if (!skillTrees.isIn(coord.x, coord.y))
            return

        val menuCoord = localToActorCoordinates(skillTrees.findActor("treeMoveGroup"), coord)

        if (skillTrees.findActor<TextButton>("leftButton").isIn(menuCoord.x, menuCoord.y))
            changeTree(-1)

        if (skillTrees.findActor<TextButton>("rightButton").isIn(menuCoord.x, menuCoord.y))
            changeTree(1)
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
        strengthTree.isVisible = true
        addActor(skillTrees)

        width = border.width
        height = border.height
        isVisible = false
        roundPosition()

        refreshSkills()
        changeTab()
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

    private fun getSkillTree(skillTreeType: SkillTree): Group {
        val tree = Group()
        var treeY = 4f
        var treeX = 0f

        for (list in skillTreeType.skills.reversed()) {
            for (passive in list) {
                if (passive is SkillTree.Pad)
                    treeX += passive.get()
                else {
                    val actor = SkillActor(passive)
                    actor.setSize(84f, 84f)
                    tree.addActor(actor)
                    actor.setPosition(treeX, treeY)
                    treeX += 84f
                }
            }
            treeY += 84f + 42f
            treeX = 0f
        }
        tree.height = treeY - 42f
        return tree
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

    private fun Actor.isIn(x: Float, y: Float) = (x.compareDelta(this.x) >= 0 && x.compareDelta(this.x + this.widthScaled()) <= 0 &&
            y.compareDelta(this.y) >= 0 && y.compareDelta(this.y + this.heightScaled()) <= 0)
}