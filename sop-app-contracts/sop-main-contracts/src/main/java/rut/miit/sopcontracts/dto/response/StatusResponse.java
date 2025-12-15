package rut.miit.sopcontracts.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StatusResponse(String status, String error) {}
