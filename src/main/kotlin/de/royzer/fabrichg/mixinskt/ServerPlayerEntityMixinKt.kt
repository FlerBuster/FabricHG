package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.isKitItem
import de.royzer.fabrichg.sendPlayerStatus
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object ServerPlayerEntityMixinKt {
    fun onDamage(damageSource: DamageSource, amount: Float, cir: CallbackInfoReturnable<Boolean>, serverPlayerEntity: ServerPlayer) {
    }

    fun onDropSelectedItem(entireStack: Boolean, cir: CallbackInfoReturnable<ItemEntity>, serverPlayerEntity: ServerPlayer) {
        val stack = serverPlayerEntity.mainHandItem
        if (GamePhaseManager.currentPhaseType == PhaseType.LOBBY) {
            serverPlayerEntity.sendPlayerStatus()
            cir.returnValue = null
            return
        }
        serverPlayerEntity.hgPlayer.kits.forEach { kit ->
            if (stack.item in kit.kitItems.filterNot { it.droppable }.map { it.itemStack.item } && stack.isKitItem) {
                serverPlayerEntity.sendPlayerStatus()
                cir.returnValue = null
            }
        }
    }
}