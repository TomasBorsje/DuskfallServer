package nz.tomasborsje.duskfall.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ServerListPingListener implements EventListener<ServerListPingEvent> {

    private static final Component motdTopLine =
            Component.text(StringUtils.center("Duskfall", 43), NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
            .append(Component.text("v0.0.1", Style.style().color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false).build()))
            .appendNewline()
            .append(Component.text(StringUtils.center("-- Dev Test --", 49), NamedTextColor.BLUE));

    private final String serverIconBase64;

    public ServerListPingListener() {
        try {
            byte[] pngByteArray = Files.readAllBytes(Path.of("server-icon.png"));
            serverIconBase64 = Base64.getEncoder().encodeToString(pngByteArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public Result run(@NotNull ServerListPingEvent event) {
        ResponseData responseData = new ResponseData();
        responseData.setMaxPlayer(1000);
        responseData.setOnline(999);
        responseData.setDescription(motdTopLine);
        responseData.setFavicon(serverIconBase64);

        event.setResponseData(responseData);
        return Result.SUCCESS;
    }

    @Override
    public @NotNull Class<ServerListPingEvent> eventType() {
        return ServerListPingEvent.class;
    }
}
