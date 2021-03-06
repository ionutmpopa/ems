package ro.sci.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ro.sci.ems.dao.UserDAO;
import ro.sci.ems.domain.User;
import ro.sci.ems.exception.ValidationException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    public Collection<User> listAll() {
        return userDAO.getAll();
    }

    public Collection<User> search( String query) {
        LOGGER.debug("Searching for " + query);
        return userDAO.searchByName(query);
    }

    public boolean delete(Long id) {
        LOGGER.debug("Deleting employee for id: " + id);
        User user = null;
        try {
            user = userDAO.findById(id);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.warn("trying to fing an inexisting employee");
            return false;
        }
        if (user != null) {
            userDAO.delete(user);
            return true;
        }

        return false;
    }

    public User get(Long id) {
        LOGGER.debug("Getting employee for id: " + id);
        return userDAO.findById(id);
    }

    public User findByEmail(String email) {
        try {
            return userDAO.findByEmail(email);
        } catch (EmptyResultDataAccessException e){
            e.getStackTrace();
        }
        return null;
    }

    public void save(User user) throws ValidationException, SQLException {
        LOGGER.debug("Saving: " + user);
        validate(user);
        userDAO.update(user);
    }

    private void validate(User user) throws ValidationException {
        Date currentDate = new Date();
        List<String> errors = new LinkedList<>();
        if (StringUtils.isEmpty(user.getFirstName())) {
            errors.add("First Name is Empty");
        }

        if (StringUtils.isEmpty(user.getLastName())) {
            errors.add("Last Name is Empty");
        }

        if (!user.getPassword().equalsIgnoreCase(user.getConfirmPassword()) ||
                user.getPassword() != user.getConfirmPassword()) {
            errors.add("Password and Confirm password do not match!");
        }

        if (StringUtils.isEmpty(user.getPassword())) {
            errors.add("Password is Empty");
        }

        if (StringUtils.isEmpty(user.getEmail())) {
            errors.add("Email is Empty");

            if (!errors.isEmpty()) {
                throw new ValidationException(errors.toArray(new String[]{}));
            }
        }
    }

    public UserDAO getDao() {
        return userDAO;
    }

    public void setDao(UserDAO dao) {
        this.userDAO = dao;
    }


}