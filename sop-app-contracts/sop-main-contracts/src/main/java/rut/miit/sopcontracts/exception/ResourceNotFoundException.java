package rut.miit.sopcontracts.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s with id=%s not found", resourceName, resourceId));
    }
}

