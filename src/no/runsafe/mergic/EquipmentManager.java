package no.runsafe.mergic;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Enchant;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;
import no.runsafe.framework.minecraft.item.meta.RunsafeLeatherArmor;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

public class EquipmentManager
{
	private EquipmentManager()
	{
	}

	public static void givePlayerWizardBoots(IPlayer player)
	{
		// Get a random colour, how exciting.
		String randomColour = String.format(
			"%02x%02x%02x",
			(int) (Math.random() * 255),
			(int) (Math.random() * 255),
			(int) (Math.random() * 255)
		);

		RunsafeMeta boots = Item.Combat.Boots.Leather.getItem(); // Create some boots.
		boots.setDisplayName("Wizard Boots"); // Rename the booties!
		Enchant.FallProtection.power(Enchant.FallProtection.getMaxLevel()).applyTo(boots); // Enchant the boots.
		RunsafeLeatherArmor armour = (RunsafeLeatherArmor) boots;
		armour.setColor(Integer.valueOf(randomColour, 16)); // Colour the boots!

		player.setBoots(boots); // Put the boots on the player.
	}

	public static void repairBoots(IPlayer player)
	{
		RunsafeMeta boots = player.getBoots();
		if (boots != null)
			boots.setDurability((short) 0);
	}

	private static int getArmourColour(IPlayer player)
	{
		RunsafeMeta boots = player.getBoots();
		if(boots == null)
			return 0;
		RunsafeLeatherArmor armour = (RunsafeLeatherArmor) boots;

		return armour.getColor();
	}

	public static void applyFullProtection(IPlayer player)
	{
		RunsafeInventory inventory = player.getInventory();
		if (inventory == null)
			return;

		RunsafeMeta helmet = Item.Combat.Helmet.Leather.getItem();
		RunsafeMeta chest = Item.Combat.Chestplate.Leather.getItem();
		RunsafeMeta leggings = Item.Combat.Leggings.Leather.getItem();

		helmet.setDisplayName("Conjured Helmet");
		chest.setDisplayName("Conjured Chestplate");
		leggings.setDisplayName("Conjured Leggings");

		RunsafeLeatherArmor leatherHelmet = (RunsafeLeatherArmor) helmet;
		RunsafeLeatherArmor leatherChest = (RunsafeLeatherArmor) chest;
		RunsafeLeatherArmor leatherLeggings = (RunsafeLeatherArmor) leggings;

		int colour = getArmourColour(player);
		leatherHelmet.setColor(colour);
		leatherChest.setColor(colour);
		leatherLeggings.setColor(colour);

		Enchant.EnvironmentalProtection.applyTo(leatherHelmet).applyTo(leatherChest).applyTo(leatherLeggings);
		Enchant.FireProtection.applyTo(leatherHelmet).applyTo(leatherChest).applyTo(leatherLeggings);
		Enchant.Durability.applyTo(leatherHelmet).applyTo(leatherChest).applyTo(leatherLeggings);
		Enchant.ProjectileProtection.applyTo(leatherHelmet).applyTo(leatherChest).applyTo(leatherLeggings);

		player.setHelmet(leatherHelmet);
		player.setChestplate(leatherChest);
		player.setLeggings(leatherLeggings);

		player.updateInventory();
	}

	public static void removeFullProtection(IPlayer player)
	{
		player.setChestplate(AIR);
		player.setLeggings(AIR);
		player.setHelmet(AIR);
	}

	private static final RunsafeMeta AIR = Item.Unavailable.Air.getItem();
}
