package dev.vini2003.hammer.gui.api.common.widget.image;

import dev.vini2003.hammer.core.HC;
import dev.vini2003.hammer.core.api.client.texture.PartitionedTexture;
import dev.vini2003.hammer.core.api.client.texture.base.Texture;
import dev.vini2003.hammer.gui.api.common.widget.Widget;
import dev.vini2003.hammer.gui.api.common.widget.provider.TextureProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Supplier;

public class ImageWidget extends Widget implements TextureProvider {
	public static final Texture STANDARD_TEXTURE = new PartitionedTexture(HC.id("textures/widget/slot.png"), 18.0F, 18.0F, 0.055F, 0.055F, 0.055F, 0.055F);
	
	protected Supplier<Texture> texture = () -> STANDARD_TEXTURE;
	
	@Override
	public void draw(MatrixStack matrices, VertexConsumerProvider provider, float tickDelta) {
		texture.get().draw(matrices, provider, getX(), getY(), getWidth(), getHeight());
	}
	
	@Override
	public Supplier<Texture> getTexture() {
		return texture;
	}
	
	@Override
	public void setTexture(Supplier<Texture> texture) {
		this.texture = texture;
	}
}
