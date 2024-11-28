package ru.practicum;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.StateOfPublication;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class DataTest {
    @Getter
    private static Event eventById1 = Event.builder()
            .id(1L)
            .annotation("Excepturi quia voluptatem aut veniam atque molestiae ut. Laboriosam id dignissimos nisi nam. Labore voluptas " +
                    "a sint sint nulla sed accusantium nulla. Eum sint et repellat. Maxime et deserunt tempora.")
            .category(Category.builder()
                    .id(1L)
                    .name("invoice1008")
                    .build())
            .initiator(User.builder()
                    .id(1L)
                    .name("Laurence Kuvalis")
                    .email("Terrance24@yahoo.com")
                    .build())
            .description("Earum voluptas repellendus in necessitatibus necessitatibus et dolores dolor. Assumenda tempore suscipit quibusdam dolor ut consequatur " +
                    "itaque quos veniam. Unde omnis totam inventore a consequatur. Illum ab ut vitae dolorum maiores libero.\n \rEa quas maiores voluptatum qui pariatur quia soluta voluptatum qui. Iusto aperiam consequatur et enim ullam omnis aut. Reprehenderit nobis non. Sapiente officia voluptas omnis.\n \rOdio ea vitae accusamus necessitatibus. Dolor maiores hic mollitia ut ut " +
                    "omnis ea nulla. Qui nesciunt est et sit. Tempore commodi sit qui dolor nihil. Maiores quaerat cupiditate dicta sunt laborum minima quam.")
            .eventDate(LocalDateTime.now().plusMonths(1))
            .createdOn(LocalDateTime.now())
            .location(Location.builder()
                    .id(1L)
                    .lat(37.2264)
                    .lon(18.6974)
                    .build())
            .paid(true)
            .participantLimit(93)
            .requestModeration(true)
            .state(StateOfPublication.PENDING)
            .title("Deserunt corrupti voluptas laboriosam voluptatem delectus excepturi ullam voluptates.")
            .build();

    @Getter
    private static Event eventById2 = Event.builder()
            .id(2L)
            .annotation("Vitae omnis delectus. In vitae sunt architecto ut velit. Et voluptatem in facilis quasi dolorem dignissimos. " +
                    "Ullam repudiandae numquam perferendis rem mollitia. Rerum alias eligendi nam id aliquid possimus aspernatur doloremque enim.")
            .category(Category.builder()
                    .id(2L)
                    .name("system-worthy4827")
                    .build())
            .initiator(User.builder()
                    .id(2L)
                    .name("Laure Uvalis")
                    .email("Uvalis@yahoo.com")
                    .build())
            .description("Repellat eligendi eius ad cumque voluptatem illo. Sit assumenda praesentium. Vero beatae doloribus veniam voluptas molestiae consequatur. Enim dolores sapiente vel.\n " +
                    "\rCorrupti qui ullam et quia est totam et. Dolorum et dolores vitae sed accusamus vel ea dolore eaque. Veritatis et perspiciatis consequuntur sit est culpa.\n \rVoluptas veniam natus perspiciatis. Et at qui. " +
                    "Eveniet vel voluptatem vitae enim illo id molestias debitis. Fuga mollitia in. Occaecati nostrum laboriosam culpa dolor labore enim.")
            .eventDate(LocalDateTime.now().plusMonths(2))
            .createdOn(LocalDateTime.now())
            .location(Location.builder()
                    .id(2L)
                    .lat(-67.1219)
                    .lon(31.5753)
                    .build())
            .paid(true)
            .participantLimit(800)
            .requestModeration(true)
            .state(StateOfPublication.PENDING)
            .title("Dolore labore odit cum enim.")
            .build();

    @Getter
    private static EventFullDto eventFullDtoById1 = EventFullDto.builder()
            .id(1L)
            .annotation("Excepturi quia voluptatem aut veniam atque molestiae ut. Laboriosam id dignissimos nisi nam. Labore voluptas " +
                    "a sint sint nulla sed accusantium nulla. Eum sint et repellat. Maxime et deserunt tempora.")
            .category(CategoryDto.builder()
                    .id(1L)
                    .name("invoice1008")
                    .build())
            .confirmedRequests(0)
            .initiator(UserShortDto.builder()
                    .id(1L)
                    .name("Laurence Kuvalis")
                    .build())
            .description("Earum voluptas repellendus in necessitatibus necessitatibus et dolores dolor. Assumenda tempore suscipit quibusdam dolor ut consequatur " +
                    "itaque quos veniam. Unde omnis totam inventore a consequatur. Illum ab ut vitae dolorum maiores libero.\n \rEa quas maiores voluptatum qui pariatur quia soluta voluptatum qui. Iusto aperiam consequatur et enim ullam omnis aut. Reprehenderit nobis non. Sapiente officia voluptas omnis.\n \rOdio ea vitae accusamus necessitatibus. Dolor maiores hic mollitia ut ut " +
                    "omnis ea nulla. Qui nesciunt est et sit. Tempore commodi sit qui dolor nihil. Maiores quaerat cupiditate dicta sunt laborum minima quam.")
            .eventDate(LocalDateTime.now().plusMonths(1))
            .createdOn(LocalDateTime.now())
            .publishedOn(LocalDateTime.now().plusHours(1))
            .location(LocationDto.builder()
                    .lat(37.2264)
                    .lon(18.6974)
                    .build())
            .paid(true)
            .participantLimit(93)
            .requestModeration(true)
            .state(StateOfPublication.PUBLISHED)
            .title("Deserunt corrupti voluptas laboriosam voluptatem delectus excepturi ullam voluptates.")
            .views(10)
            .build();

    @Getter
    private static EventShortDto eventShortDtoById1 = EventShortDto.builder()
            .id(1L)
            .annotation("Excepturi quia voluptatem aut veniam atque molestiae ut. Laboriosam id dignissimos nisi nam. Labore voluptas " +
                    "a sint sint nulla sed accusantium nulla. Eum sint et repellat. Maxime et deserunt tempora.")
            .category(CategoryDto.builder()
                    .id(1L)
                    .name("invoice1008")
                    .build())
            .confirmedRequests(0)
            .initiator(UserShortDto.builder()
                    .id(1L)
                    .name("Laurence Kuvalis")
                    .build())
            .eventDate(LocalDateTime.now().plusMonths(1))
            .paid(true)
            .participantLimit(93)
            .title("Deserunt corrupti voluptas laboriosam voluptatem delectus excepturi ullam voluptates.")
            .views(10)
            .build();

}
