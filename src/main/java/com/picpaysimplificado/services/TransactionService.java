package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositores.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    UserService userService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    public void createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());

        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.autorizeTransaction(sender, transaction.value());

        if(!isAuthorized) {
            throw new Exception("Transação nao Autorizada");
        }

        Transaction newtransaction = new Transaction();
        newtransaction.setAmount(transaction.value());
        newtransaction.setSender(sender);
        newtransaction.setReceiver(receiver);
        newtransaction.setTimeStamp(LocalDateTime.now());


        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.transactionRepository.save(newtransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

    }





    public boolean autorizeTransaction(User sender, BigDecimal value){
      ResponseEntity<Map> authorizationResponse =  restTemplate.getForEntity("https://run.mocky.io/v3/8fafdd68-a098-496f-8c9a-3442cf30dae6", Map.class);

      if(authorizationResponse.getStatusCode() == HttpStatus.OK){
          String message = (String) authorizationResponse.getBody().get("message");
          return "Autorizado".equalsIgnoreCase(message);

      } else return false;
    }
}
