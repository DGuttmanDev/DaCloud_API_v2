package es.pfc.business.mapper;

import es.pfc.business.dto.RegisterDTO;
import es.pfc.business.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserMapper {

    public User RegisterDTOToUser(RegisterDTO registerDTO){

        User user = new User();
        user.setNombre(registerDTO.getNombre());
        user.setApellidos(registerDTO.getApellidos());
        user.setNick(registerDTO.getNick());
        user.setMail(registerDTO.getMail());
        user.setPassword(registerDTO.getPassword());
        user.setFechaCreacion(new Date());

        return user;

    }

}
