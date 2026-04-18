package net.zhengzhengyiyi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.Text;
import net.zhengzhengyiyi.block.ModBlocks;
import net.zhengzhengyiyi.block.NeitherPortalEntity;
import net.zhengzhengyiyi.gui.ClientUnlockState;
import net.zhengzhengyiyi.gui.DimensionControlScreen;
import net.zhengzhengyiyi.gui.PendingVoteScreen;
import net.zhengzhengyiyi.gui.UnlocksScreen;
import net.zhengzhengyiyi.gui.toast.LevelUpToast;
import net.zhengzhengyiyi.network.ClientModNetworkManager;
import net.zhengzhengyiyi.renderer.ModEntityRenderers;
import net.zhengzhengyiyi.screen.ModScreenHandlerType;
import net.zhengzhengyiyi.network.*;

public class AprilsLegacyClient implements ClientModInitializer {

	/** V — open pending vote screen */
	private final KeyBinding pendingVoteKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		"key.aprils_legacy.pending_vote",
		InputUtil.Type.KEYSYM,
		InputUtil.GLFW_KEY_V,
		KeyBinding.Category.GAMEPLAY
	));

	/** U — open unlocks screen (mirrors Craftmine key.unlocks) */
	private final KeyBinding unlocksKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		"key.unlocks",
		InputUtil.Type.KEYSYM,
		InputUtil.GLFW_KEY_U,
		KeyBinding.Category.GAMEPLAY
	));

	/** Track last known level to show level-up toast */
	private int lastKnownLevel = -1;

	/**
	 * This method is never called — packets are registered in the server-side entry point.
	 */
	public static void registerNetworkPacket() {
	    PayloadTypeRegistry.playS2C().register(voteResponsepacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), voteResponsepacket::new));
	    PayloadTypeRegistry.playS2C().register(class_8481.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8481::new));
	    PayloadTypeRegistry.playS2C().register(class_8482.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8482::new));
	    PayloadTypeRegistry.playS2C().register(class_8483.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8483::new));
	    PayloadTypeRegistry.playS2C().register(VoteRuleSyncS2CPacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteRuleSyncS2CPacket::new));
	    PayloadTypeRegistry.playS2C().register(VoteUpdateS2CPacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteUpdateS2CPacket::new));
	    PayloadTypeRegistry.playS2C().register(ClientPacket0.PAYLOAD_ID, ClientPacket0.CODEC);
	    PayloadTypeRegistry.playC2S().register(VoteCastpacket.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), VoteCastpacket::new));
	    PayloadTypeRegistry.playC2S().register(class_8484.PAYLOAD_ID, PacketCodec.of((v, b) -> v.write(b), class_8484::new));
	}

	@Override
	public void onInitializeClient() {
		CodeSkyPipeline.init();
		ClientModNetworkManager.registerReceivers();
		ModEntityRenderers.register();

		HandledScreens.register(ModScreenHandlerType.DIMENSION_CONTROL, DimensionControlScreen::new);
		HandledScreens.register(ModScreenHandlerType.MINE_CRAFTER, net.zhengzhengyiyi.gui.MineCrafterScreen::new);

		// ── Keybinding tick ────────────────────────────────────────────────────
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// V → pending vote screen
			if (pendingVoteKey.wasPressed()) {
			    if (!(client.currentScreen instanceof PendingVoteScreen)) {
			        client.setScreen(new PendingVoteScreen());
			    }
			}
			// U → unlocks screen (mirrors Craftmine)
			if (unlocksKey.wasPressed()) {
			    if (client.currentScreen == null) {
			        client.setScreen(new UnlocksScreen(new net.zhengzhengyiyi.gui.ClientUnlockManager()));
			    }
			}
			// Level-up toast: fire when XP level increases
			if (client.player != null) {
			    int currentLevel = client.player.experienceLevel;
			    if (lastKnownLevel >= 0 && currentLevel > lastKnownLevel) {
			        client.getToastManager().add(new LevelUpToast(currentLevel));
			    }
			    lastKnownLevel = currentLevel;
			}
		});

		// ── Currency HUD ────────────────────────────────────────────────────────
		// Mirrors Craftmine's HUD that shows current currency (mine points) in-game.
		// Draws in the top-right corner: a small coin icon + number.
		HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.player == null || client.options.hudHidden) return;
			// Only show inside a mine world or when we have currency
			int currency = ClientUnlockState.getCurrency();
			if (currency <= 0) return;

			drawCurrencyHud(context, client, currency);
		});

		// ── Block color providers ───────────────────────────────────────────────
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (world != null && pos != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof NeitherPortalEntity portal) {
                    return portal.getDimensionId() & 0xFFFFFF;
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.NEITHER_PORTAL);
	}

	/**
	 * Draw currency count in the top-right corner.
	 * Mirrors Craftmine's HUD display of mine points.
	 */
	private static void drawCurrencyHud(DrawContext context, MinecraftClient client, int currency) {
		int screenWidth = client.getWindow().getScaledWidth();
		// Format: "⬡ 42" (using emerald item or text symbol)
		String text = "◈ " + currency;
		int textWidth = client.textRenderer.getWidth(text);
		int x = screenWidth - textWidth - 4;
		int y = 4;
		// Shadow background for readability
		context.fill(x - 2, y - 2, x + textWidth + 2, y + 10, 0x88000000);
		context.drawTextWithShadow(client.textRenderer, Text.literal(text), x, y, 0xFFD700); // gold color
	}
}
