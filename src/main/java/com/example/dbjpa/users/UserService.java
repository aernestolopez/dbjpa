package com.example.dbjpa.users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    private UserDTO mapperToDto(User user){
        UserDTO newUserDto = new UserDTO();
        newUserDto.setId(user.getId());
        newUserDto.setName(user.getName());
        newUserDto.setEmail(user.getEmail());
        newUserDto.setAge(user.getAge());
        return newUserDto;
    }

    private User mapperToUser(UserDTO userDTO){
        User newUser = new User();
        newUser.setName(userDTO.getName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setAge(userDTO.getAge());
        return newUser;
    }

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::mapperToDto)
                .collect(Collectors.toList());
    }

    public UserDTO save(UserDTO userDTO){
        User user = mapperToUser(userDTO);
        User saved = userRepository.save(user);
        return mapperToDto(saved);
    }

    public UserDTO getUser(Long id){
        User foundUser = userRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return  mapperToDto(foundUser);
    }

    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede borrar al usuario porque el ID: " + id +" no existe");
        }
        userRepository.deleteById(id);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO){
        User existingUser = userRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAge(userDTO.getAge());

        User updatedUser = userRepository.save(existingUser);
        return mapperToDto(updatedUser);
    }

    /**
     * Actualiza parcialmente un usuario existente buscando campos dinámicamente.
     * @param id ID del usuario a modificar.
     * @param fields Mapa con los nombres de los campos y sus nuevos valores.
     * @return El usuario actualizado transformado en DTO.
     */
    public UserDTO patchUser(Long id, Map<String, Object> fields) {
        // 1. BUSQUEDA: Intentamos obtener el usuario de la DB.
        // Si no existe, lanzamos un 404 (Not Found) inmediatamente.
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. ITERACIÓN: Recorremos cada entrada del mapa JSON recibido.
        fields.forEach((key, value) -> {

            // 3. REFLEXIÓN: Buscamos si existe un atributo en la clase 'User' que se llame igual que la 'key'.
            // Importante: El orden es (Clase, Nombre del campo).
            Field field = ReflectionUtils.findField(User.class, key);

            // 4. VALIDACIÓN DE CAMPO: Si el campo existe en nuestra entidad
            if (field != null) {
                // 5. ACCESIBILIDAD: Forzamos el acceso al campo (que suele ser 'private').
                field.setAccessible(true);

                try {
                    // 6. ASIGNACIÓN: Inyectamos el 'value' del JSON dentro del atributo del objeto 'existingUser'.
                    ReflectionUtils.setField(field, existingUser, value);
                } catch (Exception e) {
                    // 7. GESTIÓN DE ERRORES: Si el tipo de dato no coincide (ej: texto en un campo numérico).
                    throw new RuntimeException("Error al asignar el campo: " + key);
                }
            }
            // Si 'field' es null, simplemente se ignora (el cliente envió un campo que no existe).
        });

        // 8. PERSISTENCIA Y RETORNO:
        // - userRepository.save guarda los cambios (hace un UPDATE en MySQL).
        // - El save devuelve la entidad actualizada (User).
        // - mapperToDto convierte esa entidad final en el formato de salida para el cliente.
        return mapperToDto(userRepository.save(existingUser));
    }
}
