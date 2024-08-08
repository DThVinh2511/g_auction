package com.ghtk.auction.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPages;
    boolean last;
    List<T> content;
}
