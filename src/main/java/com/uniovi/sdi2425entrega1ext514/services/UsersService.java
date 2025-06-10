package com.uniovi.sdi2425entrega1ext514.services;

import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.repositories.UsersRepository;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    public void init() {
    }

    /**
     * Devuelve todos los usuarios del sistema con paginacion
     * @param pageable
     * @return
     */
    public Page<User> getUsers(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }

    /**
     * Devuelve todos los usuarios del sistema
     * @return
     */
    public List<User> getUsers() {
        return (List<User>) usersRepository.findAll();
    }


    /**
     * Devuelve los usuarios no administradores del sistema
     * @return
     */
    public List<User> getStandardUsers() {
        return ((List<User>) usersRepository.findAll()).stream().filter(u -> u.getRole().equals("ROLE_STANDARD")).collect(Collectors.toList());
    }

    /**
     * Devuelve un usuario segun su ID
     * @param id
     * @return
     */
    public User getUser(Long id) {
        return usersRepository.findById(id).get();
    }

    /**
     * Devuevle un usuario segun su DNI
     * @param dni
     * @return
     */
    public User getUserByDni(String dni) {
        return usersRepository.findByDni(dni);
    }

    /**
     * Añade un usuario a la Base de datos
     * @param user
     */
    public void addUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }

    /**
     * Guarda los cambios en un usuario
     * @param user
     */
    public void editUser(User user) {
        usersRepository.save(user);
    }


    /**
     * Genera una contraseña para el usuario
     * @return
     */
    public String generateUserPassword() {
        PasswordGenerator generator = new PasswordGenerator();

        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase, 2);
        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase, 2);
        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit, 2);

        CharacterRule specialRule = new CharacterRule(new CharacterData() {
            public String getErrorCode() { return "INSUFFICIENT_SPECIAL"; }
            public String getCharacters() { return "!@#$%^&*()-_=+[]{}|;:,.<>?"; }
        }, 2);

        return generator.generatePassword(12, Arrays.asList(upperCaseRule, lowerCaseRule, digitRule, specialRule));
    }

    /**
     * Modifica una contraseña de un usuario
     * @param user
     */
    public void changePassword(User user) {
        String newPassword = bCryptPasswordEncoder.encode(user.getPassword());
        usersRepository.updatePasswordByDni(user.getDni(), newPassword);
    }


    /**
     * Verifica que la contraseña es valida
     * @param password
     * @return
     */
    public boolean checkValidPassword(String password) {
        if(password.length() < 12) return false;

        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>-_].*");

        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
}
