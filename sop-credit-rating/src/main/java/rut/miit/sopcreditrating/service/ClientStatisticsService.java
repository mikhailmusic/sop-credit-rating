package rut.miit.sopcreditrating.service;


import rut.miit.sopcontracts.dto.response.ClientStatisticsResponse;

import java.util.UUID;

public interface ClientStatisticsService {

    ClientStatisticsResponse calculateStatistics(UUID clientId);
}