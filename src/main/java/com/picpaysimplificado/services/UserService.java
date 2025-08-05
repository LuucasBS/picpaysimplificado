package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.repositores.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public void validateTransaction(User sender, BigDecimal amount) throws Exception {

        if( sender.getUserType() == UserType.MERCHANT ) {
            throw new Exception("Usuario não autorizado a realizar transações");
        }


        if(sender.getBalance().compareTo(amount) > 0 ) {
            throw new Exception("saldo insuficiente");
        }
    }

    public User findUserById(Long id) throws Exception {
        return  this.userRepository.findUserById(id).orElseThrow(() -> new Exception("Usuario nao encontrado"));
    }


    public void saveUser(User user){
        this.userRepository.save(user);
    }
}
