package rut.miit.sopcontracts.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "root", description = "API entry point")
@RequestMapping("/api")
public interface RootController {

    @Operation(summary = "API root", description = "Entry point with links to main resources")
    @ApiResponse(responseCode = "200", description = "Root retrieved successfully")
    @GetMapping
    RepresentationModel<?> getRoot();
}