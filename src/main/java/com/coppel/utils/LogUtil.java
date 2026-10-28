package com.coppel.utils;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import com.coppel.constants.Estado;

import static com.coppel.constants.AppMessages.INICIA_TRANSACCION;
import static com.coppel.constants.AppMessages.FALLO_TRANSACCION;
import static com.coppel.constants.AppMessages.FINALIZA_TRANSACCION;

import static com.coppel.constants.Estado.FALLO;

/**
 * Clase que graba logs en general
 */
@Component
@RequiredArgsConstructor
public class LogUtil {

	private static final Logger LOGGER = LogManager.getLogger(LogUtil.class.getName());
	
	/**
	 * Metodo que graba logs de error en general
	 */
	public void grabar( Estado eEstado, String sMetodo, String sMensageError, HttpServletResponse...response ) {
		grabar(eEstado, sMetodo, sMensageError, HttpStatus.BAD_REQUEST, response);
	}//end grabar()
	
	/**
	 * Metodo que graba logs de error en general
	 */
	public void grabar( Estado eEstado, String sMetodo, String sMensageError, HttpStatusCode statusCode, HttpServletResponse...response ) {
        String sMensajeLog;
        String sEstado;
        String sIdentificador = "IMP";
		String sComponente = "ds-im-customComplements";

        sEstado = switch (eEstado) {
            case INICIA_TRANSACCION -> INICIA_TRANSACCION;
            case FINALIZA_TRANSACCION -> FINALIZA_TRANSACCION;
            case FALLO -> FALLO_TRANSACCION;
            default -> "";
        };

        if( eEstado.equals(FALLO) ) {
            sMensajeLog = String.format("%s: Componente:%s - Funcion:%s - Estado:%s - Mensaje:%s"
                    , sIdentificador, sComponente, sMetodo, sEstado, sMensageError);

            LOGGER.error(sMensajeLog);

            if( response.length > 0 ) {
                response[0].setStatus(statusCode.value());
            }
        } else {
            sMensajeLog = String.format("%s: Componente:%s - Funcion:%s - Estado:%s"
                    , sIdentificador, sComponente, sMetodo, sEstado);

            LOGGER.info(sMensajeLog);
        }
    }
}
