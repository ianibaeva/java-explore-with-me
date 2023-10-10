package ru.practicum.ewm.event.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.WhereJoinTable;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation")
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "confirmed_requests", nullable = false)
    private Integer confirmedRequests;

    @Column(name = "created_on")
    @CreationTimestamp
    private LocalDateTime createdOn;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Embedded
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Long participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "title")
    private String title;

    @WhereJoinTable(clause = "status='CONFIRMED'")
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "requests",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "requester_id"))
    private Set<User> participants = new HashSet<>();

    private Integer views;
}
