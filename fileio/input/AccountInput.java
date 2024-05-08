package fileio.input;

import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public abstract class AccountInput {
    private String username;
    private int age;
    private String city;

    public AccountInput() {
    }

    public AccountInput(final Command command) {
        this.setUsername(command.getUsername());
        this.setAge(command.getAge());
        this.setCity(command.getCity());
    }

    /**
     * Checks if this AccountInput object is equal to another object.
     *
     * This method checks if the other object is a reference to this object, if it is null, or if
     * it is not an instance of AccountInput. If any of these conditions are true, it returns
     * false. Otherwise, it checks if the age, username, and city of the other AccountInput object
     * are equal to those of this object. If they are, it returns true. Otherwise, it returns
     * false.
     *
     * @param o The object to be compared with this AccountInput object.
     * @return true if the other object is an AccountInput object and has the same age, username,
     * and city as this object, false otherwise.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountInput that = (AccountInput) o;
        return age == that.age && Objects.equals(username, that.username)
                && Objects.equals(city, that.city);
    }

    /**
     * Generates a hash code for this AccountInput object.
     *
     * This method uses the username, age, and city of this object to generate a hash code.
     *
     * @return The hash code of this AccountInput object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, age, city);
    }
}
