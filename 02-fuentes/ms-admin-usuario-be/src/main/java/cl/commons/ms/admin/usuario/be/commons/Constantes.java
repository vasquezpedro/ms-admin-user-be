package cl.commons.ms.admin.usuario.be.commons;

public final class Constantes {
    
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PASS_REGEX = "^(?=.*[A-Z])(?=(?:.*\\d){2})[a-zA-Z\\d]{8,12}$";
    public static final String MENSAJE_ERROR_EMAIL = "formato de email inválido";
    public static final String MENSAJE_ERROR_CONTRYCODE = "El campo 'contrycode' solo permite números";
    public static final String MENSAJE_ERROR_CONTRYCODE_LENGTH = "El campo 'contrycode' debe tener una longitud maxima de 3 caracteres";
    public static final String MENSAJE_ERROR_CITYCODE_LENGTH = "El campo 'citycode' debe tener una longitud maxima de 3 caracteres";
    public static final String MENSAJE_ERROR_NUMBER = "El campo 'number' solo permite números";
    public static final String MENSAJE_ERROR_NUMBER_LENGTH = "El campo 'number' debe tener una longitud 9 caracteres";
    public static final String MENSAJE_ERROR_PASSWORD = "formato de password inválido";
    public static final String MENSAJE_ERROR_EMAIL_EXISTE = "El correo ya registrado";
    public static final String MENSAJE_ERROR_ID_USER_INEXISTE = "id ingresado inexistente";
    public static final String MENSAJE_ERROR_TOKEN_INVALIDO = "token inválido";
    public static final String MENSAJE_ERROR_UNAUTHORIZED = "Usuario no autorizado para este recurso";
    public static final String MENSAJE_ERROR_PHONE_ID_USER = "Id usuario no registra telefonos asociados";
    public static final String MENSAJE_ERROR_PHONE_EXISTE = "El teléfono ingresado ya existe.";
    public static final String NUMBER_REGEX = "^[0-9]+$";
    private Constantes(){}

   
}
