package de.royzer.fabrichg.game

import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import net.silkmc.silk.core.text.literalText
import net.minecraft.network.chat.Component
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.world.level.GameRules
import net.silkmc.silk.core.task.mcCoroutineTask
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

object GamePhaseManager {
    lateinit var server: DedicatedServer
    val timer = AtomicInteger()
    var currentPhase: GamePhase = LobbyPhase
    val currentPhaseType: PhaseType get() = currentPhase.phaseType

    fun enable(minecraftDedicatedServer: DedicatedServer) {
        server = minecraftDedicatedServer
        server.gameRules.getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(false, server)
        server.overworld().dayTime = 0
        server.overworld().worldBorder.size = 1000.0
        currentPhase.init()
        mcCoroutineTask(howOften = Long.MAX_VALUE, period = 1000.milliseconds, delay = 0.milliseconds) {
            currentPhase.tick(timer.getAndIncrement())
        }
    }
    fun resetTimer() = timer.set(0)

    val isBuildingForbidden get() = currentPhaseType == PhaseType.LOBBY || currentPhaseType == PhaseType.END

    val isNotStarted get() = currentPhaseType == PhaseType.LOBBY || currentPhaseType == PhaseType.INVINCIBILITY

    val isIngame get() = currentPhaseType == PhaseType.INGAME || currentPhaseType == PhaseType.INVINCIBILITY
}

fun broadcastComponent(text: Component) {
    GamePhaseManager.server.playerList.broadcastSystemMessage(text, false)// broadcastMessage(text, ChatType.SYSTEM, Util.NIL_UUID)
}

fun broadcast(textString: String) {
    broadcastComponent(literalText(textString))
}
