package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {

    private int id;

    private String name;

    @NotBlank(message = "Электронная почта не может быть пустой", groups = {Creation.class, Update.class})
    @Email(message = "Электронная почта должна быть в правильном формате", groups = {Creation.class, Update.class})
    private String email;

    public interface Creation {}

    public interface Update {}
}
