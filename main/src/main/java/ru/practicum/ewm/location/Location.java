package ru.practicum.ewm.location;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "lat", column = @Column(name = "location_lat")),
        @AttributeOverride(name = "lon", column = @Column(name = "location_lon"))})
public class Location {

    private Float lat;

    private Float lon;
}
