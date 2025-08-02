package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    private int id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private boolean available;

    private User owner;

    private ItemRequest request;
}
