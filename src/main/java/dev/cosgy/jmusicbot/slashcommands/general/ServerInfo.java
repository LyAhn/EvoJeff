package dev.cosgy.jmusicbot.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ServerInfo extends SlashCommand {
    public ServerInfo(Bot bot) {
        this.name = "serverinfo";
        this.help = "Display information about the server";
        this.guildOnly = true;
        this.category = new Category("General");
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String GuildName = event.getGuild().getName();
        String GuildIconURL = event.getGuild().getIconUrl();
        String GuildId = event.getGuild().getId();
        String GuildOwner = Objects.requireNonNull(event.getGuild().getOwner()).getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator();
        String GuildCreatedDate = event.getGuild().getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        String GuildRolesCount = String.valueOf(event.getGuild().getRoles().size());
        String GuildMember = String.valueOf(event.getGuild().getMembers().size());
        String GuildCategoryCount = String.valueOf(event.getGuild().getCategories().size());
        String GuildTextChannelCount = String.valueOf(event.getGuild().getTextChannels().size());
        String GuildVoiceChannelCount = String.valueOf(event.getGuild().getVoiceChannels().size());
        String GuildStageChannelCount = String.valueOf(event.getGuild().getStageChannels().size());
        String GuildForumChannelCount = String.valueOf(event.getGuild().getForumChannels().size());
        String GuildLocation = event.getGuild().getLocale().getNativeName();
                /*
                .replace("japan", ":flag_jp: 日本")
                .replace("singapore", ":flag_sg: シンガポール")
                .replace("hongkong", ":flag_hk: 香港")
                .replace("Brazil", ":flag_br: ブラジル")
                .replace("us-central", ":flag_us: 中央アメリカ")
                .replace("us-west", ":flag_us: 西アメリカ")
                .replace("us-east", ":flag_us: 東アメリカ")
                .replace("us-south", ":flag_us: 南アメリカ")
                .replace("sydney", ":flag_au: シドニー")
                .replace("eu-west", ":flag_eu: 西ヨーロッパ")
                .replace("eu-central", ":flag_eu: 中央ヨーロッパ")
                .replace("russia", ":flag_ru: ロシア");
                 */

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Server " + GuildName + " information", null, GuildIconURL);

        eb.addField("Server ID", GuildId, true);
        eb.addField("Server First Language", GuildLocation, true);
        eb.addField("Server Owner", GuildOwner, true);
        eb.addField("Number of members", GuildMember, true);
        eb.addField("Number of Roles", GuildRolesCount, true);
        eb.addField("Number of categories", GuildCategoryCount, true);
        eb.addField("Number of text channels", GuildTextChannelCount, true);
        eb.addField("Number of voice channels", GuildVoiceChannelCount, true);
        eb.addField("Number of stage channels", GuildStageChannelCount, true);
        eb.addField("Number of forum channels", GuildForumChannelCount, true);

        eb.setFooter("Server creation date and time: " + GuildCreatedDate, null);

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public void execute(CommandEvent event) {
        String GuildName = event.getGuild().getName();
        String GuildIconURL = event.getGuild().getIconUrl();
        String GuildId = event.getGuild().getId();
        String GuildOwner = Objects.requireNonNull(event.getGuild().getOwner()).getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator();
        String GuildCreatedDate = event.getGuild().getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        String GuildRolesCount = String.valueOf(event.getGuild().getRoles().size());
        String GuildMember = String.valueOf(event.getGuild().getMembers().size());
        String GuildCategoryCount = String.valueOf(event.getGuild().getCategories().size());
        String GuildTextChannelCount = String.valueOf(event.getGuild().getTextChannels().size());
        String GuildVoiceChannelCount = String.valueOf(event.getGuild().getVoiceChannels().size());
        String GuildStageChannelCount = String.valueOf(event.getGuild().getStageChannels().size());
        String GuildForumChannelCount = String.valueOf(event.getGuild().getForumChannels().size());
        String GuildLocation = event.getGuild().getLocale().getNativeName();
                /*.replace("japan", ":flag_jp: 日本")
                .replace("singapore", ":flag_sg: シンガポール")
                .replace("hongkong", ":flag_hk: 香港")
                .replace("Brazil", ":flag_br: ブラジル")
                .replace("us-central", ":flag_us: 中央アメリカ")
                .replace("us-west", ":flag_us: 西アメリカ")
                .replace("us-east", ":flag_us: 東アメリカ")
                .replace("us-south", ":flag_us: 南アメリカ")
                .replace("sydney", ":flag_au: シドニー")
                .replace("eu-west", ":flag_eu: 西ヨーロッパ")
                .replace("eu-central", ":flag_eu: 中央ヨーロッパ")
                .replace("russia", ":flag_ru: ロシア");*/


        EmbedBuilder eb = new EmbedBuilder();

eb.setAuthor("Server " + GuildName + " information", null, GuildIconURL);

         eb.addField("Server ID", GuildId, true);
         eb.addField("Server First Language", GuildLocation, true);
         eb.addField("Server Owner", GuildOwner, true);
         eb.addField("Number of members", GuildMember, true);
         eb.addField("Number of Roles", GuildRolesCount, true);
         eb.addField("Number of categories", GuildCategoryCount, true);
         eb.addField("Number of text channels", GuildTextChannelCount, true);
         eb.addField("Number of voice channels", GuildVoiceChannelCount, true);
         eb.addField("Number of stage channels", GuildStageChannelCount, true);
         eb.addField("Number of forum channels", GuildForumChannelCount, true);

         eb.setFooter("Server creation date and time: " + GuildCreatedDate, null);

         event.getChannel().sendMessageEmbeds(eb.build()).queue();
     }
}
