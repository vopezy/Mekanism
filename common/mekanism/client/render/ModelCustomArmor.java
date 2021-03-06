package mekanism.client.render;

import mekanism.client.model.ModelJetpack;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class ModelCustomArmor extends ModelBiped
{
	public static ModelCustomArmor INSTANCE = new ModelCustomArmor();
	public static Minecraft mc = Minecraft.getMinecraft();

	public ArmorModel modelType;
	
	public ModelCustomArmor()
	{
		resetPart(bipedHead, 0, 0, 0);
		resetPart(bipedBody, 0, 0, 0);
		resetPart(bipedRightArm, 5, 2, 0);
		resetPart(bipedLeftArm, -5, 2, 0);
		resetPart(bipedRightLeg, 2, 12, 0);
		resetPart(bipedLeftLeg, -2, 12, 0);

		bipedHeadwear.cubeList.clear();
		bipedEars.cubeList.clear();
		bipedCloak.cubeList.clear();
	}

	public void init(Entity entity, float f, float f1, float f2, float f3, float f4, float size)
	{
		reset();

		if(modelType.armorSlot == 0)
		{
			bipedHead.isHidden = false;
			bipedHead.showModel = true;
		}
		else if(modelType.armorSlot == 1)
		{
			bipedBody.isHidden = false;
			bipedBody.showModel = true;
		}
		else if(modelType.armorSlot == 2)
		{
			bipedRightLeg.showModel = true;
			bipedLeftLeg.showModel = true;
		}

		setRotationAngles(f, f1, f2, f3, f4, size, entity);
	}
	
	public void reset()
	{
		bipedHead.isHidden = true;
		bipedBody.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedLeftLeg.isHidden = true;
		
		bipedHead.showModel = false;
		bipedBody.showModel = false;
		bipedRightArm.showModel = false;
		bipedLeftArm.showModel = false;
		bipedRightLeg.showModel = false;
		bipedLeftLeg.showModel = false;
	}

	public void resetPart(ModelRenderer renderer, float x, float y, float z)
	{
		renderer.cubeList.clear();
		ModelCustom model = new ModelCustom(this, renderer);
		renderer.addChild(model);
		setOffset(renderer, x, y, z);
	}

	public void setOffset(ModelRenderer renderer, float x, float y, float z)
	{
		renderer.offsetX = x;
		renderer.offsetY = y;
		renderer.offsetZ = z;
	}

	public class ModelCustom extends ModelRenderer
	{
		public ModelCustomArmor biped;
		public ModelRenderer partRender;
		
		public ModelCustom(ModelCustomArmor base, ModelRenderer renderer)
		{
			super(base);
			biped = base;
			partRender = renderer;
		}

		@Override
		public void render(float size)
		{	
			if(ModelCustomArmor.this.modelType != null)
			{
				GL11.glPushMatrix();
				GL11.glTranslatef(0, 0, 0.06F);
				
				mc.renderEngine.bindTexture(modelType.resource);
				
				if(ModelCustomArmor.this.modelType == ArmorModel.JETPACK && biped.bipedBody == partRender)
				{
					ArmorModel.jetpackModel.render(0.0625F);
				}
				
				GL11.glPopMatrix();
			}
		}
	}
	
	@Override
	public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		init(entity, par2, par3, par4, par5, par6, par7);
		super.render(entity, par2, par3, par4, par5, par6, par7);
	}

	public static enum ArmorModel 
	{
		JETPACK(1, MekanismUtils.getResource(ResourceType.RENDER, "Jetpack.png"));

		public int armorSlot;
		public ResourceLocation resource;
		
		public static ModelJetpack jetpackModel = new ModelJetpack();

		private ArmorModel(int i, ResourceLocation r)
		{
			armorSlot = i;
			resource = r;
		}
	}
}
