package nl.fontys.kwetter.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {"createdKwetters", "reportedKwetters", "heartedKwetters", "usersFollowed", "followedByUsers", "credentials"})
@ToString(exclude = {"createdKwetters", "reportedKwetters", "heartedKwetters", "usersFollowed", "followedByUsers", "credentials"})
public class User {

    private @Id @GeneratedValue Long id;
    private Role role;
    private Credentials credentials;

    private String name;
    private String bio;
    private String website;
    private String location;
    private byte[] photo;

    private Set<Kwetter> createdKwetters;
    private Set<Kwetter> reportedKwetters;
    private Set<Kwetter> heartedKwetters;

    private Set<User> usersFollowed;
    private Set<User> followedByUsers;

    public User(Role role){
        this.role = role;

        createdKwetters = new HashSet<>();
        reportedKwetters = new HashSet<>();
        heartedKwetters = new HashSet<>();
        usersFollowed = new HashSet<>();
        followedByUsers = new HashSet<>();
    }

    public void addCreatedKwetter(Kwetter createdKwetter) {
        createdKwetters.add(createdKwetter);
    }

    public boolean removeCreatedKwetter(Kwetter createdKwetter) {
        if (createdKwetters.remove(createdKwetter)){
            createdKwetter.removeOwner();
            return true;
        }
        return false;
    }

    public void addReportedKwetter(Kwetter reportedKwetter) {
        reportedKwetters.add(reportedKwetter);
        reportedKwetter.report();
    }

    public boolean removeReportedKwetter(Kwetter reportedKwetter) {
        if (reportedKwetters.remove(reportedKwetter)){
            reportedKwetter.removeReport();
            return true;
        }
        return false;
    }

    public void addHeartedKwetter(Kwetter heartedKwetter) {
        heartedKwetters.add(heartedKwetter);
        heartedKwetter.heart();
    }

    public boolean removeHeartedKwetter(Kwetter heartedKwetter) {
        if (heartedKwetters.remove(heartedKwetter)){
            heartedKwetter.removeHeart();
            return true;
        }
        return false;
    }

    public boolean follow(User follower) {
        if (!this.equals(follower)){
            usersFollowed.add(follower);
            follower.followedBy(this);
            return true;
        }
        return false;
    }

    public boolean removeFollow(User follower){
        if (usersFollowed.remove(follower)){
            follower.removeFollowedBy(this);
            return true;
        }
        return false;
    }

    public void followedBy(User following) {
        followedByUsers.add(following);
    }

    public void removeFollowedBy(User following) {
        followedByUsers.remove(following);
    }
}
