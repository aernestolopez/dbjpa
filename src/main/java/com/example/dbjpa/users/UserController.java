package com.example.dbjpa.users;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    /*@GetMapping
    public List<UserDTO> getAll(){
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDTO save(@Valid @RequestBody UserDTO userDTO){
        return userService.save(userDTO);
    }*/
    @Operation(summary = "Listar todos", description = "Obtiene a todos los usuarios.")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll(){
        List<UserDTO> userDTOList = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(userDTOList);
    }
    @Operation(summary = "Buscar por ID", description = "Obtiene los detalles de un usuario específico mediante su ID numérico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content)
})
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(
            @Parameter(description = "ID del usuario a buscar", example = "1")
            @PathVariable long id){
        UserDTO foundUser = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(foundUser);
    }

    @Operation(summary = "Crear nuevo usuario", description = "Registra un nuevo usuario en la base de datos.")
    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserDTO userDTO){
        UserDTO userDTOResponse = userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTOResponse);
    }
    @Hidden
    @DeleteMapping("deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @Hidden
    @PutMapping("updateUser/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable long id, @RequestBody UserDTO userDTO){
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }
    @Hidden
    @PatchMapping("patchUser/{id}")
    public UserDTO patchUser(@PathVariable long id, @RequestBody Map<String, Object> fields){

        return userService.patchUser(id, fields);
    }
}
