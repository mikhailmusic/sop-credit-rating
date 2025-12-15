package rut.miit.sopcontracts.exception;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException(String email) {
        super("Client with Email '" + email + "' already exists");
    }
}
