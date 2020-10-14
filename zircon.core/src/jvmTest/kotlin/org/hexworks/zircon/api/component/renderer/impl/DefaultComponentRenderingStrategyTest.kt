package org.hexworks.zircon.api.component.renderer.impl

import org.assertj.core.api.Assertions.assertThat
import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ComponentDecorations.box
import org.hexworks.zircon.api.ComponentDecorations.shadow
import org.hexworks.zircon.api.ComponentDecorations.side
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.ComponentStyleSet
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.data.ComponentMetadata
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.fetchCharacters
import org.hexworks.zircon.internal.component.impl.DefaultButton
import org.hexworks.zircon.internal.component.impl.DefaultLabel
import org.hexworks.zircon.internal.component.renderer.DefaultButtonRenderer
import org.hexworks.zircon.internal.component.renderer.DefaultComponentRenderingStrategy
import org.hexworks.zircon.internal.component.renderer.DefaultLabelRenderer
import org.hexworks.zircon.internal.component.renderer.decoration.BoxDecorationRenderer
import org.hexworks.zircon.internal.component.renderer.decoration.ShadowDecorationRenderer
import org.junit.Before
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class DefaultComponentRenderingStrategyTest {

    lateinit var target: DefaultComponentRenderingStrategy<Button>

    @Before
    fun setUp() {
        target = DefaultComponentRenderingStrategy(
                decorationRenderers = listOf(
                        ShadowDecorationRenderer(),
                        BoxDecorationRenderer()),
                componentRenderer = DefaultButtonRenderer() as ComponentRenderer<Button>)
    }

    @Test
    fun shouldRenderButtonWithDecorations() {
        val size = Size.create(8, 4)
        val graphics = TileGraphicsBuilder.newBuilder()
                .withSize(size)
                .build().apply {
                    fill(Tile.defaultTile().withCharacter('_'))
                }

        val target: DefaultComponentRenderingStrategy<Button> = DefaultComponentRenderingStrategy(
                decorationRenderers = listOf(shadow(), box(), side()),
                componentRenderer = DefaultButtonRenderer() as ComponentRenderer<Button>)

        val btn = DefaultButton(
                componentMetadata = ComponentMetadata(
                        tileset = CP437TilesetResources.aduDhabi16x16(),
                        size = size,
                        relativePosition = Position.defaultPosition(),
                        componentStyleSet = ComponentStyleSet.defaultStyleSet()),
                initialText = "qux",
                renderingStrategy = target)

        target.render(btn, graphics)

        assertThat(graphics.tiles.values.map { it.asCharacterTile().get().character }).containsExactly(
                '┌', '─', '─', '─', '─', '─', '┐', '_',
                '│', '[', 'q', 'u', 'x', ']', '│', '░',
                '└', '─', '─', '─', '─', '─', '┘', '░',
                '_', '░', '░', '░', '░', '░', '░', '░')

    }

    @Test
    fun shouldNotRenderButtonWhenItIsInvisible() {
        val size = Size.create(8, 4)
        val graphics = TileGraphicsBuilder.newBuilder()
                .withSize(size)
                .build()

        val target: DefaultComponentRenderingStrategy<Button> = DefaultComponentRenderingStrategy(
                decorationRenderers = listOf(shadow(), box(), side()),
                componentRenderer = DefaultButtonRenderer() as ComponentRenderer<Button>)

        val btn = DefaultButton(
                componentMetadata = ComponentMetadata(
                        tileset = CP437TilesetResources.aduDhabi16x16(),
                        size = size,
                        relativePosition = Position.defaultPosition(),
                        componentStyleSet = ComponentStyleSet.defaultStyleSet()),
                initialText = "qux",
                renderingStrategy = target)

        btn.isHidden = true

        target.render(btn, graphics)

        assertThat(graphics.fetchCharacters())
                .containsExactlyElementsOf(MutableList(32) { ' ' })
    }

    @Test
    fun shouldProperlyRenderComponentWithoutDecorations() {
        val size = Size.create(5, 5)
        val graphics = TileGraphicsBuilder.newBuilder()
                .withSize(size)
                .build()
                .apply {
                    fill(Tile.defaultTile().withCharacter('_'))
                }

        val label = DefaultLabel(
                componentMetadata = ComponentMetadata(
                        tileset = CP437TilesetResources.aduDhabi16x16(),
                        size = size,
                        relativePosition = Position.defaultPosition(),
                        componentStyleSet = ComponentStyleSet.defaultStyleSet()),
                initialText = "Long text",
                renderingStrategy = DefaultComponentRenderingStrategy(
                        decorationRenderers = listOf(),
                        componentRenderer = DefaultLabelRenderer() as ComponentRenderer<Label>))

        val target = DefaultComponentRenderingStrategy(
                decorationRenderers = listOf(),
                componentRenderer = DefaultLabelRenderer())

        target.render(label, graphics)
    }

    @Test
    fun `Should properly render decorations and the component on a filled TileGraphics`() {

        val size = Size.create(5, 5)
        val graphics = TileGraphicsBuilder.newBuilder()
                .withSize(size)
                .build()
                .apply {
                    fill(Tile.defaultTile().withCharacter('_'))
                }

        val button = DefaultButton(
                componentMetadata = ComponentMetadata(
                        tileset = CP437TilesetResources.aduDhabi16x16(),
                        size = size,
                        relativePosition = Position.defaultPosition(),
                        componentStyleSet = ComponentStyleSet.defaultStyleSet()),
                initialText = "foo",
                renderingStrategy = target)

        target.render(button, graphics)

        assertThat(graphics.tiles.values.map { it.asCharacterTile().get().character }).containsExactly(
                '┌', '─', '─', '┐', '_',
                '│', 'f', 'o', '│', '░',
                '│', 'o', ' ', '│', '░',
                '└', '─', '─', '┘', '░',
                '_', '░', '░', '░', '░')
    }

    @Test
    fun `Should properly render decorations and the component on a blank TileGraphics`() {

        val size = Size.create(4, 4)
        val graphics = TileGraphicsBuilder.newBuilder()
                .withSize(size)
                .build()

        val button = DefaultButton(
                componentMetadata = ComponentMetadata(
                        tileset = CP437TilesetResources.aduDhabi16x16(),
                        size = size,
                        relativePosition = Position.defaultPosition(),
                        componentStyleSet = ComponentStyleSet.defaultStyleSet()),
                initialText = "bar",
                renderingStrategy = target)

        target.render(button, graphics)

        assertThat(graphics.fetchCharacters()).containsExactly(
                '┌', '─', '┐', ' ',
                '│', 'b', '│', '░',
                '└', '─', '┘', '░',
                ' ', '░', '░', '░')
    }
}
