package web.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import web.dao.UserDao;
import web.dao.RoleDAO;
import web.exceptions.UserNotFoundException;
import web.model.Role;
import web.model.User;

import java.util.*;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private UserDao userDAO;
    private RoleDAO roleDAO;


    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    
    @Autowired
    public void setUserDAO(UserDao userDAO) {
        this.userDAO = userDAO;
    }

    
    @Autowired
    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    
    @Override
    public List<User> allUsers() {
        return userDAO.allUsers();
    }

    
    @Override
    public void save(User user, Set<Role> roles) {
        user.setRoles(roles);
        setPassword(user);
        userDAO.save(user);
    }

    
    @Override
    public void delete(Long id) {
        User user = userDAO.getById(id);
        if (user == null) throw new UserNotFoundException();
        userDAO.delete(user);
    }

    
    @Override
    public User getById(Long id) {
        return userDAO.getById(id);
    }

    
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userDAO.getUserByName(username);
    }


    @Override
    public void edit(User user, List<String> roles) {
        final User userDB = userDAO.getById(user.getId());
        if (userDB == null) throw new UserNotFoundException();
        Set<Role> Setroles = new HashSet<>();
        for (String st : roles) {
            if (st.equals("ADMIN")) {
                Role role_admin = roleDAO.createRoleIfNotFound("ADMIN", 1L);
                Setroles.add(role_admin);
            }
            if (st.equals("USER")) {
                Role role_user = roleDAO.createRoleIfNotFound("USER", 2L);
                Setroles.add(role_user);
            }
        }
        user.setRoles(Setroles);
        setPassword(user);
        userDAO.save(user);
    }

    private void setPassword(User user) {
        if (user.getPassword() != null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
    }
}
