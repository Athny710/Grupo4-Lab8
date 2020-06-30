package sw2.lab6.teletok.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw2.lab6.teletok.entity.Post;

import java.util.HashMap;

@RestController
@CrossOrigin
public class WebServiceController {

    @PostMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GuardarPost(
            @RequestBody String token, @RequestBody Post post) {

        HashMap<String, Object> responseMap = new HashMap<>();



        responseMap.put("estado", "creado");
        return new ResponseEntity(responseMap, HttpStatus.CREATED);

    }

}
