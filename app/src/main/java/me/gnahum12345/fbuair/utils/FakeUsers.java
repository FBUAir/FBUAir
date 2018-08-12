package me.gnahum12345.fbuair.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;

import me.gnahum12345.fbuair.managers.MyUserManager;

import static me.gnahum12345.fbuair.models.User.NO_COLOR;
import static me.gnahum12345.fbuair.utils.ImageUtils.drawableToBitmap;
import static me.gnahum12345.fbuair.utils.Utils.dateFormatter;

public class FakeUsers {

    public List<User> fakeUsersList = new ArrayList<>();

    Context context;

    public FakeUsers(Context context) {
        this.context = context;
        setUserInfo();
    }

    public List<User> getFakeUsersList() {
        return fakeUsersList;
    }

    private void setUserInfo() {
        User katherine = new User();
        katherine.setId();
        katherine.setName("Katherine Lazar");
        katherine.setOrganization("Facebook");
        katherine.setPhoneNumber("2328394839");
        katherine.setEmail("katherinelazar@gmail.com");
        katherine.setNumConnections(38);
        setProfileImage(katherine);
        katherine.setTimeAddedToHistory(dateFormatter.format(Calendar.getInstance().getTime()));

        SocialMedia facebookKatherine = new SocialMedia();
        facebookKatherine.setName("Facebook");
        facebookKatherine.setUsername(katherine.getName());
        facebookKatherine.setProfileUrl("https://www.facebook.com/katherine.lazar1");
        katherine.addSocialMedia(facebookKatherine);

        SocialMedia githubKatherine = new SocialMedia();
        githubKatherine.setName("Github");
        githubKatherine.setUsername("katherinelazar");
        katherine.addSocialMedia(githubKatherine);

        SocialMedia linkedInKatherine = new SocialMedia();
        linkedInKatherine.setName("LinkedIn");
        linkedInKatherine.setUsername(katherine.getName());
        linkedInKatherine.setProfileUrl("https://www.linkedin.com/in/katherineelenalazar/");
        katherine.addSocialMedia(linkedInKatherine);
        fakeUsersList.add(katherine);

        User raul = new User();
        raul.setId();
        raul.setName("Raul Dagir");
        raul.setPhoneNumber("2328394839");
        raul.setEmail("raul234@gmail.com");
        raul.setNumConnections(76);
        setProfileImage(raul);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 11);
        raul.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));

        SocialMedia facebookRaul = new SocialMedia();
        facebookRaul.setName("Facebook");
        facebookRaul.setUsername(raul.getName());
        facebookRaul.setProfileUrl("https://www.facebook.com/raul.gd.35");
        raul.addSocialMedia(facebookRaul);

        SocialMedia instagramRaul = new SocialMedia();
        instagramRaul.setName("Instagram");
        instagramRaul.setUsername("raullgd");
        raul.addSocialMedia(instagramRaul);

        SocialMedia snapchatRaul = new SocialMedia();
        snapchatRaul.setName("Snapchat");
        snapchatRaul.setUsername("raullgd");
        raul.addSocialMedia(snapchatRaul);

        SocialMedia soundcloudRaul = new SocialMedia();
        soundcloudRaul.setName("Soundcloud");
        soundcloudRaul.setUsername("Raul GD");
        soundcloudRaul.setProfileUrl("https://soundcloud.com/raul-gd");
        raul.addSocialMedia(soundcloudRaul);
        fakeUsersList.add(raul);

        User sanura = new User();
        sanura.setId();
        sanura.setName("Sanura N’Jaka");
        sanura.setPhoneNumber("4958273384");
        sanura.setOrganization("Stanford");
        sanura.setEmail("sanura_njaka@stanford.edu");
        sanura.setNumConnections(102);
        setProfileImage(sanura);

        calendar.set(Calendar.DATE, 9);
        sanura.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));
        fakeUsersList.add(sanura);

        User vanessa = new User();
        vanessa.setId();
        vanessa.setName("Vanessa Yan");
        vanessa.setNumConnections(0);
        setProfileImage(vanessa);

        addDefaultSM(vanessa, "LinkedIn");
        addDefaultSM(vanessa, "Github", "vanessa78");

        calendar.set(Calendar.DATE, 8);
        vanessa.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));
        fakeUsersList.add(vanessa);

        User gabriella = new User();
        gabriella.setId();
        gabriella.setName("Gabriella Garcia");
        gabriella.setPhoneNumber("3948593304");
        gabriella.setNumConnections(393);
        setProfileImage(gabriella);

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        calendar.set(Calendar.DATE, 27);
        gabriella.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));
        fakeUsersList.add(gabriella);

        User martha = new User();
        martha.setId();
        martha.setName("Martha Gao");
        martha.setNumConnections(49);
        setProfileImage(martha);

        addDefaultSM(martha, "Facebook");

        martha.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));
        fakeUsersList.add(martha);

        User emma = new User();
        emma.setId();
        emma.setName("Emma Rivera");
        emma.setNumConnections(4);
        setProfileImage(emma);

        addDefaultSM(emma, "Instagram", "emma_r");
        addDefaultSM(emma, "Facebook");
        addDefaultSM(emma, "LinkedIn");

        calendar.set(Calendar.DATE, 21);
        emma.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));
        fakeUsersList.add(emma);

        User jacob = new User();
        jacob.setId();
        jacob.setName("Jacob Snyder");
        jacob.setNumConnections(20);
        setProfileImage(jacob);

        addDefaultSM(jacob, "Snapchat", "jacobsnyder");

        calendar.set(Calendar.DATE, 12);
        jacob.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));
        fakeUsersList.add(jacob);

        User david = new User();
        david.setId();
        david.setName("David Mindlin");
        david.setPhoneNumber("3948573944");
        david.setNumConnections(33);
        setProfileImage(david);

        addDefaultSM(david, "WhatsApp", "(584)345-1837");

        calendar.set(Calendar.DATE, 10);
        david.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));
        fakeUsersList.add(david);
    }

/*    // sets profile image from resource drawable id
    void setProfileImage(User user, int drawableId) {
        user.setColor(NO_COLOR);
        user.setProfileImage(BitmapFactory.decodeResource(context.getResources(), drawableId));
    }*/

    // sets default profile image
    void setProfileImage(User user) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(Character.toString(user.getName().toCharArray()[0]).toUpperCase(),
                        color);
        user.setProfileImage(drawableToBitmap(drawable));
        user.setColor(color);
    }

    void addDefaultSM(User user, String socialMediaName, String username) {
        SocialMedia socialMedia = new SocialMedia();
        socialMedia.setName(socialMediaName);
        socialMedia.setUsername(username);
        socialMedia.setProfileUrl("https://www." + socialMediaName.toLowerCase() + ".com");
        user.addSocialMedia(socialMedia);
    }

    void addDefaultSM(User user, String socialMediaName) {
        SocialMedia socialMedia = new SocialMedia();
        socialMedia.setName(socialMediaName);
        socialMedia.setUsername(user.getName());
        socialMedia.setProfileUrl("https://www." + socialMediaName.toLowerCase() + ".com");
        user.addSocialMedia(socialMedia);
    }
}
