package sw2.lab6.teletok.config;


import oracle.jrockit.jfr.openmbean.ProducerDescriptorType;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sw2.lab6.teletok.entity.Token;
import sw2.lab6.teletok.entity.User;
import sw2.lab6.teletok.repository.TokenRepository;
import sw2.lab6.teletok.repository.UserRepository;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import sw2.lab6.teletok.entity.Post;




@RestController
@CrossOrigin
public class WebServiceController {


    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signin(@RequestBody User u){
        HashMap<String, Object> hashMap = new HashMap<>();
        Optional<User> opt = userRepository.findByUsername(u.getUsername());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String tok = null;
        if(opt.isPresent()){
            User us = opt.get();
            if(us.getPassword().equals(u.getPassword())){
                hashMap.put("status", "AUTHENTICATED");
                hashMap.put("token", "asldkhaslkdalskdj");
                httpStatus = HttpStatus.OK;
                Token token= new Token();
                token.setUser(us);
                token.setCode(tok);
                tokenRepository.save(token);
            }else{
                hashMap.put("error", "AUTH_FAILED");
            }
        }else{
            hashMap.put("error", "AUTH_FAILED");
        }
        return new ResponseEntity(hashMap, httpStatus);
    }

    @PostMapping(value = "/like/{token}/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity like(@PathVariable("token") String tok, @PathVariable("postId") int postId){

    }

    @PostMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GuardarPost(
            @RequestBody String token, @RequestBody Post post) {

        HashMap<String, Object> responseMap = new HashMap<>();



        responseMap.put("estado", "creado");
        return new ResponseEntity(responseMap, HttpStatus.CREATED);

    }

}
