package rut.miit.sopcontracts.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response with pagination")
public record PagedResponse<T>(

        @Schema(description = "The content of the current page")
        List<T> content,

        @Schema(description = "The current page number (starting from 0)")
        int pageNumber,

        @Schema(description = "The size of the page")
        int pageSize,

        @Schema(description = "The total number of elements")
        int totalElements,

        @Schema(description = "The total number of pages")
        int totalPages,

        @Schema(description = "Indicates whether this is the last page")
        boolean last
) {}
