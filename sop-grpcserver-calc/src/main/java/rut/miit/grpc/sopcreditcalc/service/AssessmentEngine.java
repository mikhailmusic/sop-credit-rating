package rut.miit.grpc.sopcreditcalc.service;

import rut.miit.grpc.AssessmentRequest;
import rut.miit.grpc.AssessmentResponse;


public interface AssessmentEngine {
    AssessmentResponse assess(AssessmentRequest request);
}
