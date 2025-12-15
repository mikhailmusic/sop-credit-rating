package rut.miit.sopcontracts.exception;

public class CifAlreadyExistsException extends ConflictException {
    public CifAlreadyExistsException(String cif) {
        super("Client with CIF '" + cif + "' already exists");
    }
}
