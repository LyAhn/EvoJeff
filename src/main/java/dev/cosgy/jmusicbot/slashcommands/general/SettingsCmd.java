/*
 * Copyright 2018 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.cosgy.jmusicbot.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import dev.cosgy.jmusicbot.settings.RepeatMode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.Objects;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SettingsCmd extends SlashCommand {
    private final static String EMOJI = "\uD83C\uDFA7"; // 🎧

    public SettingsCmd(Bot bot) {
        this.name = "settings";
        this.help = "Show bot settings";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        MessageCreateBuilder builder = new MessageCreateBuilder()
                .addContent(EMOJI + " **")
                .addContent(FormatUtil.filter(event.getJDA().getSelfUser().getName()))
                .addContent("** Settings:");
        TextChannel tChan = s.getTextChannel(event.getGuild());
        VoiceChannel vChan = s.getVoiceChannel(event.getGuild());
        Role role = s.getRole(event.getGuild());
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setDescription("Channel for command execution: " + (tChan == null ? "None" : "**#" + tChan.getName() + "**")
                        + "\nDedicated voice channel: " + (vChan == null ? "None" : "**" + vChan.getAsMention() + "**")
                        + "\nDJ permissions: " + (role == null ? "Not set" : "**" + role.getName() + "**")
                        + "\nRepeat: **" + (s.getRepeatMode() == RepeatMode.ALL ? "Enabled (Repeat all songs)" : (s.getRepeatMode() == RepeatMode.SINGLE ? "Enabled (Repeat one song)" : "disabled")) + "**"
                        + "\nVolume:**" + (s.getVolume()) + "**"
                        + "\nDefault Playlist: " + (s.getDefaultPlaylist() == null ? "None" : "**" + s.getDefaultPlaylist() + "**")
                )
                .setFooter(String.format(
                                "Join %s servers | Connect to %s voice channels",
                                event.getJDA().getGuilds().size(),
                                event.getJDA().getGuilds().stream().filter(g -> Objects.requireNonNull(g.getSelfMember().getVoiceState()).inAudioChannel()).count()),
                        null);
        event.reply(builder.addEmbeds(ebuilder.build()).build()).queue();
    }

        @Override
     protected void execute(CommandEvent event) {
         Settings s = event.getClient().getSettingsFor(event.getGuild());
         MessageCreateBuilder builder = new MessageCreateBuilder()
                 .addContent(EMOJI + " **")
                 .addContent(FormatUtil.filter(event.getSelfUser().getName()))
                 .addContent("** Settings:");
         TextChannel tChan = s.getTextChannel(event.getGuild());
         VoiceChannel vChan = s.getVoiceChannel(event.getGuild());
         Role role = s.getRole(event.getGuild());
         EmbedBuilder ebuilder = new EmbedBuilder()
                 .setColor(event.getSelfMember().getColor())
                 .setDescription("Channel for command execution: " + (tChan == null ? "None" : "**#" + tChan.getName() + "**")
                         + "\nDedicated voice channel: " + (vChan == null ? "None" : "**" + vChan.getName() + "**")
                         + "\nDJ permissions: " + (role == null ? "Not set" : "**" + role.getName() + "**")
                         + "\nRepeat: **" + (s.getRepeatMode() == RepeatMode.ALL ? "Enabled (Repeat all songs)" : (s.getRepeatMode() == RepeatMode.SINGLE ? "Enabled (Repeat one song)" : "disabled")) + "**"
                         + "\nDefault Playlist: " + (s.getDefaultPlaylist() == null ? "None" : "**" + s.getDefaultPlaylist() + "**")
                 )
                 .setFooter(String.format(
                                 "Join %s servers | Connect to %s voice channels",
                                 event.getJDA().getGuilds().size(),
                                 event.getJDA().getGuilds().stream().filter(g -> Objects.requireNonNull(g.getSelfMember().getVoiceState()).inAudioChannel()).count()),
                         null);
         event.getChannel().sendMessage(builder.addEmbeds(ebuilder.build()).build()).queue();
     }

}