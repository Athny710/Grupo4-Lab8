package sw2.lab6.teletok.config;

import oracle.jrockit.jfr.openmbean.ProducerDescriptorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw2.lab6.teletok.entity.User;
import sw2.lab6.teletok.repository.UserRepository;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
public class WebServiceController {

    @Autowired
    UserRepository userRepository;

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signin(@RequestBody User u){
        HashMap<String, Object> hashMap = new HashMap<>();
        Optional<User> opt = userRepository.findByUsername(u.getUsername());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        if(opt.isPresent()){
            User us = opt.get();
            if(us.getPassword().equals(u.getPassword())){
                hashMap.put("status", "AUTHENTICATED");
                hashMap.put("token", "asldkhaslkdalskdj");
            }else{
                hashMap.put("error", "AUTH_FAILED");
            }
        }else{
            hashMap.put("error", "AUTH_FAILED");
        }
        return new ResponseEntity(hashMap, httpStatus);
    }
}
