package ru.skillbox.diplom.model.response.postResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skillbox.diplom.model.CommonResponse;

@Getter
@Setter
@NoArgsConstructor
public class ReportResponse extends CommonResponse<ReportResponse.MessageDto> {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MessageDto{
        private String message = "ok";
    }
}
