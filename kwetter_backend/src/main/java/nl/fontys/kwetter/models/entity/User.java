package nl.fontys.kwetter.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import nl.fontys.kwetter.models.hateoas.Link;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

import static javax.persistence.CascadeType.PERSIST;

@Data
@Entity
@ToString(exclude = {"createdKwetters", "reportedKwetters", "heartedKwetters", "usersFollowed", "followedByUsers", "credentials"})
public class User implements Serializable {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", updatable = false, nullable = false, unique = true, columnDefinition = "varchar(64)")
    private UUID id = UUID.randomUUID();

    @JsonIgnoreProperties("user")
    @OneToOne(fetch = FetchType.LAZY)
    private Credentials credentials;

    @Size(max = 50)
    private String name;

    @Size(max = 140)
    private String bio;

    @Size(max = 50)
    private String website;

    @Size(max = 50)
    private String location;

    @Size(max = 50)
    private String language;

    private byte[] photo;

    @Transient
    private List<Link> links;

    @JsonIgnoreProperties({"owner", "mentions"})
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {PERSIST})
    private Set<Kwetter> createdKwetters;

    @JsonIgnoreProperties({"owner", "mentions"})
    @ManyToMany(cascade = PERSIST)
    private Set<Kwetter> reportedKwetters;

    @JsonIgnoreProperties({"owner", "mentions"})
    @ManyToMany(cascade = PERSIST)
    private Set<Kwetter> heartedKwetters;

    @JsonIgnoreProperties({"usersFollowed", "followedByUsers"})
    @ManyToMany(cascade = PERSIST)
    @JoinTable(name = "follows",
            joinColumns = {@JoinColumn(name = "follower_id")},
            inverseJoinColumns = {@JoinColumn(name = "followed_id")})
    private Set<User> usersFollowed;

    @JsonIgnoreProperties({"usersFollowed", "followedByUsers"})
    @ManyToMany(cascade = PERSIST)
    private Set<User> followedByUsers;

    public User() {
        this.createdKwetters = new HashSet<>();
        this.reportedKwetters = new HashSet<>();
        this.heartedKwetters = new HashSet<>();
        this.usersFollowed = new HashSet<>();
        this.followedByUsers = new HashSet<>();
        this.links = new ArrayList<>();
    }

    public User(User toBeClonedUser) {
        this.id = toBeClonedUser.getId();
        this.credentials = toBeClonedUser.getCredentials();
        this.name = toBeClonedUser.getName();
        this.bio = toBeClonedUser.getBio();
        this.website = toBeClonedUser.getWebsite();
        this.location = toBeClonedUser.getLocation();
        this.language = toBeClonedUser.getLanguage();
        this.photo = toBeClonedUser.getPhoto();
        this.createdKwetters = toBeClonedUser.getCreatedKwetters();
        this.reportedKwetters = toBeClonedUser.getReportedKwetters();
        this.heartedKwetters = toBeClonedUser.getHeartedKwetters();
        this.usersFollowed = toBeClonedUser.getUsersFollowed();
        this.followedByUsers = toBeClonedUser.getFollowedByUsers();
        this.links = toBeClonedUser.getLinks();
    }

    public void addCreatedKwetter(Kwetter createdKwetter) {
        createdKwetters.add(createdKwetter);
    }

    public boolean removeCreatedKwetter(Kwetter createdKwetter) {
        if (createdKwetters.remove(createdKwetter)) {
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
        if (reportedKwetters.remove(reportedKwetter)) {
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
        if (heartedKwetters.remove(heartedKwetter)) {
            heartedKwetter.removeHeart();
            return true;
        }
        return false;
    }

    public boolean follow(User follower) {
        if (!this.equals(follower)) {
            usersFollowed.add(follower);
            follower.followedBy(this);
            return true;
        }
        return false;
    }

    public boolean removeFollow(User follower) {
        if (usersFollowed.remove(follower)) {
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

    public void addLink(String url, String rel){
        links.add(new Link(url, rel));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return id == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
