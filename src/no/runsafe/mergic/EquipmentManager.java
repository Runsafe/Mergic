package no.runsafe.mergic;

import no.runsafe.framework.minecraft.Enchant;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeLeatherArmor;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class EquipmentManager
{
	private EquipmentManager() {}

	public static void givePlayerWizardBoots(RunsafePlayer player)
	{
		// Get a random colour, how exciting.
		String randomColour = String.format(
				"%02x%02x%02x",
				(int)(Math.random() * 255),
				(int)(Math.random() * 255),
				(int)(Math.random() * 255)
		);

		RunsafeMeta boots = Item.Combat.Boots.Leather.getItem(); // Create some boots.
		boots.setDisplayName("Wizard Boots"); // Rename the booties!
		Enchant.FallProtection.power(Enchant.FallProtection.getMaxLevel()).applyTo(boots); // Enchant the boots.
		RunsafeLeatherArmor armour = (RunsafeLeatherArmor) boots;
		armour.setColor(Integer.valueOf(randomColour, 16)); // Colour the boots!

		player.getInventory().setBoots(boots); // Put the boots on the player.
	}

	public static void repairBoots(RunsafePlayer player)
	{
		player.getInventory().getBoots().setDurability((short) 65);
	}
}
