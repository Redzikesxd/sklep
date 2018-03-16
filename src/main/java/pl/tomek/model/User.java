package pl.tomek.model;

import lombok.Data;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{pl.tomek.model.User.empty}")
    private String firstname;
    @NotEmpty(message = "{pl.tomek.model.User.empty}")
    private String lastname;

   @NotEmpty(message = "{pl.tomek.model.User.empty}")
    private String email;
    @Length(min = 5,max = 12,message = "{pl.tomek.model.User.min2}")
    private String login;
    @Length(min = 9,max = 15,message = "{pl.tomek.model.User.min}")
    private String passworld;

    @ManyToMany(cascade = CascadeType.PERSIST,fetch = FetchType.EAGER)
    private Set<UserRole> userRoleSet;

    private boolean regulamin1;
    private boolean regulamin2;

}
