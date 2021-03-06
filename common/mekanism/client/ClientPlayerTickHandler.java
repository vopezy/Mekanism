package mekanism.client;

import java.util.EnumSet;

import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.PacketHandler.Transmission;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.item.ItemElectricBow;
import mekanism.common.item.ItemJetpack;
import mekanism.common.item.ItemWalkieTalkie;
import mekanism.common.network.PacketConfiguratorState;
import mekanism.common.network.PacketElectricBowState;
import mekanism.common.network.PacketJetpackData;
import mekanism.common.network.PacketWalkieTalkieState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientPlayerTickHandler implements ITickHandler
{	
	public boolean lastTickUpdate = false;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if(tickData[0] instanceof EntityPlayer)
		{
			EntityPlayer entityPlayer = (EntityPlayer)tickData[0];
			
			if(entityPlayer.getCurrentEquippedItem() != null)
			{
				ItemStack stack = entityPlayer.getCurrentEquippedItem();
				
				if(entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemConfigurator)
				{
					ItemConfigurator item = (ItemConfigurator)entityPlayer.getCurrentEquippedItem().getItem();
					
		    		if(entityPlayer.isSneaking() && MekanismKeyHandler.modeSwitchKey.pressed)
		    		{
		    			if(!lastTickUpdate)
		    			{
			    			item.setState(stack, (byte)(item.getState(stack) < 3 ? item.getState(stack)+1 : 0));
			    			PacketHandler.sendPacket(Transmission.SERVER, new PacketConfiguratorState().setParams(item.getState(stack)));
			    			entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText(EnumColor.DARK_BLUE + "[Mekanism] " + EnumColor.GREY + "Configure State: " + item.getColor(item.getState(stack)) + item.getStateDisplay(item.getState(stack))));
			    			lastTickUpdate = true;
		    			}
		    		}
		    		else {
		    			lastTickUpdate = false;
		    		}
				}
				else if(entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemElectricBow)
				{
					ItemElectricBow item = (ItemElectricBow)entityPlayer.getCurrentEquippedItem().getItem();
					
					if(entityPlayer.isSneaking() && MekanismKeyHandler.modeSwitchKey.pressed)
					{
						if(!lastTickUpdate)
						{
							item.setFireState(stack, !item.getFireState(stack));
							PacketHandler.sendPacket(Transmission.SERVER, new PacketElectricBowState().setParams(item.getFireState(stack)));
							entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText(EnumColor.DARK_BLUE + "[Mekanism] " + EnumColor.GREY + "Fire Mode: " + (item.getFireState(stack) ? (EnumColor.DARK_GREEN + "ON") : (EnumColor.DARK_RED + "OFF"))));
							lastTickUpdate = true;
						}
					}
					else {
						lastTickUpdate = false;
					}
				}
				else if(entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemWalkieTalkie)
				{
					ItemWalkieTalkie item = (ItemWalkieTalkie)entityPlayer.getCurrentEquippedItem().getItem();
					
					if(entityPlayer.isSneaking() && MekanismKeyHandler.modeSwitchKey.pressed && item.getOn(stack))
					{
						if(!lastTickUpdate)
						{
							int newChan = item.getChannel(stack) < 9 ? item.getChannel(stack)+1 : 1;
							item.setChannel(stack, newChan);
							PacketHandler.sendPacket(Transmission.SERVER, new PacketWalkieTalkieState().setParams(newChan));
							Minecraft.getMinecraft().sndManager.playSoundFX("mekanism:etc.Ding", 1.0F, 1.0F);
							lastTickUpdate = true;
						}
					}
					else {
						lastTickUpdate = false;
					}
				}
				else {
					lastTickUpdate = false;
				}
			}
			
			if(Mekanism.jetpackOn.contains(entityPlayer) != isJetpackOn(entityPlayer))
			{
				if(isJetpackOn(entityPlayer))
				{
					Mekanism.jetpackOn.add(entityPlayer);
				}
				else {
					Mekanism.jetpackOn.remove(entityPlayer);
				}
				
				PacketHandler.sendPacket(Transmission.SERVER, new PacketJetpackData().setParams(entityPlayer, isJetpackOn(entityPlayer)));
			}
			
			for(EntityPlayer entry : Mekanism.jetpackOn)
			{
				Mekanism.proxy.registerSound(entry);
			}
		}
	}
	
	private boolean cacheJetpackOn(EntityPlayer player)
	{
		return Mekanism.jetpackOn.contains(player);
	}
	
	public static boolean isJetpackOn(EntityPlayer player)
	{
		if(player.inventory.armorInventory[2] != null)
		{
			if(player.inventory.armorInventory[2].getItem() instanceof ItemJetpack)
			{
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return "MekanismClientPlayer";
	}
}
