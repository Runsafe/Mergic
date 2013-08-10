package no.runsafe.mergic;

import no.runsafe.framework.api.event.player.*;
import no.runsafe.framework.api.event.plugin.IPluginDisabled;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageEvent;
import no.runsafe.framework.minecraft.event.player.*;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.*;

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
				RunsafePlayer player = event.getPlayer();

				// Check if the player is actually in the game.
				if (arena.playerIsInGame(player))
					this.game.removePlayerFromGame(player); // Throw them from the game.
			}
		}
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		RunsafePlayer player = event.getPlayer();

		// Check if the player is inside the arena when they shouldn't be.
		if (!this.arena.playerIsInGame(player) && this.arena.playerIsInPhysicalArena(player))
			this.lobby.teleportPlayerToLobby(player); // Teleport them to the lobby!
	}

	@Override
	public void OnPlayerDamage(RunsafePlayer player, RunsafeEntityDamageEvent event)
	{
		if (this.arena.playerIsInGame(player) && player.getHealth() - event.getDamage() <= 0D)
		{
			event.setDamage(0); // Cancel the incoming damage.
			this.graveyard.teleportPlayerToGraveyard(player); // Teleport player to graveyard.
			player.setHealth(20D); // Heal the player to full.
			player.setFireTicks(0); // Stop the fire from burning if they are.
			player.setFoodLevel(20); // Fill the hunger bar back to full.
			player.sendColouredMessage("&cYou have died! You will respawn shortly."); // Explain to them.
			this.killManager.OnPlayerKilled(player); // Trigger event in kill manager to tally score.
		}
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		RunsafePlayer player = event.getPlayer();

		// Check the player is registered as playing the game.
		if (this.arena.playerIsInGame(player) && !this.graveyard.playerIsInGraveyard(player))
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

	private Graveyard graveyard;
	private Arena arena;
	private Game game;
	private Lobby lobby;
	private SpellHandler spellHandler;
	private CooldownManager cooldownManager;
	private KillManager killManager;
}
