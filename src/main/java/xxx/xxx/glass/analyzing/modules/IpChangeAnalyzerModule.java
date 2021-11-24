package xxx.xxx.glass.analyzing.modules;

import xxx.xxx.glass.analyzing.actions.Countermeasure;
import xxx.xxx.glass.command.channel.CommunicationChannel;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.data.entry.Entry;
import xxx.xxx.glass.data.entry.JoinEntry;
import xxx.xxx.glass.database.DatabaseCommunicator;
import xxx.xxx.glass.database.DatabaseResponse;
import xxx.xxx.glass.discord.ButtonRegistry;
import org.bukkit.entity.Player;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Not fully implemented module to detected suspicious ip changes.
 */

public class IpChangeAnalyzerModule extends EntryAnalyzerModule {

    {
        registerHandler(JoinEntry.class, this::handleJoinEntry);
    }

    private final static long TWO_WEEKS = 1000 * 60 * 60 * 24 * 14;
    private final static int MINIMUM_LOGINS_ON_DIFFERENT_DAYS = 4;
    private final static int MAXIMUM_DIFFERENT_IPS = 3;

    private final DatabaseCommunicator databaseCommunicator;
    private final ButtonRegistry buttonRegistry;

    public IpChangeAnalyzerModule(final CommunicationChannel notificationChannel, final Countermeasure countermeasure, final ServiceProvider serviceProvider) {

        super(notificationChannel, countermeasure);

        this.databaseCommunicator = serviceProvider.getService(DatabaseCommunicator.class);
        this.buttonRegistry = serviceProvider.getService(ButtonRegistry.class);

    }

    /**
     * Used to send a notification to the configured notification discord channel.
     *
     * @param target       The player to inform about.
     * @param placeholders The placeholders.
     */

    @Override
    public void sendNotification(final Player target, final Map<String, String> placeholders) {

        buttonRegistry.registerButton("", (buttonInteractEvent -> {

        }));

    }

    /**
     * Used to handle a JoinEntry.
     *
     * @param input The entry.
     */

    private void handleJoinEntry(final Entry input) {

        final JoinEntry entry = (JoinEntry) input;

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Mono.fromCallable(() -> databaseCommunicator.findEntriesByQuery("join",
                String.format("userUuid=%s timeSpan=%s#%s",
                        entry.getUser().getUniqueId().toString(),
                        dateFormat.format(new Date(System.currentTimeMillis() - TWO_WEEKS)),
                        dateFormat.format(new Date())
                )))
                .doOnNext(databaseResponse -> {
                    if (databaseResponse.getStatus() == DatabaseResponse.Status.FAILED || !databaseResponse.hasEntries())
                        return;
                    List<String> ipList = databaseResponse.getEntries()
                            .stream()
                            .map(innerEntry -> ((JoinEntry) innerEntry).getIp())
                            .collect(Collectors.toList());

                    if (ipList.contains(entry.getIp())) return;

                    final Map<String, Integer> ips = new HashMap<>();
                    for (final String ip : ipList) {
                        if (ips.containsKey(ip)) ips.replace(ip, ips.get(ip) + 1);
                        else ips.put(ip, 1);
                    }

                    if (ips.size() > MAXIMUM_DIFFERENT_IPS) return;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

    }

}
