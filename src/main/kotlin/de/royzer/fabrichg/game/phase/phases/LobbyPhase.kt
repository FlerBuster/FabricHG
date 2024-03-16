package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.scoreboard.formattedTime
import de.royzer.fabrichg.util.getRandomHighestPos
import net.silkmc.silk.core.text.literalText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects

object LobbyPhase : GamePhase() {
    override val phaseType = PhaseType.LOBBY
    override val maxPhaseTime = 60 * 3
    override val nextPhase = InvincibilityPhase

    var isStarting = false

    override fun init() {
        GamePhaseManager.server.isPvpAllowed = false
    }

    override fun tick(timer: Int) {
        val timeLeft = maxPhaseTime - timer

        if (PlayerList.players.size >= 2) {
            when (timeLeft) {
                15 -> {
                    isStarting = true
                    PlayerList.alivePlayers.forEach {
                        val pos = getRandomHighestPos(20)
                        it.serverPlayer?.teleportTo(
                            pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()
                        )
//                        it.serverPlayer?.freeze()
                    }
                }
                180, 120, 60, 30, 10, 5, 4, 3, 2, 1 -> broadcastComponent(literalText("Das Spiel start in ") {
                    color = TEXT_GRAY
                    text(timeLeft.formattedTime) { color = TEXT_BLUE }
                    text(" Minuten")
                })
                0 -> {
                    isStarting = false
                    startNextPhase()
                }
            }
            if (timeLeft > 15 && isStarting) {
                isStarting = false
            }
        } else {
            isStarting = false
            GamePhaseManager.resetTimer()
            PlayerList.alivePlayers.forEach { hgPlayer ->
                hgPlayer.serverPlayer?.removeAllEffects()
            }
        }
    }
}

fun ServerPlayer.freeze() {
    this.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15 * 20, 255, false, false))
    this.addEffect(MobEffectInstance(MobEffects.JUMP, 15 * 20, 255, false, false))
}