/*
 * Copyright 2018-2020 Cosgy Dev
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package dev.cosgy.jmusicbot.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Objects;

/**
  * @author Cosgy Dev
  */
@CommandInfo(
         name = "About",
         description = "Display information about the bot"
)
@Author("Cosgy Dev")
public class AboutCommand extends SlashCommand {
    private final Color color;
    private final String description;
    private final Permission[] perms;
    private final String[] features;
    private boolean IS_AUTHOR = true;
    private String REPLACEMENT_ICON = "+";
    private String oauthLink;

    public AboutCommand(Color color, String description, String[] features, Permission... perms) {
        this.color = color;
        this.description = description;
        this.features = features;
        this.name = "about";
        this.help = "Display information about the bot";
        this.aliases = new String[]{"botinfo", "info"};
        this.guildOnly = false;
        this.perms = perms;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    public void setIsAuthor(boolean value) {
        this.IS_AUTHOR = value;
    }

    public void setReplacementCharacter(String value) {
        this.REPLACEMENT_ICON = value;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (oauthLink == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("Could not generate invitation link ", e);
                oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.getGuild() == null ? color : event.getGuild().getSelfMember().getColor());
        builder.setAuthor("" + event.getJDA().getSelfUser().getName() + " - About", null, event.getJDA().getSelfUser().getAvatarUrl());
        String CosgyOwner = "Forked by dubsound into English";
        String author = event.getJDA().getUserById(event.getClient().getOwnerId()) == null ? "<@" + event.getClient().getOwnerId() + ">"
                : Objects.requireNonNull(event.getJDA().getUserById(event.getClient().getOwnerId())).getName();
        StringBuilder descr = new StringBuilder().append("Hello! I'm **").append(event.getJDA().getSelfUser().getName()).append("**. You can find my repo here: ")
                .append(description).append(" using ").append("[" + JDAUtilitiesInfo.AUTHOR + "](https://github.com/JDA-Applications) [Commands Extension](" + JDAUtilitiesInfo.GITHUB + ") (")
                .append(JDAUtilitiesInfo.VERSION).append(") and [JDA library](https://github.com/DV8FromTheWorld/JDA) (")
                .append(JDAInfo.VERSION).append(") and is owned by @").append((IS_AUTHOR ? CosgyOwner : author + ". Mah nemma "))
                .append(event.getJDA().getSelfUser().getName()).append("... \nIf you have any questions, please contact dubsound on Discord")
                .append("\nUse the following command to learn how to use the bot ").append("/help")
                .append(" You will receive a DM from the bot.").append("\n\nFeatures: ```css");
        for (String feature : features)
            descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append( feature);
        descr.append("```");
        builder.setDescription(descr);
        if (event.getJDA().getShardInfo().getShardTotal() == 1) {
            builder.addField("Status", event.getJDA().getGuilds().size() + " Server\n1 Shard", true);
            builder.addField("Users", event.getJDA().getUsers().size() + " Unique\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers ().size()).sum() + " Total", true);
            builder.addField("Channels", event.getJDA().getTextChannels().size() + " Text\n" + event.getJDA().getVoiceChannels().size() + " Voice", true);
        } else {
            builder.addField("Status", (event.getClient()).getTotalGuilds() + "Server\nShard" + (event.getJDA().getShardInfo().getShardId() + 1)
                    + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
            builder.addField("", event.getJDA().getUsers().size() + "Users' shard\n" + event.getJDA().getGuilds().size() + "Server", true);
            builder.addField("", event.getJDA().getTextChannels().size() + "Text Channels\n" + event.getJDA().getVoiceChannels().size() + "Voice Channels", true);
        }
        builder.setFooter("Time when restart occurred", "https://th.bing.com/th/id/OIG.vK6X97DRTB17f9glduev?pid=ImgGn");
        builder.setTimestamp(event.getClient().getStartTime());
        event.replyEmbeds(builder.build()).queue();
    }
	 
    @Override
    protected void execute(CommandEvent event) {
        if (oauthLink == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("Could not generate invitation link ", e);
                oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : color);
        builder.setAuthor("" + event.getSelfUser().getName() + "About!", null, event.getSelfUser().getAvatarUrl());
        String CosgyOwner = "Forked by dubsound into English";
        String author = event.getJDA().getUserById(event.getClient().getOwnerId()) == null ? "<@" + event.getClient().getOwnerId() + ">"
                : Objects.requireNonNull(event.getJDA().getUserById(event.getClient().getOwnerId())).getName();
        StringBuilder descr = new StringBuilder().append("Hello! This is **").append(event.getSelfUser().getName()).append("** . ")
                .append(description).append(" is owned by dubsound using ").append(JDAUtilitiesInfo.AUTHOR + " [command extension](" + JDAUtilitiesInfo.GITHUB + ") (")
                .append(JDAUtilitiesInfo.VERSION).append(") and [JDA library](https://github.com/DV8FromTheWorld/JDA) (")
                .append(JDAInfo.VERSION).append(") and is owned by ").append((IS_AUTHOR ? CosgyOwner : author + "."))
                .append(event.getSelfUser().getName()).append("If you have any questions, please contact dubsound")
                .append("\nUse the following command to learn how to use the bot ").append(event.getClient().getTextualPrefix()).append(event.getClient().getHelpWord())
                .append(" You will receive a DM from the bot.").append("\n\nFeatures: ```css");
        for (String feature : features)
            descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append( feature);
        descr.append("````");
        builder.setDescription(descr);

        if (event.getJDA().getShardInfo().getShardTotal() == 1) {
            builder.addField("Status", event.getJDA().getGuilds().size() + "Server\n1 Shard", true);
            builder.addField("Users", event.getJDA().getUsers().size() + "Unique\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers ().size()).sum() + "sum", true);
            builder.addField("Channels", event.getJDA().getTextChannels().size() + "Text\n" + event.getJDA().getVoiceChannels().size() + "Voice", true);
        } else {
            builder.addField("Status", (event.getClient()).getTotalGuilds() + "Server\nShard" + (event.getJDA().getShardInfo().getShardId() + 1)
                    + "/" + event.getJDA().getShardInfo().getShardTotal(), true);
            builder.addField("", event.getJDA().getUsers().size() + "Users' shard\n" + event.getJDA().getGuilds().size() + "Server", true);
            builder.addField("", event.getJDA().getTextChannels().size() + "Text Channels\n" + event.getJDA().getVoiceChannels().size() + "Voice Channels", true);
        }
        builder.setFooter("Time when restart occurred", "https://th.bing.com/th/id/OIG.vK6X97DRTB17f9glduev?pid=ImgGn");
        builder.setTimestamp(event.getClient().getStartTime());
        event.reply(builder.build());
    }

}
