package rut.miit.sopcreditrating.exception;

import com.netflix.graphql.dgs.exceptions.DgsBadRequestException;
import com.netflix.graphql.types.errors.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import rut.miit.sopcontracts.exception.BusinessLogicException;
import rut.miit.sopcontracts.exception.ResourceNotFoundException;


@ControllerAdvice
public class GraphQLExceptionHandler {

    @GraphQlExceptionHandler(ResourceNotFoundException.class)
    public GraphQLError handleNotFound(ResourceNotFoundException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.NOT_FOUND)
                .build();
    }

    @GraphQlExceptionHandler(DgsBadRequestException.class)
    public GraphQLError handleBadRequest(DgsBadRequestException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler(BusinessLogicException.class)
    public GraphQLError handleBusiness(BusinessLogicException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler(IllegalArgumentException.class)
    public GraphQLError  handleIllegalArg(IllegalArgumentException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }
}
