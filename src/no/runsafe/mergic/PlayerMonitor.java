package no.runsafe.mergic;

import no.runsafe.framework.api.event.player.IPlayerCustomEvent;
import no.runsafe.framework.api.event.player.IPlayerDamageEvent;
import no.runsafe.framework.api.event.player.IPlayerInteractEvent;
import no.runsafe.framework.api.event.player.IPlayerJoinEvent;
import no.runsafe.framework.api.event.plugin.IPluginDisabled;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageEvent;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerInteractEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerJoinEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.mergic.magic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerMonitor implements IPlayerCustomEvent, IPlayerJoinEvent, IPlayerInteractEvent, IPluginDisabled, IPlayerDamageEvent
{
	public PlayerMonitor(Graveyard graveyard, Arena arena, Game game, Lobby lobby, SpellHandler spellHandler, CooldownManager cooldownManager, KillManager killManager)
	{
		this.graveyard = graveyard;
		this.arena = arena;
		this.game = game;
		this.lobby = lobby;
		this.spellHandler = spellHandler;
		this.cooldownManager = cooldownManager;
		this.killManager = killManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		// Did we trigger a region leave event?
		if (event.getEvent().equals("region.leave"))
		{
			// See if the region the player left is the arena region.
			Map<String, String> data = (Map<String, String>) event.getData();
			if (arena.getArenaRegionString().equals(String.format("%s-%s", data.get("world"), data.get("region"))))
			{
				IPlayer player = event.getPlayer();

				// Check if the player is actually in the game.
				if (arena.playerIsInGame(player))
					this.game.removePlayerFromGame(player); // Throw them from the game.
			}
		}
		else if (event.getEvent().equals("region.enter")) // Or maybe an enter?
		{
			// See if the region the player entered is the arena region.
			Map<String, String> data = (Map<String, String>) event.getData();
			if (lobby.getLobbyRegionString().equals(String.format("%s-%s", data.get("world"), data.get("region"))))
			{
				IPlayer player = event.getPlayer();

				player.getInventory().clear(); // Clear the players inventory.
				this.spellHandler.givePlayerAllSpells(player); // Give the player all spells.
				EquipmentManager.givePlayerWizardBoots(player); // Give the player some magic boots!
				player.setLevel(this.killManager.getPlayerKills(player)); // Update the players level.
			}
		}
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		IPlayer player = event.getPlayer();

		// Check if the player is inside the arena when they shouldn't be.
		if (!this.arena.playerIsInGame(player) && this.arena.playerIsInPhysicalArena(player))
			this.lobby.teleportPlayerToLobby(player); // Teleport them to the lobby!
	}

	@Override
	public void OnPlayerDamage(IPlayer player, RunsafeEntityDamageEvent event)
	{
		if (this.arena.playerIsInGame(player) && player.getHealth() - event.getDamage() <= 0D)
		{
			event.setDamage(0); // Cancel the incoming damage.
			this.graveyard.teleportPlayerToGraveyard(player); // Teleport player to graveyard.
			player.setHealth(20D); // Heal the player to full.
			player.setFireTicks(0); // Stop the fire from burning if they are.
			player.setFoodLevel(20); // Fill the hunger bar back to full.
			EquipmentManager.repairBoots(player); // Repair the players boots.

			// If we can confirm they were killed, tell them who by, otherwise default message.
			IPlayer killer = this.killManager.getKiller(player);
			if (killer == null)
			{
				player.sendColouredMessage("&cYou have died! You will respawn shortly.");
			}
			else
			{
				player.sendColouredMessage("&cYou were killed by %s! You will respawn shortly.", killer.getName());
				killer.sendColouredMessage("&aYou killed killed %s, +1 point.", player.getName());
			}

			this.killManager.OnPlayerKilled(player); // Trigger event in kill manager to tally score.
		}
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		IPlayer player = event.getPlayer();

		// Check the player is registered as playing the game.
		if (isDebugging(player) || (this.arena.playerIsInGame(player) && !this.graveyard.playerIsInGraveyard(player)))
		{
			RunsafeMeta item = event.getItemStack();
			if (item == null)
				return;

			Spell spell = this.spellHandler.getSpellByName(item.getDisplayName()); // Grab the spell.
			if (spell != null)
			{
				SpellType type = spell.getType(); // Get the spell type.

				// If we want a left click but we're not getting it, return to cancel processing here.
				if (type.getInteractType() == InteractType.LEFT_CLICK && !event.isLeftClick())
					return;

				// If we want a right click but we're not getting it, return to cancel processing here.
				if (type.getInteractType() == InteractType.RIGHT_CLICK && !event.isRightClick())
					return;

				// Check if we have the right item and are not on cooldown for that school.
				if (item.is(type.getCastItem()) && this.cooldownManager.canCastSpell(player, spell))
				{
					spell.onCast(player); // Make the player cast the spell.
					this.cooldownManager.applySchoolCooldown(player, spell); // Apply school cooldown.
				}
			}
		}
	}

	@Override
	public void OnPluginDisabled()
	{
		// If the server shuts down, we should cancel the game just to make sure.
		this.game.cancelGame();
	}

	public boolean isDebugging(IPlayer player)
	{
		return debuggers.contains(player.getName());
	}

	public boolean toggleDebugging(IPlayer player)
	{
		boolean isDebugging = isDebugging(player);
		String playerName = player.getName();

		if (isDebugging)
			debuggers.remove(playerName);
		else
			debuggers.add(playerName);

		return !isDebugging;
	}

	private Graveyard graveyard;
	private Arena arena;
	private Game game;
	private Lobby lobby;
	private SpellHandler spellHandler;
	private CooldownManager cooldownManager;
	private KillManager killManager;
	private final List<String> debuggers = new ArrayList<String>();
}
