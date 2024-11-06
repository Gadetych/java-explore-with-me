package ru.practicum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.StatusParticipationRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;
    private StatusParticipationRequest status;
}
